package pl.dev_software.saunamaster.toolbox.item;

import jakarta.transaction.Transactional;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pl.dev_software.saunamaster.toolbox.model.*;
import pl.dev_software.saunamaster.toolbox.shelf.ShelfRepository;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
@Transactional
class ItemControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UsageFactRepository usageFactRepository;
    @Autowired
    private ObjectMapper objectMapper;

    private Shelf shelf;

    @BeforeEach
    public void setUp(@Autowired ShelfRepository shelfRepository) {
        shelf = shelfRepository.save(ShelfFixture.aShelfWithRandomName());
    }

    @Test
    public void shouldReturnEmptyListOfItemsOnShelf() throws Exception {
        // Arrange

        // Act & Assert
        mockMvc.perform(get("/shelves/{shelfId}/items", shelf.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.empty").value(Boolean.TRUE))
                .andExpect(jsonPath("$.content.length()").value(0));
    }

    @Test
    public void shouldReturnOneItemOnShelf() throws Exception {
        // Arrange
        Item item = ItemFixture.anItemWithRandomName();
        item.setShelf(shelf);
        itemRepository.save(item);

        // Act & Assert
        mockMvc.perform(get("/shelves/{shelfId}/items", shelf.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.empty").value(Boolean.FALSE))
                .andExpect(jsonPath("$.content.length()").value(1));
    }

    @Test
    public void shouldReturnPaginatedListOfItemsOnShelf() throws Exception {
        // Arrange
        final int ITEMS = 100;
        final int PAGE_SIZE = 20;
        itemRepository.saveAll(ItemFixture.anItemsOnShelf(shelf, ITEMS));

        // Act & Assert
        int expectedPages = (ITEMS + PAGE_SIZE - 1) / PAGE_SIZE;
        mockMvc.perform(get("/shelves/{shelfId}/items?page=0&size={pageSize}", shelf.getId(), PAGE_SIZE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.empty").value(Boolean.FALSE))
                .andExpect(jsonPath("$.totalElements").value(ITEMS))
                .andExpect(jsonPath("$.totalPages").value(expectedPages))
                .andExpect(jsonPath("$.content.length()").value(PAGE_SIZE));
    }

    @Test
    public void shouldReturnSingleItemById() throws Exception {
        // Arrange
        Item item = ItemFixture.anItemWithRandomName();
        item.setShelf(shelf);
        Item savedItem = itemRepository.save(item);

        // Act & Assert
        mockMvc.perform(get("/shelves/{shelfId}/items/{itemId}", shelf.getId(), savedItem.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedItem.getId().toString()))
                .andExpect(jsonPath("$.name").value(savedItem.getName()));
    }

    @Test
    public void shouldReturnNotFoundWhenRequestedCrossShelfItem(@Autowired ShelfRepository shelfRepository) throws Exception {
        // Arrange
        Shelf anotherShelf = shelfRepository.save(ShelfFixture.aShelfWithRandomName());
        Item item = ItemFixture.anItemWithRandomName();
        item.setShelf(anotherShelf);
        Item savedItem = itemRepository.save(item);

        // Act & Assert
        mockMvc.perform(get("/shelves/{shelfId}/items/{itemId}", shelf.getId(), savedItem.getId()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value(Matchers.containsString(savedItem.getId().toString())));
    }

    @Test
    public void shouldReturnNotFoundWhenRequestedNotExistingItem() throws Exception {
        // Arrange
        UUID nonExistingItemId = UUID.randomUUID();

        // Act & Assert
        mockMvc.perform(get("/shelves/{shelfId}/items/{itemId}", shelf.getId(), nonExistingItemId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value(Matchers.containsString(nonExistingItemId.toString())));
    }

    @Test
    public void shouldSaveSingleItem() throws Exception {
        // Arrange
        Item item = ItemFixture.anItemWithRandomName();

        // Act & Assert
        String newResourceLocation = mockMvc.perform(
                        post("/shelves/{shelfId}/items", shelf.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(item))
                )
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andReturn()
                .getResponse()
                .getHeader("Location");

        mockMvc.perform(get(newResourceLocation))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(item.getName()));
    }

    @Test
    public void shouldReturnBadRequestWhenSavingItemWithEmptyName() throws Exception {
        // Arrange
        Item item = ItemFixture.anItemWithName("");

        // Act & Assert
        mockMvc.perform(
                        post("/shelves/{shelfId}/items", shelf.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(item))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value(Matchers.containsString("name")));
    }

    @Test
    public void shouldReturnBadRequestWhenSavingItemWithNulledName() throws Exception {
        // Arrange
        Item item = ItemFixture.anItemWithName(null);

        // Act & Assert
        mockMvc.perform(
                        post("/shelves/{shelfId}/items", shelf.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(item))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value(Matchers.containsString("name")));
    }

    @Test
    public void shouldReturnNotFoundWhenSavingItemOnNotExistingShelf() throws Exception {
        // Arrange
        UUID nonExistingShelfId = UUID.randomUUID();
        Item item = ItemFixture.anItemWithRandomName();

        // Act & Assert
        mockMvc.perform(
                        post("/shelves/{shelfId}/items", nonExistingShelfId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(item))
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value(Matchers.containsString(nonExistingShelfId.toString())));
    }

    @Test
    public void shouldReturnBadRequestWhenRequestedSaveWithEmptyBody() throws Exception {
        // Arrange

        // Act & Assert
        mockMvc.perform(post("/shelves/{shelfId}/items", shelf.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("") // Empty body
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturnUnsupportedMediaTypeWhenRequestedSaveWithWrongMediaType() throws Exception {
        // Assert
        Item item = ItemFixture.anItemWithRandomName();
        item.setShelf(shelf);

        // Act & Assert
        mockMvc.perform(
                        post("/shelves/{shelfId}/items", shelf.getId())
                                .contentType(MediaType.TEXT_PLAIN)
                                .content(objectMapper.writeValueAsString(item))
                )
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    public void shouldUpdateSingleItem() throws Exception {
        // Arrange
        Item item = ItemFixture.anItemWithRandomName();
        item.setShelf(shelf);
        Item savedItem = itemRepository.save(item);

        Item updatedItem = ItemFixture.anItemWithRandomName();
        updatedItem.setId(item.getId());
        updatedItem.setShelf(shelf);

        // Act & Assert
        mockMvc.perform(
                        put("/shelves/{shelfId}/items/{itemId}", shelf.getId(), savedItem.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updatedItem))
                )
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/shelves/{shelfId}/items/{itemId}", shelf.getId(), savedItem.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(updatedItem.getName()));
    }

    @Test
    public void shouldReturnNotFountWhenRequestedUpdateOnNonExistingShelf() throws Exception {
        // Arrange
        UUID nonExistingShelfId = UUID.randomUUID();
        Item item = ItemFixture.anItemWithRandomName();
        item.setShelf(shelf);
        Item savedItem = itemRepository.save(item);
        Item updatedItem = ItemFixture.anItemWithRandomName();
        updatedItem.setId(savedItem.getId());

        // Act & Assert
        mockMvc.perform(
                        put("/shelves/{shelfId}/items/{itemId}", nonExistingShelfId, savedItem.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updatedItem))
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value(Matchers.containsString(nonExistingShelfId.toString())));
    }

    @Test
    public void shouldReturnNotFountWhenRequestedUpdateOnNonExistingItem() throws Exception {
        // Arrange
        UUID nonExistingItemId = UUID.randomUUID();
        Item updatedItem = ItemFixture.anItemWithRandomName();
        updatedItem.setId(nonExistingItemId);

        // Act & Assert
        mockMvc.perform(
                        put("/shelves/{shelfId}/items/{itemId}", shelf.getId(), nonExistingItemId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updatedItem))
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value(Matchers.containsString(nonExistingItemId.toString())));
    }

    @Test
    public void shouldReturnUnsupportedMediaTypeWhenSentWrongMediaType() throws Exception {
        // Arrange
        Item item = ItemFixture.anItemWithRandomName();
        item.setShelf(shelf);
        Item savedItem = itemRepository.save(item);
        Item updatedItem = ItemFixture.anItemWithRandomName();
        updatedItem.setId(savedItem.getId());

        // Act & Assert
        mockMvc.perform(
                        put("/shelves/{shelfId}/items/{itemId}", shelf.getId(), savedItem.getId())
                                .contentType(MediaType.TEXT_PLAIN)
                                .content(objectMapper.writeValueAsString(updatedItem))
                )
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    public void shouldReturnNotFoundWhenTryingToUpdateCrossShelfItem(@Autowired ShelfRepository shelfRepository) throws Exception {
        // Arrange
        Shelf anotherShelf = shelfRepository.save(ShelfFixture.aShelfWithRandomName());
        Item item = ItemFixture.anItemWithRandomName();
        item.setShelf(anotherShelf);
        Item savedItem = itemRepository.save(item);

        Item updatedItem = ItemFixture.anItemWithRandomName();
        updatedItem.setId(savedItem.getId());

        // Act & Assert
        mockMvc.perform(
                        put("/shelves/{shelfId}/items/{itemId}", shelf.getId(), savedItem.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updatedItem))
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value(Matchers.containsString(savedItem.getId().toString())));
    }

    @Test
    public void shouldDeleteSingleItem() throws Exception {
        // Arrange
        Item item = ItemFixture.anItemWithRandomName();
        item.setShelf(shelf);
        Item savedItem = itemRepository.save(item);

        // Act & Assert
        mockMvc.perform(
                        delete("/shelves/{shelfId}/items/{itemId}", shelf.getId(), savedItem.getId())
                )
                .andExpect(status().isNoContent());

        mockMvc.perform(
                        get("/shelves/{shelfId}/items/{itemId}", shelf.getId(), savedItem.getId())
                )
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldDeleteSingleItemWithUsageFacts() throws Exception {
        // Arrange
        Item item = ItemFixture.anItemWithRandomName();
        item.setShelf(shelf);
        Item savedItem = itemRepository.saveAndFlush(item);
        UsageFact usageFact = new UsageFact();
        usageFact.setUsageTime(LocalDateTime.now());
        usageFact.setItem(savedItem);
        savedItem.getUsageFacts().add(usageFact);
        usageFactRepository.saveAndFlush(usageFact);

        // Act & Assert
        mockMvc.perform(
                        delete("/shelves/{shelfId}/items/{itemId}", shelf.getId(), savedItem.getId())
                )
                .andExpect(status().isNoContent());

        mockMvc.perform(
                        get("/shelves/{shelfId}/items/{itemId}", shelf.getId(), savedItem.getId())
                )
                .andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldReturnNotFoundWhenTryingToDeleteItemCrossShelf(@Autowired ShelfRepository shelfRepository) throws Exception {
        // Arrange
        Shelf anotherShelf = shelfRepository.save(ShelfFixture.aShelfWithName("Another Shelf"));

        Item item = ItemFixture.anItemWithRandomName();
        item.setShelf(anotherShelf);
        Item savedItem = itemRepository.save(item);

        // Act & Assert
        mockMvc.perform(
                        delete("/shelves/{shelfId}/items/{itemId}", shelf.getId(), savedItem.getId())
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value(Matchers.containsString(savedItem.getId().toString())));
    }

    @Test
    public void shouldReturnNotFoundWhenTryingToDeleteItemWithNotExisitingId() throws Exception {
        // Arrange
        UUID nonExistingItemId = UUID.randomUUID();

        // Act & Assert
        mockMvc.perform(
                        delete("/shelves/{shelfId}/items/{itemId}", shelf.getId(), nonExistingItemId)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value(Matchers.containsString(nonExistingItemId.toString())));
    }

    @Test
    public void shouldReturnBadRequestWhenDeleteMalformedId() throws Exception {
        // Arrange
        String malformedItemId = "malformed-uuid";

        // Act & Assert
        mockMvc.perform(
                        delete("/shelves/{shelfId}/items/{itemId}", shelf.getId(), malformedItemId)
                )
                .andExpect(status().isBadRequest());
    }

}
