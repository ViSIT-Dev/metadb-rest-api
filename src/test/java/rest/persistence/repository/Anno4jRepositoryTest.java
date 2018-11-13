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

    private String annotationID;
    private String specificResourceID;

    @Test
    public void getLowestClassGivenId() throws IllegalAccessException, InstantiationException, MalformedQueryException, RepositoryException, QueryEvaluationException {
        String resource = this.anno4jRepository.getLowestClassGivenId(this.annotationID);

        assertEquals(OADM.ANNOTATION, resource);
    }

    @Test
    public void getLowestClassGivenId2() throws RepositoryException, IllegalAccessException, InstantiationException, QueryEvaluationException, MalformedQueryException {
        String resource = this.anno4jRepository.getLowestClassGivenId(this.specificResourceID);

        assertEquals(OADM.SPECIFIC_RESOURCE, resource);
    }

    @Override
    public void createTestModel() throws RepositoryException, IllegalAccessException, InstantiationException {
        Anno4j anno4j = this.anno4jRepository.getAnno4j();

        Annotation annotation = anno4j.createObject(Annotation.class);
        this.annotationID = annotation.getResourceAsString();

        SpecificResource specificResource = anno4j.createObject(SpecificResource.class);
        this.specificResourceID = specificResource.getResourceAsString();
    }
}