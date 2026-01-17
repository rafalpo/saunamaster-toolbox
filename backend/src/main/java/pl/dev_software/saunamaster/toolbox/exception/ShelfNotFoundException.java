package pl.dev_software.saunamaster.toolbox.exception;

import java.util.UUID;

public class ShelfNotFoundException extends RuntimeException {

    public ShelfNotFoundException(UUID id) {
        super("Shelf with id " + id + " not found");
    }
}
