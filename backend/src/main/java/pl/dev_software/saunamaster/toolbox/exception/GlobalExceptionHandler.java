package pl.dev_software.saunamaster.toolbox.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pl.dev_software.saunamaster.toolbox.dto.ErrorDTO;
import pl.dev_software.saunamaster.toolbox.dto.ErrorDTO.ErrorCode;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ShelfNotFoundException.class)
    public ErrorDTO handleShelfNotFoundException(ShelfNotFoundException ex) {
        return new ErrorDTO(ErrorCode.SHELF_NOT_FOUND, ex.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ShelfValidationException.class)
    public ErrorDTO handleShelfValidationException(ShelfValidationException ex) {
        return new ErrorDTO(ErrorCode.SHELF_VALIDATION_ERROR, ex.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ItemNotFoundException.class)
    public ErrorDTO handleItemNotFoundException(ItemNotFoundException ex) {
        return new ErrorDTO(ErrorCode.ITEM_NOT_FOUND, ex.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ItemValidationException.class)
    public ErrorDTO handleItemValidationException(ItemValidationException ex) {
        return new ErrorDTO(ErrorCode.ITEM_VALIDATION_ERROR, ex.getMessage());
    }

}
