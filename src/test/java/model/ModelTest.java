package model;

import com.github.anno4j.Anno4j;
import com.github.anno4j.querying.QueryService;
import model.technicalMetadata.DigitalRepresentation;
import model.vismo.Group;
import model.vismo.Reference;
import model.vismo.ReferenceEntry;
import org.apache.marmotta.ldpath.parser.ParseException;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;

import java.util.List;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

public class ModelTest {

    private Anno4j anno4j;

    private final static String KEYWORD = "keyword1";
    private final static String KEYWORD2 = "KEYWORD2";
    private final static String ICONOGRAPHY = "iconography";

    @Before
    public void setUp() throws Exception {
        this.anno4j = new Anno4j();
    }

    @Test
    public void testGroupAndReferences() throws RepositoryException, IllegalAccessException, InstantiationException, ParseException, MalformedQueryException, QueryEvaluationException {
        Group group = this.anno4j.createObject(Group.class);

        DigitalRepresentation digrep = this.anno4j.createObject(DigitalRepresentation.class);

        group.addKeyword(KEYWORD);
        group.addKeyword(KEYWORD2);
        group.setIconography(ICONOGRAPHY);
        group.addDigitalRepresentation(digrep);

        ReferenceEntry entry = this.anno4j.createObject(ReferenceEntry.class);

        entry.setIsAbout(group);
        group.addEntry(entry);

        Reference reference = this.anno4j.createObject(Reference.class);

        reference.addEntry(entry);
        entry.setEntryIn(reference);

        QueryService queryService = this.anno4j.createQueryService();
        queryService.addCriteria(".", group.getResourceAsString());

        List<Group> result = queryService.execute(Group.class);

        assertEquals(1, result.size());

        Group resultGroup = result.get(0);

        assertEquals(entry.getResourceAsString(), ((ReferenceEntry) resultGroup.getEntries().toArray()[0]).getResourceAsString());
        assertEquals(ICONOGRAPHY, resultGroup.getIconography());
        assertTrue(group.getKeywords().contains(KEYWORD));
        assertTrue(group.getKeywords().contains(KEYWORD2));
        assertEquals(1, group.getDigitalRepresentations().size());
        assertTrue(group.getDigitalRepresentations().contains(digrep));
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
