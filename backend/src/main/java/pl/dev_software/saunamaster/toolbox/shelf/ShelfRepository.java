package pl.dev_software.saunamaster.toolbox.shelf;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.dev_software.saunamaster.toolbox.dto.ShelfSummaryDTO;
import pl.dev_software.saunamaster.toolbox.model.Shelf;

import java.util.Optional;
import java.util.UUID;

public interface ShelfRepository extends JpaRepository<Shelf, UUID> {

    @Query("SELECT new pl.dev_software.saunamaster.toolbox.dto.ShelfSummaryDTO(s.id, s.name, COUNT(i)) " +
            "FROM Shelf s LEFT JOIN s.items i GROUP BY s.id, s.name")
    Page<ShelfSummaryDTO> findAllSummaries(Pageable pageable);

    @Query("SELECT new pl.dev_software.saunamaster.toolbox.dto.ShelfSummaryDTO(s.id, s.name, COUNT(i)) " +
            "FROM Shelf s LEFT JOIN s.items i WHERE s.id=:id GROUP BY s.id, s.name")
    Optional<ShelfSummaryDTO> findSummaryById(@Param("id") UUID id);

}
