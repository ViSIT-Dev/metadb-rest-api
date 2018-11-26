package rest.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class UpdateDigitalRepositoryException extends RuntimeException {

    public UpdateDigitalRepositoryException(String message){
        super(message);
    }

}
