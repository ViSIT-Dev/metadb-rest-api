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

    public final static String GROUP = NS + "Group";

    /*
    Relationships
     */

    public final static String HAS_DIGITAL_REPRESENTATION = NS + "hasDigitalRepresentation";

    public final static String DIGITALLY_REPRESENTS = NS + "digitallyRepresents";

    /*
    Properties
     */

    public final static String TECHNICAL_METADATA = NS + "technicalMetadata";

    public final static String ICONOGRAPHY = NS + "iconography";

    public final static String KEYWORD = NS + "keyword";
}
