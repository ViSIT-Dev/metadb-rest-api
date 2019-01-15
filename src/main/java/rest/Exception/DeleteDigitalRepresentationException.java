package rest.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excpetion Class for a failed Search Query on the ObjectRepository
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class DeleteDigitalRepresentationException extends RuntimeException {
    public DeleteDigitalRepresentationException(String message) {
        super(message);
    }

}
