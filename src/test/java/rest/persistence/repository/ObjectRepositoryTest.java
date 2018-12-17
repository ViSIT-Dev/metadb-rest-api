package rest.persistence.repository;

import com.github.anno4j.Anno4j;
import model.Resource;
import model.technicalMetadata.DigitalRepresentation;
import org.apache.jena.atlas.json.JsonObject;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.openrdf.repository.RepositoryException;
import org.springframework.test.web.servlet.MockMvc;
import rest.BaseWebTest;

import static org.junit.Assert.*;

/**
 * Test Class for Testing the Object Repository
 */
public class ObjectRepositoryTest extends BaseWebTest {

    private String objectID;
    private String mediaID1;
    private String mediaID2;
    private ObjectRepository objectRepository;

    /**
     * Test to get a Json of the Object with a given Object ID expects Success and a Json  to come
     */
    @Test
    public void getRepresentationOfObjectSuccess() throws Exception {
      //  this.objectRepository.getRepresentationOfObject(this.mediaID1,);

    }


    @Override
    public void createTestModel() throws RepositoryException, IllegalAccessException, InstantiationException {
        Anno4j anno4j = this.digitalRepresentationRepository.getAnno4j();

        Resource resource = anno4j.createObject(Resource.class);
        this.objectID = resource.getResourceAsString();

        DigitalRepresentation rep1 = anno4j.createObject(DigitalRepresentation.class);
        this.mediaID1 = rep1.getResourceAsString();
        String test = "test1";
        rep1.setTechnicalMetadata(test);

        DigitalRepresentation rep2 = anno4j.createObject(DigitalRepresentation.class);
        this.mediaID2 = rep2.getResourceAsString();
        String test2 = "test2";
        rep2.setTechnicalMetadata(test2);

        resource.addDigitalRepresentation(rep1);
        resource.addDigitalRepresentation(rep2);
    }




}