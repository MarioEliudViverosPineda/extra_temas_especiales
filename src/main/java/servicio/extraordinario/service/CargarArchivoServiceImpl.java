package servicio.extraordinario.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import servicio.extraordinario.model.ArchivoEntity;
import servicio.extraordinario.repository.ArchivoRepository;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.common.BitMatrix;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.JPEGFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.io.ByteArrayInputStream;
import org.apache.pdfbox.Loader;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class CargarArchivoServiceImpl implements CargarArchivoService{

    @Autowired
    private ArchivoRepository archivoRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ArchivoEntity> listarArchivos() {
        return archivoRepository.getAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void guardarArchivo(ArchivoEntity archivo, MultipartFile file) throws Exception {
        // 1. Generar el PDF modificado con el código QR (o el archivo original si no es PDF)
        byte[] archivoModificadoBytes = generarPDFConCodigoQR(file);

        // 2. Asignar los bytes modificados a tu entidad antes de guardar
        archivo.setContenido(archivoModificadoBytes);

        // CRÍTICO: Aquí se asigna el tamaño final real (con o sin QR)
        archivo.setTamano((long) archivoModificadoBytes.length);

        // 3. Guardar la entidad en la base de datos
        archivoRepository.save(archivo);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void eliminarArchivo(Long id) {
        archivoRepository.deleteById(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<ArchivoEntity> obtenerArchivoPorId(Long id) {
        return archivoRepository.findById(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean validarArchivo(MultipartFile file) {
        String tipo = file.getContentType();
        long tamañoMax = 5 * 1024 * 1024; // 5MB
        return (tipo != null &&
                (tipo.equals("application/pdf") || tipo.startsWith("image/")))
                && file.getSize() <= tamañoMax;
    }

    @Override
    public byte[] obtenerContenidoArchivo(long id){
        return archivoRepository.findById(id).get().getContenido();
    }

    /**
     * Servicio que permite generar un codigo qr e insertarlo al archivo
     *
     * @param file
     * @return
     * @throws Exception
     */
    public byte[]   generarPDFConCodigoQR(MultipartFile file) throws Exception {
        // Generar texto para QR
        String qrText = "Archivo: " + file.getOriginalFilename() + " - Fecha: " + java.time.LocalDate.now();
        byte[] qrCodeImage = generarCodigoQR(qrText);

        if (file.getContentType() != null && file.getContentType().equals("application/pdf")) {
            // Insertar código QR en la última página del PDF
            return insertarCodigoQREnUltimaPagina(file.getBytes(), qrCodeImage);
        } else {
            // Para otros tipos, no se modifica el archivo
            return file.getBytes();
        }
    }

    /**
     * Servicio que permite generar el codigo qr
     *
     * @param texto
     * @return
     * @throws WriterException
     * @throws IOException
     */
    public static byte[] generarCodigoQR(String texto) throws WriterException, IOException {
        int ancho = 200;
        int alto = 200;

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.MARGIN, 1); // margen del QR

        BitMatrix bitMatrix = qrCodeWriter.encode(texto, BarcodeFormat.QR_CODE, ancho, alto, hints);

        BufferedImage imagen = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < ancho; x++) {
            for (int y = 0; y < alto; y++) {
                int color = bitMatrix.get(x, y) ? Color.BLACK.getRGB() : Color.WHITE.getRGB();
                imagen.setRGB(x, y, color);
            }
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(imagen, "PNG", baos);
        return baos.toByteArray();
    }

    /**
     * Servicio que permite insertar el codigo qr dentro del archivo pdf
     *
     * @param pdfBytes
     * @param qrImageBytes
     * @return
     * @throws IOException
     */
    public byte[] insertarCodigoQREnUltimaPagina(byte[] pdfBytes, byte[] qrImageBytes) throws IOException {
        // 1. Cargar el documento PDF desde los bytes
        try (PDDocument document = Loader.loadPDF(pdfBytes);
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            // 2. Obtener la última página
            int pageCount = document.getNumberOfPages();
            if (pageCount == 0) {
                return pdfBytes; // Si el PDF no tiene páginas, lo devolvemos intacto
            }
            PDPage ultimaPagina = document.getPage(pageCount - 1);

            // 3. Convertir los bytes del QR en un objeto de imagen que PDFBox entienda
            ByteArrayInputStream bais = new ByteArrayInputStream(qrImageBytes);
            BufferedImage bufferedImage = ImageIO.read(bais);
            PDImageXObject qrImage = JPEGFactory.createFromImage(document, bufferedImage);

            // 4. Abrir un ContentStream en modo "APPEND" para no borrar el contenido existente de la página
            try (PDPageContentStream contentStream = new PDPageContentStream(
                    document,
                    ultimaPagina,
                    PDPageContentStream.AppendMode.APPEND,
                    true,
                    true)) {

                // 5. Definir posición y tamaño del código QR (en puntos de PDF: 72 puntos = 1 pulgada)
                float anchoQR = 100;
                float altoQR = 100;

                // Coordenadas (X, Y). El origen (0,0) es la esquina INFERIOR izquierda.
                // Esto lo colocará en la esquina inferior derecha con un margen de 20 puntos
                float x = ultimaPagina.getMediaBox().getWidth() - anchoQR - 20;
                float y = 20;

                // 6. Dibujar la imagen en el PDF
                contentStream.drawImage(qrImage, x, y, anchoQR, altoQR);
            }

            // 7. Guardar los cambios en el flujo de salida y retornar los bytes del nuevo PDF
            document.save(outputStream);
            return outputStream.toByteArray();
        }
    }
}
