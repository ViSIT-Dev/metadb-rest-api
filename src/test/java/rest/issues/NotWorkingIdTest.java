package rest.issues;

import com.github.anno4j.Anno4j;
import org.junit.Test;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.Update;
import org.openrdf.query.UpdateExecutionException;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfigException;
import org.openrdf.repository.object.ObjectConnection;
import org.springframework.test.web.servlet.MvcResult;
import rest.BaseWebTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test to reproduce issue REST 500 fetching http://visit.de/data/5baa09fdc1e34
 */
public class NotWorkingIdTest extends BaseWebTest {

    private final static String STANDARD_URL = "https://database.visit.uni-passau.de/";
    private final static String OBJECT_ID = "http://visit.de/data/5baa09fdc1e34";

    @Test
    public void testIssue() throws Exception {
        Anno4j anno4j = this.objectRepository.getAnno4j();

        ObjectConnection connection = anno4j.getObjectRepository().getConnection();

        Update update = connection.prepareUpdate(UPDATE_QUERY);
        update.execute();

        String requestURL = STANDARD_URL + "object?id=" + OBJECT_ID;
        MvcResult mvcResult = mockMvc.perform(get(requestURL)).andDo(print()).andExpect(status().isOk()).andReturn();
        String mvcResultString = mvcResult.getResponse().getContentAsString();

        System.out.println(mvcResult);
    }

    private final static String UPDATE_QUERY = "INSERT DATA {" +
            "<http://visit.de/data/5baa09fdc1e34> rdf:type <http://visit.de/ontologies/vismo/Architecture> . " +
            "<http://visit.de/data/5baa09fdc1e34> <http://erlangen-crm.org/170309/P108i_was_produced_by> <http://visit.de/data/5bb5db27e73fb> . " +
            "<http://visit.de/data/5baa09fdc1e34> <http://erlangen-crm.org/170309/P55_has_current_location> <http://visit.de/data/5baa5ad9cf476> . " +
            "<http://visit.de/data/5baa09fdc1e34> <http://erlangen-crm.org/170309/P1_is_identified_by> <http://visit.de/data/5baa09fdc81dd> . " +
            "<http://visit.de/data/5baa09fdc1e34> <http://erlangen-crm.org/170309/P31i_was_modified_by> <http://visit.de/data/5bb5db27f193c> . " +
            "<http://visit.de/data/5baa09fdc1e34> <http://erlangen-crm.org/170309/P31i_was_modified_by> <http://visit.de/data/5bb5db2809c5b> . " +
            "<http://visit.de/data/5baa09fdc1e34> <http://erlangen-crm.org/170309/P31i_was_modified_by> <http://visit.de/data/5bb5db2817d24> . " +
            "<http://visit.de/data/5baa09fdc1e34> <http://erlangen-crm.org/170309/P31i_was_modified_by> <http://visit.de/data/5bb5db2825f43> . " +
            "<http://visit.de/data/5baa09fdc1e34> <http://erlangen-crm.org/170309/P2_has_type> <http://visit.de/data/5bb5db284370e> . " +
            "<http://visit.de/data/5baa09fdc1e34> <http://erlangen-crm.org/170309/P2_has_type> <http://visit.de/data/5bb5db2845a6f> . " +
            "<http://visit.de/data/5baa09fdc1e34> <http://erlangen-crm.org/170309/P2_has_type> <http://visit.de/data/5bb5db28493ef> . " +
            "<http://visit.de/data/5baa09fdc1e34> <http://visit.de/data/originatesFrom> \"vismotest\" . " +
            "<http://visit.de/data/5baa09fdc1e34> owl:sameAs <https://database-test.visit.uni-passau.de/drupal/wisski/navigate/215/view> . " +
            "<http://visit.de/data/5baa03734f10d> <http://erlangen-crm.org/170309/P55_has_current_location> <http://visit.de/data/5baa09fdc1e34> . " +
            "<http://visit.de/data/5baa09e27ce2c> <http://erlangen-crm.org/170309/P52i_is_current_owner_of> <http://visit.de/data/5baa09fdc1e34> . " +
            "<https://database-test.visit.uni-passau.de/drupal/wisski/navigate/215/view> owl:sameAs <http://visit.de/data/5baa09fdc1e34> . " +
            "<http://visit.de/data/5baa5d5cada67> <http://erlangen-crm.org/170309/P55_has_current_location> <http://visit.de/data/5baa09fdc1e34> . " +
            "<http://visit.de/data/5bb5dd3d9782f> <http://erlangen-crm.org/170309/P89_falls_within> <http://visit.de/data/5baa09fdc1e34> . " +
            "<http://visit.de/data/5bb5df566e940> <http://erlangen-crm.org/170309/P89_falls_within> <http://visit.de/data/5baa09fdc1e34> . " +
            "<http://visit.de/data/5bbf05cbd082a> <http://erlangen-crm.org/170309/P55_has_current_location> <http://visit.de/data/5baa09fdc1e34> . " +
            "<http://visit.de/data/5bee7f4f1d7b2> <http://erlangen-crm.org/170309/P55_has_current_location> <http://visit.de/data/5baa09fdc1e34> . " +
            "<http://visit.de/data/5bee81c416746> <http://erlangen-crm.org/170309/P55_has_current_location> <http://visit.de/data/5baa09fdc1e34> . " +
            "<http://visit.de/data/5c0f581801582> <http://erlangen-crm.org/170309/P55_has_current_location> <http://visit.de/data/5baa09fdc1e34> . " +
            "<http://visit.de/data/5c0f595dc57d2> <http://erlangen-crm.org/170309/P55_has_current_location> <http://visit.de/data/5baa09fdc1e34> . " +
            "<http://visit.de/data/5c0f5a23d5df4> <http://erlangen-crm.org/170309/P55_has_current_location> <http://visit.de/data/5baa09fdc1e34> . " +
            "}";

    @Override
    public void createTestModel() throws RepositoryException, IllegalAccessException, InstantiationException, RepositoryConfigException, MalformedQueryException, UpdateExecutionException {

    }
}
