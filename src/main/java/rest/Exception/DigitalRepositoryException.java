package rest.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class DigitalRepositoryException extends RuntimeException {
    public DigitalRepositoryException(String message){
        super(message);
    }
}
