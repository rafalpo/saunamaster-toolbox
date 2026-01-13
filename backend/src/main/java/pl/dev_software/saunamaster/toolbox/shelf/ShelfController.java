package pl.dev_software.saunamaster.toolbox.shelf;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;
import pl.dev_software.saunamaster.toolbox.dto.ErrorDTO;
import pl.dev_software.saunamaster.toolbox.dto.ShelfSummaryDTO;
import pl.dev_software.saunamaster.toolbox.exception.ShelfNotFoundException;
import pl.dev_software.saunamaster.toolbox.exception.ShelfValidationException;
import pl.dev_software.saunamaster.toolbox.model.Shelf;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/shelves")
public class ShelfController {

    private final ShelfRepository shelfRepository;

    public ShelfController(ShelfRepository shelfRepository) {
        this.shelfRepository = shelfRepository;
    }

    @GetMapping
    public Page<ShelfSummaryDTO> getAllShelves(Pageable pageable) {
        return shelfRepository.findAllSummaries(pageable);
    }

    @GetMapping("/{id}")
    public ShelfSummaryDTO getById(@PathVariable UUID id) {
        return shelfRepository.findSummaryById(id)
                .orElseThrow(() -> new ShelfNotFoundException(id));
    }

    @PostMapping
    public ResponseEntity<Void> saveShelf(@RequestBody Shelf shelf, UriComponentsBuilder ucb) {
        if (shelf == null || shelf.getName() == null || StringUtils.isBlank(shelf.getName())) {
            throw new ShelfValidationException("name", "should not be null or blank");
        }
        if (shelf.getId() != null) {
            shelf.setId(null);
        }
        Shelf savedShelf = shelfRepository.saveAndFlush(shelf);

        URI location = ucb.path("/shelves/{id}")
                .buildAndExpand(savedShelf.getId())
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/{id}")
    public void updateShelf(@PathVariable("id") UUID id, @RequestBody Shelf shelf) {
        Shelf existingShelf = shelfRepository.findById(id)
                .orElseThrow(() -> new ShelfNotFoundException(id));

        existingShelf.setName(shelf.getName());

        shelfRepository.saveAndFlush(existingShelf);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteShelf(@PathVariable("id") UUID id) {
        Shelf existingShelf = shelfRepository.findById(id)
                .orElseThrow(() -> new ShelfNotFoundException(id));
        shelfRepository.delete(existingShelf);
    }

}
