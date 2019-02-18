package rest.persistence.repository;

import com.github.anno4j.Anno4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Repository class for the import functionality.
 */
@Repository
public class ImportRepository {

    @Autowired
    private Anno4j anno4j;
}
