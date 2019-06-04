package model.namespace;

/**
 * Created by Manu on 18.12.18.
 */
public class CIDOC {

    public final static String NS = "http://erlangen-crm.org/170309/";

    public final static String PREFIX = "crm";

    /*
    Classes
     */

    public final static String E1_CRM_ENTITY = NS + "E1_CRM_Entity";

    public final static String E77_PERSISTENT_ITEM = NS + "E77_Persistent_Item";

    public final static String E39_ACTOR = NS + "E39_Actor";

    public final static String E74_GROUP = NS + "E74_Group";

    /*
    Relationships
     */

    public final static String P1_IS_IDENTIFIED_BY = NS + "P1_is_identified_by";

    public final static String P2_HAS_TYPE = NS + "P2_has_type";

    public final static String P7_TOOK_PLACE_AT = NS + "P7_took_place_at";


    /*
    Properties
     */

    public final static String P3_HAS_NOTE = NS + "P3_has_note";

}
