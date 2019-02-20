package rest.persistence.repository;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.Annotation;
import com.github.anno4j.model.impl.targets.SpecificResource;
import com.github.anno4j.model.namespaces.OADM;
import model.Resource;
import model.namespace.VISMO;
import model.technicalMetadata.DigitalRepresentation;
import model.vismo.Group;
import org.apache.marmotta.ldpath.parser.ParseException;
import org.junit.Test;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.Update;
import org.openrdf.repository.RepositoryException;
import org.springframework.test.web.servlet.MvcResult;
import rest.BaseWebTest;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class Anno4jRepositoryTest extends BaseWebTest {

    private String annotationID;
    private String specificResourceID;
    private String groupID;

    private String resourceID;
    private String digRepID;

    private final static String WISSKI_VIEW_PATH = "https://database.visit.uni-passau.de/drupal/wisski/navigate/177/view";

    @Test
    public void testGetObjectRepresentationByWissKIViewPath() throws Exception {
        Anno4j anno4j = this.anno4jRepository.getAnno4j();

        // Test data for the getObjectIdByWisskiPath method
        Resource someResource = anno4j.createObject(Resource.class);

        String updateQuery = "INSERT DATA\n" +
                "{ \n" +
                "  <" + WISSKI_VIEW_PATH + "> <http://www.w3.org/2002/07/owl#sameAs> <" + someResource.getResourceAsString() + "> ." +
                "}";

        Update update = anno4j.getObjectRepository().getConnection().prepareUpdate(updateQuery);

        update.execute();

        String objectIdByWisskiPath = this.anno4jRepository.getObjectIdByWisskiPath(WISSKI_VIEW_PATH);

        assertEquals(someResource.getResourceAsString(), objectIdByWisskiPath);
    }

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

    @Test
    public void getLowestClassGivenIdWithVismoGroup() throws RepositoryException, MalformedQueryException, QueryEvaluationException {
        String resource = this.anno4jRepository.getLowestClassGivenId(this.groupID);

        assertEquals(VISMO.GROUP, resource);
    }

    @Test
    public void testGetResourceIdByDigRep() throws RepositoryException, QueryEvaluationException, MalformedQueryException, ParseException {
        String resourceId = this.anno4jRepository.getResourceIdByDigitalRepresentation(this.digRepID);

        assertEquals(this.resourceID, resourceId);
    }

    @Override
    public void createTestModel() throws RepositoryException, IllegalAccessException, InstantiationException {
        Anno4j anno4j = this.anno4jRepository.getAnno4j();

        Annotation annotation = anno4j.createObject(Annotation.class);
        this.annotationID = annotation.getResourceAsString();

        SpecificResource specificResource = anno4j.createObject(SpecificResource.class);
        this.specificResourceID = specificResource.getResourceAsString();

        Group group = anno4j.createObject(Group.class);
        this.groupID = group.getResourceAsString();

        Resource resource = anno4j.createObject(Resource.class);
        this.resourceID = resource.getResourceAsString();

        DigitalRepresentation digitalRepresentation = anno4j.createObject(DigitalRepresentation.class);
        resource.addDigitalRepresentation(digitalRepresentation);
        this.digRepID = digitalRepresentation.getResourceAsString();
    }
}