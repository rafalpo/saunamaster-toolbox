package pl.dev_software.saunamaster.toolbox.item;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.dev_software.saunamaster.toolbox.dto.ItemSummaryDTO;
import pl.dev_software.saunamaster.toolbox.dto.UsageFactDTO;
import pl.dev_software.saunamaster.toolbox.exception.ItemNotFoundException;
import pl.dev_software.saunamaster.toolbox.exception.ItemValidationException;
import pl.dev_software.saunamaster.toolbox.exception.ShelfNotFoundException;
import pl.dev_software.saunamaster.toolbox.model.Item;
import pl.dev_software.saunamaster.toolbox.model.Shelf;
import pl.dev_software.saunamaster.toolbox.model.UsageFact;
import pl.dev_software.saunamaster.toolbox.shelf.ShelfRepository;

import java.time.LocalDateTime;
import java.util.UUID;

@RequestMapping("/shelves/{shelfId}/items")
@RestController
public class ItemController {

    private final ItemRepository itemRepository;
    private final ShelfRepository shelfRepository;
    private final UsageFactRepository usageFactRepository;

    public ItemController(ItemRepository itemRepository, ShelfRepository shelfRepository, UsageFactRepository usageFactRepository) {
        this.itemRepository = itemRepository;
        this.shelfRepository = shelfRepository;
        this.usageFactRepository = usageFactRepository;
    }

    @GetMapping
    public Page<ItemSummaryDTO> getAllShelfItems(@PathVariable("shelfId") UUID shelfId, Pageable pageable) {
        return itemRepository.findByShelfId(shelfId, pageable);
    }

    @GetMapping
    @RequestMapping("/{itemId}")
    public ItemSummaryDTO getById(@PathVariable("shelfId") UUID shelfId, @PathVariable("itemId") UUID itemId) {
        return itemRepository.findByShelfIdAndItemId(shelfId, itemId)
                .orElseThrow(() -> new ItemNotFoundException(shelfId, itemId));
    }

    @PostMapping
    public ResponseEntity<Void> putNewItemOnShelf(@PathVariable("shelfId") UUID shelfId, @RequestBody Item item) {
        if (item == null || item.getName() == null || StringUtils.isBlank(item.getName())) {
            throw new ItemValidationException("name", "should not be null or blank");
        }
        Shelf shelf = shelfRepository.findById(shelfId)
            .orElseThrow(() -> new ShelfNotFoundException(shelfId));
        item.setShelf(shelf);
        Item savedItem = itemRepository.saveAndFlush(item);

        return ResponseEntity.status(HttpStatus.CREATED)
            .header("Location", String.format("/shelves/%s/items/%s", shelfId, savedItem.getId()))
            .build();
    }

    @PutMapping("/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateItemOnShelf(@PathVariable("shelfId") UUID shelfId, @PathVariable("itemId") UUID itemId, @RequestBody Item item) {
        if (item == null || item.getName() == null || StringUtils.isBlank(item.getName())) {
            throw new ItemValidationException("name", "should not be null or blank");
        }
        Item existingItem = itemRepository.findById(itemId)
            .orElseThrow(() -> new ItemNotFoundException(shelfId, itemId));
        if (!existingItem.getShelf().getId().equals(shelfId)) {
            throw new ItemNotFoundException(shelfId, itemId);
        }
        existingItem.setName(item.getName());
        itemRepository.saveAndFlush(existingItem);
    }

    @DeleteMapping("/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeItemFromShelf(@PathVariable("shelfId") UUID shelfId, @PathVariable("itemId") UUID itemId) {
        Item existingItem = itemRepository.findByIdWithAllUsageFacts(itemId)
            .orElseThrow(() -> new ItemNotFoundException(shelfId, itemId));
        if (!existingItem.getShelf().getId().equals(shelfId)) {
            throw new ItemNotFoundException(shelfId, itemId);
        }
        itemRepository.delete(existingItem);
    }

    @GetMapping("/{itemId}/usage-facts")
    public Page<UsageFactDTO> getAllUsageFacts(@PathVariable("shelfId") UUID shelfId, @PathVariable("itemId") UUID itemId, Pageable pageable) {
        return usageFactRepository.findAllByShelfIdAndItemId(shelfId, itemId, pageable);
    }

    @PostMapping("/{itemId}/usage-facts")
    @ResponseStatus(HttpStatus.CREATED)
    public void addUsageFact(@PathVariable("shelfId") UUID shelfId, @PathVariable("itemId") UUID itemId) {
        Item existingItem = itemRepository.findByIdWithAllUsageFacts(itemId)
                .orElseThrow(() -> new ItemNotFoundException(shelfId, itemId));
        UsageFact usageFact = new UsageFact();
        usageFact.setItem(existingItem);
        usageFact.setUsageTime(LocalDateTime.now());
        existingItem.getUsageFacts().add(usageFact);
        itemRepository.saveAndFlush(existingItem);
    }

}
