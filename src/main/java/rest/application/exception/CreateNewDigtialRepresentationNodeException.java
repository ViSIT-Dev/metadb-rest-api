package rest.application.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class CreateNewDigtialRepresentationNodeException extends RuntimeException {

    public CreateNewDigtialRepresentationNodeException(String message) {
        super(message);
    }
}
