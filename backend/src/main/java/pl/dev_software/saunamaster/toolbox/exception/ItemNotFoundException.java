package pl.dev_software.saunamaster.toolbox.exception;

import java.util.UUID;

public class ItemNotFoundException extends RuntimeException {

    public ItemNotFoundException(UUID shelfId, UUID itemId) {
        super("Item with id " + itemId + " cannot be found on shelf with id " + shelfId);
    }

}
