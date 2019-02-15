package rest.application.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception Class for all Types of Excpetions that occur in DigitlRepresentationRepository Class
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class DigitalRepositoryException extends RuntimeException {
    public DigitalRepositoryException(String message) {
        super(message);
    }
}
