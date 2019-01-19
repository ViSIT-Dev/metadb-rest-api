package rest;

import com.github.anno4j.Anno4j;
import model.Resource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfigException;
import org.openrdf.repository.sparql.SPARQLRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@ComponentScan(basePackages = {"rest"})
public class VisitRestApplication { // extends SpringBootServletInitializer

    private static Log logger = LogFactory.getLog(VisitRestApplication.class);

    @Value("${visit.rest.sparql.endpoint.query}")
    private String sparqlEndpointQuery;

    @Value("${visit.rest.sparql.endpoint.update}")
    private String sparqlEndpointUpdate;

    @Value("${visit.rest.sparql.endpoint.testdata}")
    private boolean createTestdata;

    @Bean
    public Anno4j anno4j() throws RepositoryConfigException, RepositoryException, InstantiationException, IllegalAccessException {
        if (!sparqlEndpointQuery.equals("none") && !sparqlEndpointUpdate.equals("none")) {
            logger.info("Connecting to SPARQL endpoint " + sparqlEndpointQuery + " and " + sparqlEndpointUpdate);
            SPARQLRepository sparqlRepository = new SPARQLRepository(sparqlEndpointQuery, sparqlEndpointUpdate);
            return new Anno4j(sparqlRepository, null, false);
        } else {
            logger.info("No SPARQL endpoint configured. Creating In-Memory SPARQL endpoint");

            Anno4j anno4j = new Anno4j();

            if(createTestdata) {
                this.createTestData(anno4j);
            }

            return anno4j;
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(VisitRestApplication.class, args);
    }

    private void createTestData(Anno4j anno4j) throws RepositoryException, IllegalAccessException, InstantiationException {
        Resource resource1 = anno4j.createObject(Resource.class);

        Resource resource2 = anno4j.createObject(Resource.class);

        logger.debug("Resource created with ID: " + resource1.getResourceAsString());
        logger.debug("Resource created with ID: " + resource2.getResourceAsString());
    }
}