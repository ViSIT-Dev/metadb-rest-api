package rest.persistence.repository;

import com.github.anno4j.model.Annotation;
import com.github.anno4j.model.impl.targets.SpecificResource;
import com.github.anno4j.model.namespaces.OADM;
import org.junit.Test;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;
import org.springframework.beans.factory.annotation.Autowired;
import rest.BaseWebTest;

import static org.junit.Assert.*;

public class Anno4jRepositoryTest extends BaseWebTest {

    @Autowired
    private Anno4jRepository repository;

    @Test
    public void getLowestClassGivenId() throws IllegalAccessException, InstantiationException, MalformedQueryException, RepositoryException, QueryEvaluationException {
        Annotation annotation = this.repository.getAnno4j().createObject(Annotation.class);

        String resource = this.repository.getLowestClassGivenId(annotation.getResourceAsString());

        assertEquals(OADM.ANNOTATION, resource);
    }

    @Test
    public void getLowestClassGivenId2() throws RepositoryException, IllegalAccessException, InstantiationException, QueryEvaluationException, MalformedQueryException {
        SpecificResource specificResource = this.repository.getAnno4j().createObject(SpecificResource.class);

        String resource = this.repository.getLowestClassGivenId(specificResource.getResourceAsString());

        assertEquals(OADM.SPECIFIC_RESOURCE, resource);
    }
}