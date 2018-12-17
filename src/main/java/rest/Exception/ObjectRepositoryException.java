package rest.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import rest.persistence.repository.ObjectRepository;

/**
 * Excpetion Class for a failed Search Query on the ObjectRepository
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ObjectRepositoryException extends RuntimeException {
    public ObjectRepositoryException(String message) {
        super(message);
    }
}

// TODO (Christian) Kein guter Exception Name, da reden wir nächstes mal drüber. Sollte irgendwie den passierten Fehler suggerieren
