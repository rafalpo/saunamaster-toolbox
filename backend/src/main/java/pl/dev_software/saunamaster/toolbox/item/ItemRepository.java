package pl.dev_software.saunamaster.toolbox.item;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.dev_software.saunamaster.toolbox.dto.ItemSummaryDTO;
import pl.dev_software.saunamaster.toolbox.model.Item;

import java.util.Optional;
import java.util.UUID;

public interface ItemRepository extends JpaRepository<Item, UUID> {

    @Query("SELECT new pl.dev_software.saunamaster.toolbox.dto.ItemSummaryDTO(i.id, i.name, COUNT(uf)) "
            + "FROM Item i LEFT JOIN i.usageFacts uf WHERE i.shelf.id = :shelfId GROUP BY i.id, i.name")
    Page<ItemSummaryDTO> findByShelfId(UUID shelfId, Pageable pageable);

    @Query("SELECT new pl.dev_software.saunamaster.toolbox.dto.ItemSummaryDTO(i.id, i.name, COUNT(uf)) "
            + "FROM Item i LEFT JOIN i.usageFacts uf WHERE i.shelf.id = :shelfId AND i.id = :itemId GROUP BY i.id, i.name")
    Optional<ItemSummaryDTO> findByShelfIdAndItemId(UUID shelfId, UUID itemId);

    @Query("SELECT i FROM Item i LEFT JOIN FETCH i.usageFacts WHERE i.id = :itemId")
    Optional<Item> findByIdWithAllUsageFacts(UUID itemId);

}
