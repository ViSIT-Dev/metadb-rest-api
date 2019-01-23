package rest.configuration;

import org.openrdf.idGenerator.IDGenerator;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.sail.memory.model.MemValueFactory;

import java.util.Set;
import java.util.UUID;

/**
 * Implementation of IDGenerator in order to create URIs that are preceded by an own URI.
 */
public class VisitIDGenerator implements IDGenerator {

    @Override
    public Resource generateID(Set<URI> types) {
        return new MemValueFactory().createURI("http://visit.de/metadb/" + UUID.randomUUID());
    }
}
