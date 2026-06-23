package servicio.extraordinario.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "archivo")
@Setter
@Getter
public class ArchivoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_archivo")
    private Long idArchivo;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "tipo")
    private String tipo;

    @Column(name = "tamano")
    private long tamano;

    @Column(name = "contenido", columnDefinition = "bytea")
    private byte[] contenido;
}
