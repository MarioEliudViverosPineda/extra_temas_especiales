package servicio.extraordinario.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import servicio.extraordinario.model.ArchivoEntity;
import servicio.extraordinario.service.CargarArchivoService;

import java.util.Optional;


@Controller
@RequestMapping("/archivos")
@Validated
@RequiredArgsConstructor
@Slf4j
public class CargarArchivoController {

    private final CargarArchivoService cargarArchivoService;

    /**
     * Servicio que permite consultar los archivos guardados
     *
     * @param model
     * @return
     */
    @GetMapping
    public String listarArchivos(Model model) {
        model.addAttribute("archivos", cargarArchivoService.listarArchivos());
        return "archivos/lista";
    }

    /**
     * Servicio que permite cargar los archivos
     *
     * @return
     */
    @GetMapping("/nuevo")
    public String mostrarFormularioCarga() {
        return "archivos/nuevo";
    }

    /**
     * Servicio que permite cargar archivos mandados por el usuario
     *
     * @param file
     * @param model
     * @return
     */
    @PostMapping("/cargar")
    public String cargarArchivo(@RequestParam("file") MultipartFile file, Model model) throws Exception {

        if (!cargarArchivoService.validarArchivo(file)) {
            model.addAttribute("error",
                    "Archivo no válido. Solo PDF o imágenes menores a 5MB.");
            return "archivos/nuevo";
        }
        // El controlador solo crea la entidad y asigna datos básicos que vienen del cliente
        ArchivoEntity archivo = new ArchivoEntity();
        archivo.setNombre(file.getOriginalFilename());
        archivo.setTipo(file.getContentType());

        // El servicio se encargará de procesar el archivo, calcular el tamaño final y guardar
        cargarArchivoService.guardarArchivo(archivo, file);

        return "redirect:/archivos";
    }

    /**
     * Elimina uno de los archivos cargados
     *
     * @param id
     * @return
     */
    @GetMapping("/eliminar/{id}")
    public String eliminarArchivo(@PathVariable Long id) {
        cargarArchivoService.eliminarArchivo(id);
        return "redirect:/archivos";
    }



    /**
     * Permite visualizar un archivo PDF 
     *
     * @param id
     * @return ResponseEntity con el contenido del PDF y tipo de contenido
     */
    @GetMapping("/ver/{id}")
    public ResponseEntity<byte[]> verArchivoPDF(@PathVariable Long id) {
        Optional<ArchivoEntity> archivoOpt = cargarArchivoService.obtenerArchivoPorId(id);
        if (archivoOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        ArchivoEntity archivo = archivoOpt.get();
        byte[] contenidoArchivo = cargarArchivoService.obtenerContenidoArchivo(id);

        if (contenidoArchivo == null || contenidoArchivo.length == 0) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + archivo.getNombre() + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(contenidoArchivo);
    }

}
