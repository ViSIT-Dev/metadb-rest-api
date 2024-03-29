package rest.persistence.repository;

import com.github.anno4j.Anno4j;
import model.Resource;
import model.namespace.VISMO;
import model.technicalMetadata.DigitalRepresentation;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.marmotta.ldpath.parser.ParseException;
import org.junit.Test;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.UpdateExecutionException;
import org.openrdf.repository.RepositoryException;
import rest.BaseWebTest;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.*;

public class DigitalRepresentationRepositoryTest extends BaseWebTest {

    private String objectID;
    private String resourceWithoutDigRep;
    private Resource resource;
    private String mediaID1;
    private String mediaID2;

    @Test
    public void testNumberOfDigitalRepresentations() throws RepositoryException, QueryEvaluationException, MalformedQueryException, ParseException {
        int number1 = this.digitalRepresentationRepository.getNumberOfDigitalRepresentationsByObjectId(objectID);

        int number2 = this.digitalRepresentationRepository.getNumberOfDigitalRepresentationsByObjectId(resourceWithoutDigRep);

        assertEquals(2, number1);
        assertEquals(0, number2);
    }

    @Test
    public void testGetAllTechnicalMetadataStringsByObjectID() throws RepositoryException, MalformedQueryException, QueryEvaluationException, ParseException {
        String result = this.digitalRepresentationRepository.getAllTechnicalMetadataStringsByObjectID(this.objectID, Resource.class);
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

    @Test
    public void createNewDigitalRepresentationNodeWithExisitingObjectId() throws IllegalAccessException, MalformedQueryException, RepositoryException, InstantiationException, ParseException, QueryEvaluationException, UpdateExecutionException {
        String objectId = objectID;
        String newNodeId = this.digitalRepresentationRepository.createNewDigitalRepresentationNode(objectId, VISMO.RESOURCE);

        Set<DigitalRepresentation> digitalRepresentations = this.resource.getDigitalRepresentations();
        LinkedList<String> digRepIds = new LinkedList<String>();

        for(DigitalRepresentation digRep : digitalRepresentations) {
            digRepIds.add(digRep.getResourceAsString());
        }

        assertTrue(digRepIds.contains(newNodeId));
    }

    @Test
    public void updateDigitalRepresentationNodeWithExistingMediaId() throws RepositoryException, QueryEvaluationException, MalformedQueryException, ParseException {
        String newData = "newSampleData";
        this.digitalRepresentationRepository.updateDigitalRepresentationNode(this.mediaID1, newData);
        String resultMediaTest = this.digitalRepresentationRepository.getSingleTechnicalMetadataByMediaID(this.mediaID1);
        assertEquals(resultMediaTest, newData);
    }

    @Test(expected = QueryEvaluationException.class)
    public void updateDigitalRepresentationNodeWithNonExistingMediaId() throws RepositoryException, QueryEvaluationException, MalformedQueryException, ParseException {
        String random = RandomStringUtils.randomAlphanumeric(47);
        this.digitalRepresentationRepository.updateDigitalRepresentationNode(random, "HelloWorld");
        String resultMediaTest = this.digitalRepresentationRepository.getSingleTechnicalMetadataByMediaID(random);
        assertEquals(resultMediaTest, random);
    }

    @Test(expected = QueryEvaluationException.class)
    public void deleteDigitalRepresentationNodeMediaSuccess() throws RepositoryException, QueryEvaluationException, MalformedQueryException, ParseException {
        String mediaID = this.mediaID1;
        this.digitalRepresentationRepository.deleteDigitalRepresentationMedia(mediaID);
        assertThat(this.digitalRepresentationRepository.getAllTechnicalMetadataStringsByObjectID(objectID, Resource.class), instanceOf(String.class));
        this.digitalRepresentationRepository.getSingleTechnicalMetadataByMediaID(mediaID);
    }

    @Test(expected = QueryEvaluationException.class)
    public void deleteDigitalRepresentationNodeMediaFailure() throws RepositoryException, QueryEvaluationException, MalformedQueryException, ParseException {
        String random = RandomStringUtils.randomAlphanumeric(47);
        this.digitalRepresentationRepository.deleteDigitalRepresentationMedia(random);
    }

    @Test(expected = QueryEvaluationException.class)
    public void deleteDigitalRepresentationNodeMediaAndObjectSuccess() throws RepositoryException, QueryEvaluationException, MalformedQueryException, ParseException {
        String objectId = this.objectID;
        String mediaId = this.mediaID1;
        this.digitalRepresentationRepository.deleteDigitalRepresentationMediaAndObject(mediaId, objectId);
        assertThat(this.digitalRepresentationRepository.getAllTechnicalMetadataStringsByObjectID(objectId, Resource.class), instanceOf(List.class));
        this.digitalRepresentationRepository.getSingleTechnicalMetadataByMediaID(mediaId);
    }

    @Test(expected = QueryEvaluationException.class)
    public void deleteDigitalRepresentationNodeMediaAndObjectMediaIdFailure() throws RepositoryException, QueryEvaluationException, MalformedQueryException, ParseException {
        String objectID = this.objectID;
        String mediaId = RandomStringUtils.randomAlphanumeric(47);
        this.digitalRepresentationRepository.deleteDigitalRepresentationMediaAndObject(mediaId, objectID);
    }

    @Test(expected = QueryEvaluationException.class)
    public void deleteDigitalRepresentationNodeMediaAndObjectObjectIdFailure() throws RepositoryException, QueryEvaluationException, MalformedQueryException, ParseException {
        String objectID = RandomStringUtils.randomAlphanumeric(47);
        String mediaId = this.mediaID1;
        this.digitalRepresentationRepository.deleteDigitalRepresentationMediaAndObject(mediaId, objectID);
    }

    @Override
    public void createTestModel() throws RepositoryException, IllegalAccessException, InstantiationException {
        Anno4j anno4j = this.digitalRepresentationRepository.getAnno4j();

        Resource resource = anno4j.createObject(Resource.class);
        this.resource = resource;
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

        Resource resourceWithoutDigRep = anno4j.createObject(Resource.class);
        this.resourceWithoutDigRep = resourceWithoutDigRep.getResourceAsString();
    }
}
