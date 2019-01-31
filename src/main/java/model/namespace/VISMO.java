package model.namespace;

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

    public final static String REFERENCE = NS + "Reference";

    public final static String REFERENCE_ENTRY = NS + "ReferenceEntry";

    public final static String TITLE = NS + "Title";

    /*
    Relationships
     */

    public final static String HAS_DIGITAL_REPRESENTATION = NS + "hasDigitalRepresentation";

    public final static String DIGITALLY_REPRESENTS = NS + "digitallyRepresents";

    public final static String REFERENCED_BY_ENTRY = NS + "referencedByEntry";

    public final static String IS_ENTRY_IN = NS + "isEntryIn";

    public final static String CONTAINS_ENTRY = NS + "containsEntry";

    public final static String ENTRY_IS_ABOUT = NS + "entryIsAbout";

    /*
    Properties
     */

    public final static String TECHNICAL_METADATA = NS + "technicalMetadata";

    public final static String ICONOGRAPHY = NS + "iconography";

    public final static String KEYWORD = NS + "keyword";

    public final static String ENTRY_PAGES = NS + "entryPages";

    public final static String SUPERORDINATE_TITLE = NS + "superordinateTitle";
}
