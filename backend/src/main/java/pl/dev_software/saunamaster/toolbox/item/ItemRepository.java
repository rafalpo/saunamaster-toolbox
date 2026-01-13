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

    @Query("SELECT new pl.dev_software.saunamaster.toolbox.dto.ItemSummaryDTO(i.id, i.name) FROM Item i WHERE i.shelf.id = :shelfId")
    Page<ItemSummaryDTO> findByShelfId(UUID shelfId, Pageable pageable);

    @Query("SELECT new pl.dev_software.saunamaster.toolbox.dto.ItemSummaryDTO(i.id, i.name) FROM Item i WHERE i.shelf.id = :shelfId AND i.id = :itemId")
    Optional<ItemSummaryDTO> findByShelfIdAndItemId(UUID shelfId, UUID itemId);

}
