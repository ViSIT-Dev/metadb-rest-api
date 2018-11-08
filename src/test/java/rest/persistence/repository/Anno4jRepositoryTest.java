package rest.persistence.repository;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.Annotation;
import com.github.anno4j.model.impl.targets.SpecificResource;
import com.github.anno4j.model.namespaces.OADM;
import model.Resource;
import model.technicalMetadata.DigitalRepresentation;
import org.junit.Assume;
import org.junit.Before;
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

    private String annotationID;
    private String specificResourceID;

    @Before
    public void setUp() throws RepositoryException, IllegalAccessException, InstantiationException {
        Assume.assumeTrue(this.isOfflineCheck());

        Anno4j anno4j = this.repository.getAnno4j();

        this.createModel(anno4j);
    }

    @Test
    public void getLowestClassGivenId() throws IllegalAccessException, InstantiationException, MalformedQueryException, RepositoryException, QueryEvaluationException {
        String resource = this.repository.getLowestClassGivenId(this.annotationID);

        assertEquals(OADM.ANNOTATION, resource);
    }

    @Test
    public void getLowestClassGivenId2() throws RepositoryException, IllegalAccessException, InstantiationException, QueryEvaluationException, MalformedQueryException {
        String resource = this.repository.getLowestClassGivenId(this.specificResourceID);

        assertEquals(OADM.SPECIFIC_RESOURCE, resource);
    }

    private void createModel(Anno4j anno4j) throws RepositoryException, IllegalAccessException, InstantiationException {
        Annotation annotation = anno4j.createObject(Annotation.class);
        this.annotationID = annotation.getResourceAsString();

        SpecificResource specificResource = anno4j.createObject(SpecificResource.class);
        this.specificResourceID = specificResource.getResourceAsString();
    }
}