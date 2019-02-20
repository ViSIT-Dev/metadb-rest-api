package rest;

import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.UpdateExecutionException;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfigException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import rest.persistence.repository.Anno4jRepository;
import rest.persistence.repository.DigitalRepresentationRepository;
import rest.persistence.repository.ObjectRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 * Base Web test to test application setup etc.
 *
 * For further tests of controllers, let the further test classes extend this class.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.MOCK)
@WebAppConfiguration
public abstract class BaseWebTest {

    protected MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Value("${visit.rest.sparql.endpoint.query}")
    private String sparqlEndpointQuery;

    @Value("${visit.rest.sparql.endpoint.update}")
    private String sparqlEndpointUpdate;

    @Autowired
    protected DigitalRepresentationRepository digitalRepresentationRepository;

    @Autowired
    protected Anno4jRepository anno4jRepository;

    @Autowired
    protected ObjectRepository objectRepository;

    @Before
    public void setUp() throws Exception {
        Assume.assumeTrue(this.isOfflineCheck());

        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .defaultRequest(get("/"))
                .build();

        this.createTestModel();
    }

    @After
    public void destroy() throws RepositoryException {
        this.digitalRepresentationRepository.getAnno4j().getObjectRepository().getConnection().close();
        this.anno4jRepository.getAnno4j().getObjectRepository().getConnection().close();
    }

    public abstract void createTestModel() throws RepositoryException, IllegalAccessException, InstantiationException, RepositoryConfigException, MalformedQueryException, UpdateExecutionException;

    @Test
    public void testSomething() {
        // Empty test to test BaseWebTest setup
    }

    public boolean isOfflineCheck() {
        return (this.sparqlEndpointQuery.equals("none")) && (this.sparqlEndpointUpdate.equals("none"));
    }
}
