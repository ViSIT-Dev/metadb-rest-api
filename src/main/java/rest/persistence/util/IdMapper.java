package rest.persistence.util;

import rest.application.exception.IdMapperException;
import rest.configuration.VisitIDGenerator;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.UUID;

/**
 * Class used by the ImportQueryGenerator for dealing with the to be created IDs.
 * <p>
 * When a data (JSON) import is done, this mapper remembers all base IDs (IDs that are associated with a top level entity
 * like persons, architecture, places, etc.) and all referenced IDs (IDs that refer to another entity as a relationship).
 * <p>
 * A remembered ID will be associated with a URI ID that will later on be used in the update query.
 * <p>
 * When an ID is remembered, first a check is done if this ID has already been used elsewhere. If yes, this reference
 * can instantly be exchanged with the generated URI ID from above. If not, a new entry with generated ID is added.
 * <p>
 * This ensures that at the end (hopefully all) references are matched against one another and that every reference ID
 * is corresponding to a base ID.
 * <p>
 * If there are missing links or reference IDs that are not matched, but no further data is available, this is added
 * to an errors list, which is passed out after the import process to inform the user of what has not worked.
 * <p>
 * While data is read in, these possibilities can happen:
 * - A base ID is read, which ...
 * - is not yet discovered -> Add the ID to known baseIDs, create a URI and issue it in the mappedIDs
 * - is already discovered -> Throw an exception, clashing base IDs are not possible
 * - A reference ID is read, so check the existing baseIDs. If ...
 * - a match is found -> then look up the created URI for the baseID and use this info in the created query
 * - a match is NOT found -> remember the referenceID, as the missing baseID may be added later
 * <p>
 * Type checking doable? Idea: Add datatype to the wrapper.csv then pass it to IdMapper
 */
public class IdMapper {

    /*
     BaseIDs and referenceIDs are persisted as HashMap, which contains the remembered original ID and the type of the
     top level entity or the relationship respectively. This is used for type checking.
      */
    private HashMap<String, String> baseIDs;
    private HashMap<String, String> referenceIDs;

    private HashMap<String, String> mappedIDs;

    public IdMapper() {
        this.baseIDs = new HashMap<String, String>();
        this.referenceIDs = new HashMap<String, String>();

        this.mappedIDs = new HashMap<String, String>();
    }

    public String addBaseID(String baseID, String objectType) throws IdMapperException {
        String uri;

        if (!this.baseIDs.containsKey(baseID)) {
            // ID not yet discovered
            // Check if a respective referenceID exists which was previously read and references this baseID
            if (this.referenceIDs.containsKey(baseID)) {
                uri = this.referenceIDs.get(baseID);

                this.referenceIDs.remove(baseID);
            } else {
                uri = VisitIDGenerator.generateVisitDBID();
            }

            this.baseIDs.put(baseID, objectType);
            this.mappedIDs.put(baseID, uri);
        } else { //TODO
            //throw new IdMapperException("The read baseID <" + baseID + "> clashes and is thereby used more than once, which is not allowed!");
        	System.out.println(baseID + "clashes.");
        	uri = VisitIDGenerator.generateVisitDBID();
        }

        return uri;
    }

    public String addReferenceID(String referenceID) {
        String uri;

        if(this.mappedIDs.containsKey(referenceID)) {
            uri = this.mappedIDs.get(referenceID);
        } else if(this.referenceIDs.containsKey(referenceID)) {
            uri = this.referenceIDs.get(referenceID);
        } else {
            uri = VisitIDGenerator.generateVisitDBID();
            this.referenceIDs.put(referenceID, uri);
        }

        return uri;
    }

    public LinkedList<String> mappedErrors() {
        LinkedList<String> errors = new LinkedList<String>();

        if(!this.referenceIDs.isEmpty()) {
            for(String key : this.referenceIDs.keySet()) {
                errors.add("Reference ID " + key + " is not mapped to any existing entity!");
            }
        }

        return errors;
    }


}





















