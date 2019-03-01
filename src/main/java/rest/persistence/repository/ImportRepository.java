package rest.persistence.repository;

import com.github.anno4j.Anno4j;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.Update;
import org.openrdf.query.UpdateExecutionException;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.object.ObjectConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Repository class for the import functionality.
 */
@Repository
public class ImportRepository {

    @Autowired
    private Anno4j anno4j;

    public void persistJSONDataByUpdateQuery(String updateQuery) throws RepositoryException, MalformedQueryException, UpdateExecutionException {
        ObjectConnection connection = this.anno4j.getObjectRepository().getConnection();

        Update update = connection.prepareUpdate(updateQuery);

        update.execute();
    }
}
