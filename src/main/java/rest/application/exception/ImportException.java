package rest.application.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ImportException extends Exception {

    public ImportException(String message) {
        super(message);
    }
}
