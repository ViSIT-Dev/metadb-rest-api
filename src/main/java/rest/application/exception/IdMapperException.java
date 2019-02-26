package rest.application.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import rest.persistence.util.IdMapper;

/**
 * Exception which is used in the IdMapper process. Issues a conflict that has been done during an import process.
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class IdMapperException extends Exception {

    public IdMapperException(String message) {
        super(message);
    }
}
