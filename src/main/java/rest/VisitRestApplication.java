package rest;

import com.github.anno4j.Anno4j;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfigException;
import org.openrdf.repository.sparql.SPARQLRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class VisitRestApplication { // extends SpringBootServletInitializer

    private static Log logger = LogFactory.getLog(VisitRestApplication.class);

    @Value("${visit.rest.sparql.endpoint.query}")
    private String sparqlEndpointQuery;

    @Value("${visit.rest.sparql.endpoint.update}")
    private String sparqlEndpointUpdate;

    @Bean
    public Anno4j anno4j() throws RepositoryConfigException, RepositoryException {
        if (!sparqlEndpointQuery.equals("none") && !sparqlEndpointUpdate.equals("none")) {
            logger.info("Connecting to SPARQL endpoint " + sparqlEndpointQuery + " and " + sparqlEndpointUpdate);
            SPARQLRepository sparqlRepository = new SPARQLRepository(sparqlEndpointQuery, sparqlEndpointUpdate);
            return new Anno4j(sparqlRepository, null, false);
        } else {
            logger.info("No SPARQL endpoint configured. Creating In-Memory SPARQL endpoint");
            return new Anno4j();
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(VisitRestApplication.class, args);
    }

}