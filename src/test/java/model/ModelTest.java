package model;

import com.github.anno4j.Anno4j;
import model.technicalMetadata.DigitalRepresentation;
import org.apache.marmotta.ldpath.parser.ParseException;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class ModelTest {

    private Anno4j anno4j;

    @Before
    public void setUp() throws Exception {
        this.anno4j = new Anno4j();
    }

    @Test
    public void testResourceAndDigitalRepresentation() throws RepositoryException, IllegalAccessException, InstantiationException, ParseException, MalformedQueryException, QueryEvaluationException {
        Resource resource = this.anno4j.createObject(Resource.class);

        DigitalRepresentation rep1 = this.anno4j.createObject(DigitalRepresentation.class);
        String test = "test";
        rep1.setTechnicalMetadata(test);

        DigitalRepresentation rep2 = this.anno4j.createObject(DigitalRepresentation.class);

        List<Resource> result = this.anno4j.createQueryService().execute(Resource.class);

        Resource queried = result.get(0);

        assertEquals(0, queried.getDigitalRepresentations().size());

        resource.addDigitalRepresentation(rep1);

        result = this.anno4j.createQueryService().execute(Resource.class);

        queried = result.get(0);

        assertEquals(1, queried.getDigitalRepresentations().size());
        String technicalMetadata = ((DigitalRepresentation) queried.getDigitalRepresentations().toArray()[0]).getTechnicalMetadata();
        assertEquals(test, technicalMetadata);

        resource.addDigitalRepresentation(rep2);

        result = this.anno4j.createQueryService().execute(Resource.class);

        queried = result.get(0);

        assertEquals(2, queried.getDigitalRepresentations().size());
    }
}
