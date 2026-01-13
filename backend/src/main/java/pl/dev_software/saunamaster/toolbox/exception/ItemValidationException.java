package pl.dev_software.saunamaster.toolbox.exception;

import lombok.Data;

@Data
public class ItemValidationException extends IllegalArgumentException {

    private final String fieldName;
    private final String errorMessage;

    public ItemValidationException(String fieldName, String errorMessage) {
        super("Shelf validation error, field: " + fieldName + " not passed validation with message: " + errorMessage);
        this.fieldName = fieldName;
        this.errorMessage = errorMessage;
    }

}
