package pl.dev_software.saunamaster.toolbox.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.dev_software.saunamaster.toolbox.dto.ItemSummaryDTO;
import pl.dev_software.saunamaster.toolbox.exception.ItemNotFoundException;
import pl.dev_software.saunamaster.toolbox.exception.ItemValidationException;
import pl.dev_software.saunamaster.toolbox.model.Item;
import pl.dev_software.saunamaster.toolbox.model.ItemFixture;
import pl.dev_software.saunamaster.toolbox.model.Shelf;
import pl.dev_software.saunamaster.toolbox.model.ShelfFixture;
import pl.dev_software.saunamaster.toolbox.shelf.ShelfRepository;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemControllerTest {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private ShelfRepository shelfRepository;
    @InjectMocks
    private ItemController itemController;

    @Test
    public void shouldReturnPageOfItemsForShelf() {
        // Arrange
        UUID shelfId = UUID.randomUUID();
        Pageable pageable = Pageable.unpaged();
        when(itemRepository.findByShelfId(shelfId, pageable)).thenReturn(Page.empty(pageable));

        // Act
        Page<ItemSummaryDTO> items = itemController.getAllShelfItems(shelfId, pageable);

        // Assert
        verify(itemRepository, atMostOnce()).findByShelfId(shelfId, pageable);
        assertEquals(0, items.getTotalElements());
    }

    @Test
    public void shouldReturnItemSummaryByShelfIdAndItemId() {
        // Arrange
        UUID shelfId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();
        ItemSummaryDTO itemSummaryDTO = new ItemSummaryDTO(itemId, "Test Item");
        when(itemRepository.findByShelfIdAndItemId(shelfId, itemId)).thenReturn(Optional.of(itemSummaryDTO));

        // Act
        ItemSummaryDTO result = itemController.getById(shelfId, itemId);

        // Assert
        assertEquals(itemSummaryDTO, result);
        verify(itemRepository, atMostOnce()).findByShelfIdAndItemId(shelfId, itemId);
    }

    @Test
    public void shouldThrowExceptionWhenItemNotFoundOnShelf() {
        // Arrange
        UUID shelfId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();
        when(itemRepository.findByShelfIdAndItemId(shelfId, itemId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ItemNotFoundException.class, () -> {
            itemController.getById(shelfId, itemId);
        });
        verify(itemRepository, atMostOnce()).findByShelfIdAndItemId(shelfId, itemId);
    }

    @Test
    public void shouldSaveNewItemOnExistingShelf() {
        // Arrange
        UUID shelfId = UUID.randomUUID();
        Item item = ItemFixture.anItemWithName("New Item");
        Shelf shelf = ShelfFixture.aShelfWithRandomName();
        when(shelfRepository.findById(shelfId)).thenReturn(Optional.of(shelf));
        when(itemRepository.saveAndFlush(any(Item.class))).thenAnswer(invocation -> {
            Item savedItem = invocation.getArgument(0);
            savedItem.setId(UUID.randomUUID());
            return savedItem;
        });

        // Act
        itemController.putNewItemOnShelf(shelfId, item);

        // Assert
        verify(shelfRepository, atMostOnce()).findById(shelfId);
        verify(itemRepository, atMostOnce()).saveAndFlush(any(Item.class));
    }

    @Test
    public void shouldThrowExceptionWhenShelfDoesNotExistOnItemCreation() {
        // Arrange
        UUID shelfId = UUID.randomUUID();
        Item item = new Item();

        // Act & Assert
        ItemValidationException ex = assertThrows(ItemValidationException.class, () -> {
            itemController.putNewItemOnShelf(shelfId, item);
        });
        verifyNoInteractions(itemRepository, shelfRepository);
        assertEquals("name", ex.getFieldName());
    }

    @Test
    public void shouldThrowValidationExceptionWhenItemNameIsBlank() {
        // Arrange
        UUID shelfId = UUID.randomUUID();
        Item item = new Item();
        item.setName("      ");

        // Act & Assert
        ItemValidationException ex = assertThrows(ItemValidationException.class, () -> {
            itemController.putNewItemOnShelf(shelfId, item);
        });
        assertEquals("name", ex.getFieldName());
        verifyNoInteractions(itemRepository);
    }

    @Test
    public void shouldThrowValidationExceptionWhenSavingItemWithEmptyName() {
        // Arrange
        UUID shelfId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();
        Item item = ItemFixture.anItemWithName(null);
        item.setId(itemId);
        Shelf shelf = ShelfFixture.aShelfWithRandomName();
        shelf.setId(shelfId);
        item.setShelf(shelf);

        // Act & Assert
        ItemValidationException ex = assertThrows(ItemValidationException.class, () -> {
            itemController.putNewItemOnShelf(shelfId, item);
        });
        assertEquals("name", ex.getFieldName());
        verify(shelfRepository, never()).findById(shelfId);
        verify(itemRepository, never()).saveAndFlush(any(Item.class));
    }

    @Test
    public void shouldUpdateItemNameWhenItBelongsToShelf() {
        // Arrange
        UUID shelfId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();
        Item existingItem = ItemFixture.anItemWithName("Old Name");
        existingItem.setId(itemId);
        Shelf shelf = ShelfFixture.aShelfWithRandomName();
        shelf.setId(shelfId);
        existingItem.setShelf(shelf);

        Item updatedItem = ItemFixture.anItemWithName("Updated Name");

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));

        // Act
        itemController.updateItemOnShelf(shelfId, itemId, updatedItem);

        // Assert
        verify(itemRepository, atMostOnce()).findById(itemId);
        verify(itemRepository, atMostOnce()).saveAndFlush(existingItem);
        assertEquals("Updated Name", existingItem.getName());
    }

    @Test
    public void shouldThrowExceptionWhenUpdatingItemThatBelongsToDifferentShelf() {
        // Arrange
        UUID shelfId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();
        Item existingItem = ItemFixture.anItemWithName("Old Name");
        existingItem.setId(itemId);
        Shelf differentShelf = ShelfFixture.aShelfWithRandomName();
        differentShelf.setId(UUID.randomUUID());
        existingItem.setShelf(differentShelf);

        Item updatedItem = ItemFixture.anItemWithName("Updated Name");

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));

        // Act & Assert
        assertThrows(ItemNotFoundException.class, () -> {
            itemController.updateItemOnShelf(shelfId, itemId, updatedItem);
        });
        verify(itemRepository, atMostOnce()).findById(itemId);
        verify(itemRepository, never()).saveAndFlush(any(Item.class));
    }

    @Test
    public void shouldThrowExceptionWhenUpdatingNonExistingItem() {
        // Arrange
        UUID shelfId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();
        Item updatedItem = ItemFixture.anItemWithName("Updated Name");

        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ItemNotFoundException.class, () -> {
            itemController.updateItemOnShelf(shelfId, itemId, updatedItem);
        });
        verify(itemRepository, atMostOnce()).findById(itemId);
        verify(itemRepository, never()).saveAndFlush(any(Item.class));
    }

    @Test
    public void shouldThrowValidationExceptionWhenUpdatingItemWithBlankName() {
        // Arrange
        UUID shelfId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();
        Item updatedItem = ItemFixture.anItemWithName("   ");

        // Act & Assert
        ItemValidationException ex = assertThrows(ItemValidationException.class, () -> {
            itemController.updateItemOnShelf(shelfId, itemId, updatedItem);
        });
        assertEquals("name", ex.getFieldName());
        verify(itemRepository, never()).findById(any(UUID.class));
        verify(itemRepository, never()).saveAndFlush(any(Item.class));
    }

    @Test
    public void shouldDeleteItemWhenItBelongsToShelf() {
        // Arrange
        UUID shelfId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();
        Item existingItem = ItemFixture.anItemWithName("Item to Delete");
        existingItem.setId(itemId);
        Shelf shelf = ShelfFixture.aShelfWithRandomName();
        shelf.setId(shelfId);
        existingItem.setShelf(shelf);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));

        // Act
        itemController.removeItemFromShelf(shelfId, itemId);

        // Assert
        verify(itemRepository, atMostOnce()).findById(itemId);
        verify(itemRepository, atMostOnce()).delete(existingItem);
    }

    @Test
    public void shouldThrowExceptionWhenDeletingItemThatBelongsToDifferentShelf() {
        // Arrange
        UUID shelfId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();
        Item existingItem = ItemFixture.anItemWithName("Item to Delete");
        existingItem.setId(itemId);
        Shelf differentShelf = ShelfFixture.aShelfWithRandomName();
        differentShelf.setId(UUID.randomUUID());
        existingItem.setShelf(differentShelf);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));

        // Act & Assert
        assertThrows(ItemNotFoundException.class, () -> {
            itemController.removeItemFromShelf(shelfId, itemId);
        });
        verify(itemRepository, atMostOnce()).findById(itemId);
        verify(itemRepository, never()).delete(any(Item.class));
    }

}
