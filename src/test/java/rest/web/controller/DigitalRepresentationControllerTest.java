package rest.web.controller;

import com.github.anno4j.Anno4j;
import model.Resource;
import model.technicalMetadata.DigitalRepresentation;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfigException;
import org.springframework.test.web.servlet.MockMvc;
import rest.BaseWebTest;
import rest.Exception.DigitalRepositoryException;
import rest.persistence.repository.DigitalRepresentationRepository;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.junit.Assert.*;

/**
 * Test Class with methods in order to test the DigitalRepresentationController.
 */
public class DigitalRepresentationControllerTest extends BaseWebTest {

    // TODO (Christian) Bitte hier tests entwickeln, die die Funktionalitäten um die DigitalRepresentation Requirements abdecken
    private String objectID;
    private String mediaID1;
    private String mediaID2;
    private MockMvc mockMvc;
    private Anno4j anno4j;
    private DigitalRepresentationController digitalRepresentationController = new DigitalRepresentationController();
    private DigitalRepresentationRepository digitalRepresentationRepository;

    private final String standardUrl = "https://database.visit.uni-passau.de/api/";

    /**
     * setUp a new Model for the test methods
     * @throws RepositoryException
     * @throws RepositoryConfigException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    @Before
    public void setUp() throws RepositoryException,RepositoryConfigException ,IllegalAccessException, InstantiationException {
        Assume.assumeTrue(this.isOfflineCheck());
        this.anno4j = new Anno4j();
        this.createModel(anno4j);


    }

    /**
     * Testmethod, expects a DigitalRepositoryException to come with a false given MediaID
     * @throws Exception
     */
   @Test(expected = DigitalRepositoryException.class)
    public void getSingleTechnicalMetadataByFalseMediaIDTest() throws Exception{
       /*Erstelle einen zufälligen Alphanumerischen String mit Länge 47*/
        String random = RandomStringUtils.randomAlphanumeric(47);
        /*Erstelle Simulation der Klasse DigitalRepresentationRepository falls diese später aufgerufen wird*/
        DigitalRepresentationRepository digitalRepresentationRepository = mock(DigitalRepresentationRepository.class);
        /*falls Anno4J angefragt wird gebe ein neues anno4J Object zurück*/
        when(digitalRepresentationRepository.getAnno4j()).thenReturn(this.anno4j);
        //teste den Repository
        given(digitalRepresentationRepository.getSingleTechnicalMetadataByMediaID(this.mediaID1)).willReturn("test1");
        /*Teste den Controller*/
        mockMvc.perform(get(standardUrl+"media/"+random));
    }

    /**
     * Testmethod, expects a List to come with a ObjectID given
     */
    @Test
    public void getAllTechnicalMetadataStringsByObjectIDTest(){

    }

    /**
     * Method to create the Objects to test the Testmethods on
     * @param anno4j
     * @throws RepositoryException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
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