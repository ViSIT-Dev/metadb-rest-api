package model;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.Annotation;
import com.github.anno4j.model.impl.targets.SpecificResource;
import com.github.anno4j.model.namespaces.OADM;
import com.github.anno4j.querying.QueryService;
import model.technicalMetadata.DigitalRepresentation;
import org.apache.marmotta.ldpath.parser.ParseException;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfigException;
import org.springframework.beans.factory.annotation.Autowired;
import rest.persistence.repository.Anno4jRepository;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Various querying examples with Anno4j.
 */
public class QueryExamples {

    private String objectID;
    private String mediaID1;
    private String mediaID2;

    private Anno4j anno4j;

    @Before
    public void setUp() throws RepositoryException, IllegalAccessException, InstantiationException, RepositoryConfigException {
        this.anno4j = new Anno4j();

        this.createModel(anno4j);
    }

    @Test
    public void queryAllResources() throws RepositoryException, ParseException, MalformedQueryException, QueryEvaluationException {
        QueryService qs = this.anno4j.createQueryService();

        List<Resource> result = qs.execute(Resource.class);

        assertEquals(1, result.size());
    }

    @Test
    public void queryAllDigitalRepresentations() throws RepositoryException, ParseException, MalformedQueryException, QueryEvaluationException {
        QueryService qs = this.anno4j.createQueryService();

        List<DigitalRepresentation> result = qs.execute(DigitalRepresentation.class);

        assertEquals(2, result.size());
    }

    @Test
    public void queryResourceWithGivenID() throws RepositoryException, ParseException, MalformedQueryException, QueryEvaluationException {
        QueryService qs = this.anno4j.createQueryService();

        qs.addCriteria(".", this.objectID);

        List<Resource> result = qs.execute(Resource.class);

        assertEquals(1, result.size());

        assertEquals(this.objectID, result.get(0).getResourceAsString());
    }

    @Test
    public void queryAllDigRepOfGivenResource() throws RepositoryException, ParseException, MalformedQueryException, QueryEvaluationException {
        QueryService qs = this.anno4j.createQueryService();
        qs.addPrefix(VISMO.PREFIX, VISMO.NS);

        qs.addCriteria("^vismo:hasDigitalRepresentation", this.objectID);

        List<DigitalRepresentation> result = qs.execute(DigitalRepresentation.class);

        assertEquals(2, result.size());

        // Get all technicalMetadataStrings
        List<String> techMetadatas = new LinkedList<>();

        for(DigitalRepresentation digitalRepresentation : result) {
            techMetadatas.add(digitalRepresentation.getTechnicalMetadata());
        }
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
