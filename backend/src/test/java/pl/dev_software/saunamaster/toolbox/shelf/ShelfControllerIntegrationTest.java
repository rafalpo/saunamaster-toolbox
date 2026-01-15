package pl.dev_software.saunamaster.toolbox.shelf;

import jakarta.transaction.Transactional;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pl.dev_software.saunamaster.toolbox.model.Shelf;
import pl.dev_software.saunamaster.toolbox.model.ShelfFixture;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
@Transactional
class ShelfControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ShelfRepository shelfRepository;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void shouldReturnEmptyShelvesList() throws Exception {
        // Arrange

        // Act & Assert
        mockMvc.perform(get("/shelves"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.empty").value(Boolean.TRUE))
                .andExpect(jsonPath("$.content.length()").value(0));
    }

    @Test
    public void shouldReturnMultipleShelves() throws Exception {
        // Arrange
        shelfRepository.save(ShelfFixture.aShelf());
        shelfRepository.save(ShelfFixture.aShelf());

        // Act & Assert
        mockMvc.perform(get("/shelves"))
                .andExpect(jsonPath("$.empty").value(Boolean.FALSE))
                .andExpect(jsonPath("$.content.length()").value(2));
    }

    @Test
    public void shouldReturnMultiplePagesOfShelves() throws Exception {
        // Arrange
        final int ITEMS = 100;
        final int PAGE_SIZE = 20;
        shelfRepository.saveAll(ShelfFixture.aShelves(ITEMS));

        // Act & Assert
        int expectedPages = (ITEMS + PAGE_SIZE - 1) / PAGE_SIZE;
        mockMvc.perform(get("/shelves?size={size}", PAGE_SIZE))
                .andExpect(jsonPath("$.empty").value(Boolean.FALSE))
                .andExpect(jsonPath("$.totalElements").value(ITEMS))
                .andExpect(jsonPath("$.totalPages").value(expectedPages))
                .andExpect(jsonPath("$.content.length()").value(PAGE_SIZE));
    }

    @Test
    public void shouldReturnSingleShelfWithNoItems() throws Exception {
        // Arrange
        Shelf shelf = shelfRepository.save(ShelfFixture.aShelf());

        // Act & Assert
        mockMvc.perform(get("/shelves/{id}", shelf.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(shelf.getId().toString()))
                .andExpect(jsonPath("$.name").value(shelf.getName()))
                .andExpect(jsonPath("$.itemsCount").value(0));
    }

    @Test
    public void shouldReturnSingleShelfWithItems() throws Exception {
        // Arrange
        final int ITEMS_NUMBER = 10;
        Shelf shelf = shelfRepository.save(ShelfFixture.aShelfWithItems(ITEMS_NUMBER));

        // Act & Assert
        mockMvc.perform(get("/shelves/{id}", shelf.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(shelf.getId().toString()))
                .andExpect(jsonPath("$.name").value(shelf.getName()))
                .andExpect(jsonPath("$.itemsCount").value(shelf.getItems().size()));
    }

    @Test
    public void shouldReturnNotFoundWhenShelfWithGivenIdDoesNotExist() throws Exception {
        // Arrange
        String nonExistingId = UUID.randomUUID().toString();

        // Act & Assert
        mockMvc.perform(get("/shelves/{id}", nonExistingId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value(Matchers.containsString(nonExistingId)));
    }

    @Test
    public void shouldSaveSingleShelfWithNoItems() throws Exception {
        // Arrange
        Shelf shelf = ShelfFixture.aShelf();

        // Act & Assert
        String newResourceLocation = mockMvc.perform(
                        post("/shelves")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(shelf))
                )
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andReturn()
                .getResponse()
                .getHeader("Location");

        mockMvc.perform(get(newResourceLocation))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value(shelf.getName()))
                .andExpect(jsonPath("$.itemsCount").value(0));
    }

    @Test
    public void shouldReturnBadRequestWhenSavingShelfWithNoName() throws Exception {
        // Arrange
        Shelf shelf = ShelfFixture.aShelfWithName(null);

        // Act & Assert
        mockMvc.perform(
                        post("/shelves")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(shelf))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value(Matchers.containsString("name")));
    }

    @Test
    public void shouldReturnBadRequestWhenSavingShelfWithEmptyName() throws Exception {
        // Arrange
        Shelf shelf = ShelfFixture.aShelfWithName("");

        // Act & Assert
        mockMvc.perform(
                        post("/shelves")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(shelf))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value(Matchers.containsString("name")));
    }

    @Test
    public void shouldReturnBadRequestWhenTryingToSaveWithOtherMediaType() throws Exception {
        // Arrange
        Shelf shelf = ShelfFixture.aShelfWithRandomName();

        // Act & Assert
        mockMvc.perform(
                        post("/shelves")
                                .contentType(MediaType.TEXT_PLAIN)
                                .content(objectMapper.writeValueAsString(shelf))
                )
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(content().string(""));
    }

    @Test
    public void shouldReturnBadRequestWhenTryingToSaveNull() throws Exception {
        // Arrange
        Shelf shelf = null;

        // Act & Assert
        mockMvc.perform(
                        post("/shelves")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(shelf))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturnBadRequestWhenTryingToSaveBlankString() throws Exception {
        // Arrange
        String content = "";

        // Act & Assert
        mockMvc.perform(
                        post("/shelves")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldUpdateSingleShelf() throws Exception {
        // Arrange
        Shelf shelf = shelfRepository.save(ShelfFixture.aShelfWithRandomName());
        Shelf updatedShelf = ShelfFixture.aShelfWithName("New Name");
        updatedShelf.setId(shelf.getId());

        // Act & Assert
        mockMvc.perform(
                        put("/shelves/{id}", shelf.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updatedShelf))
                )
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/shelves/{id}", updatedShelf.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(updatedShelf.getName()));
    }

    @Test
    public void shouldUpdateSingleShelfUsingPathVariableId() throws Exception {
        // Arrange
        Shelf shelf = shelfRepository.save(ShelfFixture.aShelfWithRandomName());
        Shelf updatedShelf = ShelfFixture.aShelfWithName("New Name");
        updatedShelf.setId(UUID.randomUUID());

        // Act & Assert
        mockMvc.perform(
                        put("/shelves/{id}", shelf.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updatedShelf))
                )
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/shelves/{id}", shelf.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(updatedShelf.getName()));

        mockMvc.perform(get("/shelves/{id}", updatedShelf.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldReturnNotFoundWhenShelfWithGivenIdDoesNotExistDuringUpdate() throws Exception {
        // Arrange
        Shelf shelf = ShelfFixture.aShelf();
        String nonExistinId = UUID.randomUUID().toString();

        // Act & Assert
        mockMvc.perform(
                        put("/shelves/{id}", nonExistinId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(shelf))
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value(Matchers.containsString(nonExistinId)));
    }

    @Test
    public void shouldDeleteSingleShelfWithNoItems() throws Exception {
        // Arrange
        Shelf shelf = shelfRepository.save(ShelfFixture.aShelfWithRandomName());

        // Act & Assert
        mockMvc.perform(
                        delete("/shelves/{id}", shelf.getId())
                )
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/shelves/{id}", shelf.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldDeleteSingleShelfWithItems() throws Exception {
        // Arrange
        Shelf shelf = shelfRepository.save(ShelfFixture.aShelfWithItems(5));

        // Act & Assert
        mockMvc.perform(
                delete("/shelves/{id}", shelf.getId())
        )
        .andExpect(status().isNoContent());

        mockMvc.perform(get("/shelves/{id}", shelf.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldReturnNotFoundWhenTryingToDeleteNonExistingResource() throws Exception {
        // Arrange
        String nonExistingId = UUID.randomUUID().toString();

        // Act & Assert
        mockMvc.perform(
                        delete("/shelves/{id}", nonExistingId)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value(Matchers.containsString(nonExistingId)));
    }

    @Test
    public void shouldReturnBadRequestWhenTryingToDeleteWithInvalidUUID() throws Exception {
        // Arrange
        String invalidId = "invalid-uuid";

        // Act & Assert
        mockMvc.perform(
                        delete("/shelves/{id}", invalidId)
                )
                .andExpect(status().isBadRequest());
    }

}
