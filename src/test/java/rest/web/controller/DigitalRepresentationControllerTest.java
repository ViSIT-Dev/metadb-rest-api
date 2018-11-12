package rest.web.controller;

import com.github.anno4j.Anno4j;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfigException;
import org.springframework.test.web.servlet.MockMvc;
import rest.BaseWebTest;
import rest.Exception.DigitalRepositoryException;

import static org.junit.Assert.*;

/**
 * Created by Manu on 31.10.18.
 */
public class DigitalRepresentationControllerTest extends BaseWebTest {

    // TODO (Christian) Bitte hier tests entwickeln, die die Funktionalitäten um die DigitalRepresentation Requirements abdecken

    MockMvc mockMvc;
    @Before
    public void setUp() throws RepositoryException, IllegalAccessException, InstantiationException {
        Assume.assumeTrue(this.isOfflineCheck());

    }
    @Test(expected = DigitalRepositoryException.class)
    public void getSingleTechnicalMetadataByEmptyMediaIDTest() throws RepositoryException, RepositoryConfigException {
        Anno4j anno4j = new Anno4j();

    }
    @Test
    public void getAllTechnicalMetadataStringsByObjectIDTest(){

    }
}