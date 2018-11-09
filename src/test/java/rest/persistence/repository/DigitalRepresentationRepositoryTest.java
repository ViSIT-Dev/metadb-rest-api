package rest.persistence.repository;

import com.github.anno4j.Anno4j;
import model.Resource;
import model.technicalMetadata.DigitalRepresentation;
import org.apache.marmotta.ldpath.parser.ParseException;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;
import org.springframework.beans.factory.annotation.Autowired;
import rest.BaseWebTest;

import java.util.List;

import static org.junit.Assert.*;

public class DigitalRepresentationRepositoryTest extends BaseWebTest{

    @Autowired
    private DigitalRepresentationRepository digitalRepresentationRepository;

    private String objectID;
    private String mediaID1;
    private String mediaID2;

    @Before
    public void setUp() throws RepositoryException, IllegalAccessException, InstantiationException {
        Assume.assumeTrue(this.isOfflineCheck());

        Anno4j anno4j = this.digitalRepresentationRepository.getAnno4j();

        this.createModel(anno4j);
    }

    @Test
    public void testGetAllTechnicalMetadataStringsByObjectID() throws RepositoryException, MalformedQueryException, QueryEvaluationException {
        List<String> result = this.digitalRepresentationRepository.getAllTechnicalMetadataStringsByObjectID(this.objectID);

        assertEquals(2, result.size());
        assertTrue(result.contains("test1"));
        assertTrue(result.contains("test2"));
    }

    @Test
    public void testGetSingleMetadataByMediaID() throws RepositoryException, QueryEvaluationException, MalformedQueryException, ParseException {
        String techMetadata1 = this.digitalRepresentationRepository.getSingleTechnicalMetadataByMediaID(this.mediaID1);

        String techMetadata2 = this.digitalRepresentationRepository.getSingleTechnicalMetadataByMediaID(this.mediaID2);

        assertEquals("test1", techMetadata1);
        assertEquals("test2", techMetadata2);
    }

    @Test(expected = QueryEvaluationException.class)
    public void testGetSingleMetadataByMediaIDWithNonExistingID() throws RepositoryException, QueryEvaluationException, MalformedQueryException, ParseException {
        String nonExistentID = mediaID1 + "abc";

        String techMetadata = this.digitalRepresentationRepository.getSingleTechnicalMetadataByMediaID(nonExistentID);
    }

    private void createModel(Anno4j anno4j) throws RepositoryException, IllegalAccessException, InstantiationException {
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