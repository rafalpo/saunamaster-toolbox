package pl.dev_software.saunamaster.toolbox.dto;

public record ErrorDTO(ErrorCode errorCode, String message) {

    public enum ErrorCode {
        SHELF_NOT_FOUND,
        SHELF_VALIDATION_ERROR,
        ITEM_NOT_FOUND,
        ITEM_VALIDATION_ERROR
    }

}
