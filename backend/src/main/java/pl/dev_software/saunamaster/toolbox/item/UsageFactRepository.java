package pl.dev_software.saunamaster.toolbox.item;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.dev_software.saunamaster.toolbox.dto.UsageFactDTO;
import pl.dev_software.saunamaster.toolbox.model.UsageFact;

import java.util.UUID;

public interface UsageFactRepository extends JpaRepository<UsageFact, UUID> {

    @Query("SELECT new pl.dev_software.saunamaster.toolbox.dto.UsageFactDTO(uf.id, uf.usageTime) FROM UsageFact uf"
            + " WHERE uf.item.id = :itemId AND uf.item.shelf.id = :shelfId")
    Page<UsageFactDTO> findAllByShelfIdAndItemId(UUID shelfId, UUID itemId, Pageable pageable);

    Page<UsageFact> findByItemId(UUID itemId, Pageable pageable);

}
