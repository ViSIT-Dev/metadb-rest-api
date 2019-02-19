package rest.persistence.util;

import org.junit.Test;

import java.util.Arrays;
import java.util.LinkedList;

import static org.junit.Assert.*;

/**
 * Test-suite for the TripleMerger Class.
 */
public class TripleMergerTest {

    private final static String TRIPLES1 = "?x <http://erlangen-crm.org/170309/P128_carries> ?y1 . ?y1 rdf:type <http://erlangen-crm.org/170309/E34_Inscription> . ?y1 <http://erlangen-crm.org/170309/P3_has_note> ?inscription_text";
    private final static String TRIPLES2 = "?x <http://erlangen-crm.org/170309/P128_carries> ?y2 . ?y2 rdf:type <http://erlangen-crm.org/170309/E34_Inscription> . ?y2 <http://erlangen-crm.org/170309/P2_has_type> ?y3 . ?y3 rdf:type <http://erlangen-crm.org/170309/E55_Type> . ?y3 <http://erlangen-crm.org/170309/P3_has_note> ?inscription_has_type";
    private final static String TRIPLES3 = "?x <http://erlangen-crm.org/170309/P128_carries> ?y4 . ?y4 rdf:type <http://erlangen-crm.org/170309/E34_Inscription> . ?y4 <http://erlangen-crm.org/170309/P106_is_composed_of> ?y5 . ?y5 rdf:type <http://erlangen-crm.org/170309/E37_Mark> . ?y5 <http://erlangen-crm.org/170309/P3_has_note> ?inscription_signature";

    @Test
    public void testTripleMergerWithTwoTriples() {
        String result = TripleMerger.mergeTriples(new LinkedList<>(Arrays.asList(TRIPLES1, TRIPLES2)));

        assertTrue(result.contains("?x <http://erlangen-crm.org/170309/P128_carries> ?y1"));
        assertTrue(result.contains("?y1 rdf:type <http://erlangen-crm.org/170309/E34_Inscription>"));
        assertTrue(result.contains("?y1 <http://erlangen-crm.org/170309/P3_has_note> ?inscription_text"));
        assertTrue(result.contains("?y1 <http://erlangen-crm.org/170309/P2_has_type> ?y3"));
        assertTrue(result.contains("?y3 <http://erlangen-crm.org/170309/P3_has_note> ?inscription_has_type"));

        assertFalse(result.contains("?x <http://erlangen-crm.org/170309/P128_carries> ?y2"));
        assertFalse(result.contains("?y2 rdf:type <http://erlangen-crm.org/170309/E34_Inscription>"));

        System.out.println("The result is: " + result);
    }

    @Test
    public void testTripleMergerWithThreeTriples() {
        String result = TripleMerger.mergeTriples(new LinkedList<>(Arrays.asList(TRIPLES1, TRIPLES2, TRIPLES3)));

        assertTrue(result.contains("?x <http://erlangen-crm.org/170309/P128_carries> ?y1"));
        assertTrue(result.contains("?y1 rdf:type <http://erlangen-crm.org/170309/E34_Inscription>"));
        assertTrue(result.contains("?y1 <http://erlangen-crm.org/170309/P3_has_note> ?inscription_text"));
        assertTrue(result.contains("?y1 <http://erlangen-crm.org/170309/P2_has_type> ?y3"));
        assertTrue(result.contains("?y3 <http://erlangen-crm.org/170309/P3_has_note> ?inscription_has_type"));
        assertTrue(result.contains("?y1 <http://erlangen-crm.org/170309/P106_is_composed_of> ?y5"));
        assertTrue(result.contains("?y5 <http://erlangen-crm.org/170309/P3_has_note> ?inscription_signature"));

        assertFalse(result.contains("?x <http://erlangen-crm.org/170309/P128_carries> ?y2"));
        assertFalse(result.contains("?y2 rdf:type <http://erlangen-crm.org/170309/E34_Inscription>"));
        assertFalse(result.contains("?y4 rdf:type <http://erlangen-crm.org/170309/E34_Inscription>"));
        assertFalse(result.contains("?y4 <http://erlangen-crm.org/170309/P106_is_composed_of> ?y5"));

        System.out.println("The result is: " + result);
    }
}