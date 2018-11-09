package rest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 * Base Web test to test application setup etc.
 *
 * For further tests of controllers, let the further test classes extend this class.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.MOCK)
@WebAppConfiguration
public class BaseWebTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Value("${visit.rest.sparql.endpoint.query}")
    private String sparqlEndpointQuery;

    @Value("${visit.rest.sparql.endpoint.update}")
    private String sparqlEndpointUpdate;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .defaultRequest(get("/"))
                .build();
    }

    @Test
    public void testSomething() {
        // Empty test to test BaseWebTest setup
    }

    public MockMvc getMockMvc() {
        return mockMvc;
    }

    public boolean isOfflineCheck() {
        return (this.sparqlEndpointQuery.equals("none")) && (this.sparqlEndpointUpdate.equals("none"));
    }
}
