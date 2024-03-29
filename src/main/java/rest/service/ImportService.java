package rest.service;

import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.UpdateExecutionException;
import org.openrdf.repository.RepositoryException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import rest.application.exception.IdMapperException;
import rest.application.exception.ImportException;
import rest.application.exception.QueryGenerationException;
import rest.persistence.repository.ImportRepository;
import rest.persistence.util.ImportQueryGenerator;

/**
 * Service class for the import functionality.
 */
@Service
public class ImportService {

    @Autowired
    private ImportRepository importRepository;

    @Autowired
    private ImportQueryGenerator importQueryGenerator;

    public void importJSON(String json) throws ImportException {
        try {
            String updateQuery = this.importQueryGenerator.createUpdateQueryFromJSON(json);

            this.importRepository.persistJSONDataByUpdateQuery(updateQuery);
        } catch (QueryGenerationException | IdMapperException | UpdateExecutionException | MalformedQueryException | RepositoryException e) {
            throw new ImportException(e.getMessage());
        }
    }
}
