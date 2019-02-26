package rest.persistence.util;

import org.junit.Test;
import rest.application.exception.IdMapperException;

import static org.junit.Assert.*;

/**
 * Test suite for the IdMapper Class.
 */
public class IdMapperTest {

    private final static String BASE_ID_ONE = "1";
    private final static String BASE_ID_TWO = "2";
    private final static String BASE_ID_THREE = "3";

    private final static String MATCHING_REFERENCE_ID = "1";
    private final static String MATCHING_REFERENCE_ID_2 = "2";
    private final static String NOT_MATCHING_REFERENCE_ID = "4";

    private final static String GENERIC_TYPE = "type";

    @Test(expected = IdMapperException.class)
    public void testClashingBaseIds() throws IdMapperException {
        IdMapper mapper = new IdMapper();

        mapper.addBaseID(BASE_ID_ONE, GENERIC_TYPE);
        mapper.addBaseID(BASE_ID_ONE, GENERIC_TYPE);
    }

    @Test
    public void testSimpleBaseIds() throws IdMapperException {
        IdMapper mapper = new IdMapper();

        String baseId1 = mapper.addBaseID(BASE_ID_ONE, GENERIC_TYPE);
        String baseId2 = mapper.addBaseID(BASE_ID_TWO, GENERIC_TYPE);
        String baseId3 = mapper.addBaseID(BASE_ID_THREE, GENERIC_TYPE);

        assertNotEquals(baseId1, baseId2);
        assertNotEquals(baseId2, baseId3);
        assertNotEquals(baseId3, baseId1);
    }

    @Test
    public void testReferencedId() throws IdMapperException {
        IdMapper mapper = new IdMapper();

        String baseId1 = mapper.addBaseID(BASE_ID_ONE, GENERIC_TYPE);

        String referenceId1 = mapper.addReferenceID(MATCHING_REFERENCE_ID);

        assertEquals(baseId1, referenceId1);
        assertTrue(mapper.mappedErrors().isEmpty());
    }

    @Test
    public void testReferencedIdWithReverseOrder() throws IdMapperException {
        IdMapper mapper = new IdMapper();

        String referenceId1 = mapper.addReferenceID(MATCHING_REFERENCE_ID);

        String baseId1 = mapper.addBaseID(BASE_ID_ONE, GENERIC_TYPE);

        assertEquals(baseId1, referenceId1);
        assertTrue(mapper.mappedErrors().isEmpty());
    }

    @Test
    public void testNotMatchingReferenceId() throws IdMapperException {
        IdMapper mapper = new IdMapper();

        String baseId1 = mapper.addBaseID(BASE_ID_ONE, GENERIC_TYPE);

        String referenceId1 = mapper.addReferenceID(NOT_MATCHING_REFERENCE_ID);

        assertNotEquals(baseId1, referenceId1);
        assertFalse(mapper.mappedErrors().isEmpty());
    }

    @Test
    public void testIdMapper() throws IdMapperException {
        IdMapper mapper = new IdMapper();

        String matchingReferenceId1 = mapper.addReferenceID(MATCHING_REFERENCE_ID);

        String baseId1 = mapper.addBaseID(BASE_ID_ONE, GENERIC_TYPE);

        String baseId2 = mapper.addBaseID(BASE_ID_TWO, GENERIC_TYPE);

        String notMatchingReferenceId = mapper.addReferenceID(NOT_MATCHING_REFERENCE_ID);

        String baseId3 = mapper.addBaseID(BASE_ID_THREE, GENERIC_TYPE);

        String matchingReferenceId2 = mapper.addReferenceID(MATCHING_REFERENCE_ID_2);

        assertEquals(baseId1, matchingReferenceId1);
        assertEquals(baseId2, matchingReferenceId2);
        assertNotEquals(baseId1, baseId3);
        assertFalse(mapper.mappedErrors().isEmpty());
    }

}