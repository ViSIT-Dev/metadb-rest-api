package model;

/**
 * (Use Case-eeduced) Static namespace Class for the VISMO ontology.
 */
public class VISMO {

    public final static String NS = "http://visit.de/ontologies/vismo/";

    public final static String PREFIX = "vismo";

    /*
    Classes
     */

    public final static String RESOURCE = NS + "Resource";

    public final static String DIGITAL_REPRESENTATION = NS + "DigitalRepresentation";


    /*
    Relationships
     */

    public final static String HAS_DIGITAL_REPRESENTATION = NS + "hasDigitalRepresentation";

    /*
    Properties
     */

    public final static String TECHNICAL_METADATA = NS + "technicalMetadata";
}
