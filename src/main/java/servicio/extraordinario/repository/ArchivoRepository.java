package servicio.extraordinario.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import servicio.extraordinario.model.ArchivoEntity;

import java.util.List;

@Repository
public interface ArchivoRepository extends JpaRepository<ArchivoEntity, Long> {

    @Query("""
            Select ae from ArchivoEntity ae
            """)
    List<ArchivoEntity> getAll();

}
