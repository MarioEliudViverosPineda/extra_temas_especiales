package servicio.extraordinario.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import servicio.extraordinario.model.PersonaEntity;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<PersonaEntity, Long> {

    /**
     *
     * @param correo
     * @return
     */
    Optional<PersonaEntity> findByCorreo(String correo);

    /**
     *
     * @param correo
     * @return
     */
    boolean existsByCorreo(String correo);
}
