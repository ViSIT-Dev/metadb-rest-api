package rest;

import com.github.anno4j.Anno4j;
import model.Resource;
import model.technicalMetadata.DigitalRepresentation;
import model.vismo.Group;
import model.vismo.Reference;
import model.vismo.ReferenceEntry;
import model.vismo.Title;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfigException;
import org.openrdf.repository.sparql.SPARQLRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import rest.configuration.VisitIDGenerator;
import rest.persistence.util.ImportQueryGenerator;

import java.math.BigInteger;

@SpringBootApplication
@ComponentScan(basePackages = {"rest"})
public class VisitRestApplication extends SpringBootServletInitializer {

    private static Log logger = LogFactory.getLog(VisitRestApplication.class);

    @Value("${visit.rest.sparql.endpoint.query}")
    private String sparqlEndpointQuery;

    @Value("${visit.rest.sparql.endpoint.update}")
    private String sparqlEndpointUpdate;

    @Value("${visit.rest.sparql.endpoint.testdata}")
    private boolean createTestdata;

    @Value("${visit.rest.templates.path}")
    private String pathToTemplates;

    @Bean
    public Anno4j anno4j() throws RepositoryConfigException, RepositoryException, InstantiationException, IllegalAccessException {
        if (!sparqlEndpointQuery.equals("none") && !sparqlEndpointUpdate.equals("none")) {
            logger.info("Connecting to SPARQL endpoint " + sparqlEndpointQuery + " and " + sparqlEndpointUpdate);
            SPARQLRepository sparqlRepository = new SPARQLRepository(sparqlEndpointQuery, sparqlEndpointUpdate);
            return new Anno4j(sparqlRepository, new VisitIDGenerator(), null, true);
        } else {
            logger.info("No SPARQL endpoint configured. Creating In-Memory SPARQL endpoint");

            Anno4j anno4j = new Anno4j(true);
            anno4j.setIdGenerator(new VisitIDGenerator());

            if(createTestdata) {
                this.createTestData(anno4j);
            }

            return anno4j;
        }
    }

    @Bean
    public ImportQueryGenerator importQueryGenerator() {
        return new ImportQueryGenerator(this.sparqlEndpointQuery, this.sparqlEndpointUpdate, this.pathToTemplates);
    }

    public static void main(String[] args) {
        SpringApplication.run(VisitRestApplication.class, args);
    }

    /**
     * Method to create testdata that can be used via the created Tomcat instance. Is only persisted if the boolean
     * "createTestdata" is set to true in the application.properties.
     *
     * @param anno4j    The local Anno4j that is used.
     * @throws RepositoryException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    private void createTestData(Anno4j anno4j) throws RepositoryException, IllegalAccessException, InstantiationException {
        Resource resource1 = anno4j.createObject(Resource.class);

        Resource resource2 = anno4j.createObject(Resource.class);

        DigitalRepresentation digitalRepresentation1 = anno4j.createObject(DigitalRepresentation.class);
        digitalRepresentation1.setTechnicalMetadata("Sample Data");
        resource1.addDigitalRepresentation(digitalRepresentation1);

        DigitalRepresentation digitalRepresentation2 = anno4j.createObject(DigitalRepresentation.class);
        digitalRepresentation2.setTechnicalMetadata("Another Sample Data");
        resource1.addDigitalRepresentation(digitalRepresentation2);

        DigitalRepresentation digitalRepresentation3 = anno4j.createObject(DigitalRepresentation.class);
        digitalRepresentation3.setTechnicalMetadata("Another third Sample Data");
        resource2.addDigitalRepresentation(digitalRepresentation3);

        DigitalRepresentation digitalRepresentation4 = anno4j.createObject(DigitalRepresentation.class);
        digitalRepresentation4.setTechnicalMetadata("Another fourth Sample Data");
        resource2.addDigitalRepresentation(digitalRepresentation4);

        Group group = anno4j.createObject(Group.class);
        group.setIconography("Iconography");
        group.addKeyword("Keyword");
        group.addKeyword("Keyword2");

        ReferenceEntry entry = anno4j.createObject(ReferenceEntry.class);
        entry.setPages(BigInteger.valueOf(11));

        entry.setIsAbout(group);
        group.addEntry(entry);

        ReferenceEntry entry2 = anno4j.createObject(ReferenceEntry.class);
        entry2.setPages(BigInteger.valueOf(22));

        entry2.setIsAbout(group);
        group.addEntry(entry2);

        Reference reference = anno4j.createObject(Reference.class);
        reference.addKeyword("ReferenceKeyword");

        Title title = anno4j.createObject(Title.class);
        title.setTitle("ReferenceTitle");
        title.setSuperordinateTitle("ReferenceSuperordinateTitle");
        reference.setTitle(title);

        reference.addEntry(entry);
        entry.setEntryIn(reference);

        reference.addEntry(entry2);
        entry2.setEntryIn(reference);

        logger.debug("Resource created with ID: " + resource1.getResourceAsString());
        logger.debug("Digital Representation with id created: " + digitalRepresentation1.getResourceAsString());
        logger.debug("Digital Representation with id created: " + digitalRepresentation2.getResourceAsString());
        logger.debug("Resource created with ID: " + resource2.getResourceAsString());
        logger.debug("Digital Representation with id created: " + digitalRepresentation3.getResourceAsString());
        logger.debug("Digital Representation with id created: " + digitalRepresentation4.getResourceAsString());
        logger.debug("Group entity created with id: " + group.getResourceAsString());

    }
}