package model.technicalMetadata;

import com.github.anno4j.Anno4j;
import model.Resource;
import model.VISMO;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.repository.object.ObjectQuery;
import org.openrdf.repository.object.RDFObject;
import org.openrdf.result.Result;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class QueryMetadataTest {

    private Anno4j anno4j;
    private String id;

    @Before
    public void setUp() throws Exception {
        this.anno4j = new Anno4j();

        createModel(this.anno4j);
    }

    @Test
    public void testGetTechnicalMetadataQuery() throws RepositoryException, MalformedQueryException, QueryEvaluationException {
        ObjectConnection connection = this.anno4j.getObjectRepository().getConnection();

        String queryString = "SELECT ?d\n" +
                "   WHERE { <" + this.id + "> a <" + VISMO.RESOURCE + "> .\n" +
                "   <" + this.id + "> <" + VISMO.HAS_DIGITAL_REPRESENTATION + "> ?d }";

//        System.out.println(queryString);

        ObjectQuery query = connection.prepareObjectQuery(queryString);

        Result<RDFObject> result = query.evaluate(RDFObject.class);

        List<RDFObject> list = result.asList();

        assertEquals("test1", ((DigitalRepresentation)list.get(0)).getTechnicalMetadata());
        assertEquals("test2", ((DigitalRepresentation)list.get(1)).getTechnicalMetadata());
    }

    private void createModel(Anno4j anno4j) throws RepositoryException, IllegalAccessException, InstantiationException {
        Resource resource = anno4j.createObject(Resource.class);
        this.id = resource.getResourceAsString();

        DigitalRepresentation rep1 = anno4j.createObject(DigitalRepresentation.class);
        String test = "test1";
        rep1.setTechnicalMetadata(test);

        DigitalRepresentation rep2 = anno4j.createObject(DigitalRepresentation.class);
        String test2 = "test2";
        rep2.setTechnicalMetadata(test2);

        resource.addDigitalRepresentation(rep1);
        resource.addDigitalRepresentation(rep2);
    }
}
