package rest.application.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by Manu on 20.02.19.
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class MetadataQueryException extends Exception {

    public MetadataQueryException(String message) {
        super(message);
    }
}
