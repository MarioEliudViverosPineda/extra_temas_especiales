package servicio.extraordinario.service;

import org.springframework.web.multipart.MultipartFile;
import servicio.extraordinario.model.ArchivoEntity;

import java.util.List;
import java.util.Optional;

public interface CargarArchivoService {

    /**
     * Servicio que permite listar los archivos
     *
     * @return
     */
    List<ArchivoEntity> listarArchivos();

    /**
     * Servicio que permite guardar un archivo.
     *
     * @param archivo
     */
    void guardarArchivo(ArchivoEntity archivo, MultipartFile file) throws Exception;

    /**
     * Servicio que permite eliminar el archivo por medio del identificador
     *
     * @param id
     */
    void eliminarArchivo(Long id);

    /**
     * Servicio que permite obtener el archivo por medio del identificador
     *
     * @param id
     * @return
     */
    Optional<ArchivoEntity> obtenerArchivoPorId(Long id);

    /**
     * Servicio que permite validar el archivo
     *
     * @param file
     * @return
     */
    boolean validarArchivo(MultipartFile file);

    /**
     *
     * @param id
     * @return
     */
    byte[] obtenerContenidoArchivo(long id);
}
