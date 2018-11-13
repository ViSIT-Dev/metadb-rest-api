package rest.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception Class for all Types of Excpetions that occur in DigitlRepresentationRepository Class
 */
// TODO Evtl. eine schlechte Error-message zum Zurückgeben. 404 wird auch geworfen, wenn der angefragte REST Pfad nicht besteht.
@ResponseStatus(HttpStatus.NOT_FOUND)
public class DigitalRepositoryException extends RuntimeException {
    public DigitalRepositoryException(String message){
        super(message);
    }
}
