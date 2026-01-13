package pl.dev_software.saunamaster.toolbox.shelf;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;
import pl.dev_software.saunamaster.toolbox.dto.ShelfSummaryDTO;
import pl.dev_software.saunamaster.toolbox.exception.ShelfNotFoundException;
import pl.dev_software.saunamaster.toolbox.exception.ShelfValidationException;
import pl.dev_software.saunamaster.toolbox.model.Shelf;
import pl.dev_software.saunamaster.toolbox.model.ShelfFixture;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ShelfControllerTest {

    @Mock
    private ShelfRepository shelfRepository;
    @InjectMocks
    private ShelfController shelfController;

    @Test
    public void shouldReturnPageOfShelves() {
        // Arrange
        Pageable pageable = Pageable.unpaged();
        when(shelfRepository.findAllSummaries(pageable)).thenReturn(Page.empty(pageable));

        // Act
        Page<ShelfSummaryDTO> result = shelfController.getAllShelves(pageable);

        // Assert
        verify(shelfRepository, atMostOnce()).findAllSummaries(pageable);
        assertEquals(0, result.getTotalElements());
    }

    @Test
    public void shouldReturnShelfSummaryById() {
        // Arrange
        UUID shelfId = UUID.randomUUID();
        ShelfSummaryDTO expectedShelf = new ShelfSummaryDTO(shelfId, "Test Shelf", 10L);
        when(shelfRepository.findSummaryById(shelfId)).thenReturn(Optional.of(expectedShelf));

        // Act
        ShelfSummaryDTO result = shelfController.getById(shelfId);

        // Assert
        verify(shelfRepository, atMostOnce()).findSummaryById(shelfId);
        assertEquals(expectedShelf, result);
    }

    @Test
    public void shouldThrowExceptionWhenShelfNotFoundById() {
        // Arrange
        UUID shelfId = UUID.randomUUID();
        when(shelfRepository.findSummaryById(shelfId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ShelfNotFoundException.class, () -> {
            shelfController.getById(shelfId);
        });
        verify(shelfRepository, atMostOnce()).findSummaryById(shelfId);
    }

    @Test
    public void shouldCreateNewShelfAndReturnLocation() throws URISyntaxException {
        // Arrange
        UUID generatedId = UUID.randomUUID();
        when(shelfRepository.saveAndFlush(any(Shelf.class))).thenAnswer(invocation -> {
            Shelf shelf = invocation.getArgument(0);
            shelf.setId(generatedId);
            return shelf;
        });
        Shelf shelf = ShelfFixture.aShelfWithRandomName();

        // Act
        var response = shelfController.saveShelf(shelf, UriComponentsBuilder.newInstance());

        // Assert
        verify(shelfRepository, atMostOnce()).saveAndFlush(any(Shelf.class));
        assertNotNull(response.getHeaders().getFirst("Location"));
        assertTrue(response.getHeaders().getFirst("Location").contains(generatedId.toString()));
    }

    @Test
    public void shouldThrowValidationExceptionWhenSavingShelfWithNullName() {
        // Arrange
        Shelf shelf = new Shelf();
        shelf.setName(null);

        // Act & Assert
        ShelfValidationException ex = assertThrows(ShelfValidationException.class, () -> {
            shelfController.saveShelf(shelf, UriComponentsBuilder.newInstance());
        });
        verify(shelfRepository, never()).saveAndFlush(any(Shelf.class));
        assertEquals("name", ex.getFieldName());
    }

    @Test
    public void shouldThrowValidationExceptionWhenSavingShelfWithBlankName() {
        // Arrange
        Shelf shelf = new Shelf();
        shelf.setName("   ");

        // Act & Assert
        ShelfValidationException ex = assertThrows(ShelfValidationException.class, () -> {
            shelfController.saveShelf(shelf, UriComponentsBuilder.newInstance());
        });
        verify(shelfRepository, never()).saveAndFlush(any(Shelf.class));
        assertEquals("name", ex.getFieldName());
    }

    @Test
    public void shouldUpdateShelfName() {
        // Arrange
        UUID shelfId = UUID.randomUUID();
        Shelf existingShelf = ShelfFixture.aShelfWithName("Old Name");
        when(shelfRepository.findById(shelfId)).thenReturn(Optional.of(existingShelf));

        Shelf updatedShelf = new Shelf();
        updatedShelf.setName("New Name");

        // Act
        shelfController.updateShelf(shelfId, updatedShelf);

        // Assert
        verify(shelfRepository, atMostOnce()).findById(shelfId);
        verify(shelfRepository, atMostOnce()).saveAndFlush(existingShelf);
        assertEquals("New Name", existingShelf.getName());
    }

    @Test
    public void shouldThrowExceptionWhenUpdatingNonExistingShelf() {
        // Arrange
        UUID shelfId = UUID.randomUUID();
        when(shelfRepository.findById(shelfId)).thenReturn(Optional.empty());

        Shelf updatedShelf = new Shelf();
        updatedShelf.setName("New Name");

        // Act & Assert
        assertThrows(ShelfNotFoundException.class, () -> {
            shelfController.updateShelf(shelfId, updatedShelf);
        });
        verify(shelfRepository, atMostOnce()).findById(shelfId);
        verify(shelfRepository, never()).saveAndFlush(any(Shelf.class));
    }

    @Test
    public void shouldDeleteShelf() {
        // Arrange
        UUID shelfId = UUID.randomUUID();
        Shelf existingShelf = ShelfFixture.aShelfWithRandomName();
        when(shelfRepository.findById(shelfId)).thenReturn(Optional.of(existingShelf));

        // Act
        shelfController.deleteShelf(shelfId);

        // Assert
        verify(shelfRepository, atMostOnce()).findById(shelfId);
        verify(shelfRepository, atMostOnce()).delete(existingShelf);
    }

    @Test
    public void shouldThrowExceptionWhenDeletingNonExistingShelf() {
        // Arrange
        UUID shelfId = UUID.randomUUID();
        when(shelfRepository.findById(shelfId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ShelfNotFoundException.class, () -> {
            shelfController.deleteShelf(shelfId);
        });
        verify(shelfRepository, atMostOnce()).findById(shelfId);
        verify(shelfRepository, never()).delete(any(Shelf.class));
    }

}
