package rest.persistence.repository;

import com.github.anno4j.Anno4j;
import com.github.anno4j.model.impl.ResourceObject;
import com.github.anno4j.querying.QueryService;
import model.Resource;
import model.namespace.VISMO;
import org.apache.marmotta.ldpath.parser.ParseException;
import org.openrdf.query.*;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.object.ObjectConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import rest.application.exception.MetadataQueryException;

import java.util.List;

@Repository
public class Anno4jRepository {

    @Autowired
    private Anno4j anno4j;

    public Anno4jRepository() {
    }

    /**
     * ATM mainly here for test purposes..
     */
    public Anno4j getAnno4j() {
        return this.anno4j;
    }

    /**
     * Method to query an object Id by supporting a WissKI view path that corresponds to the object from the underlying triplestore.
     * This path is connected to the respective RDF resource via an owl:sameAs relationship.
     *
     * @param wisskiPath    The WissKI view path for the object to query for.
     * @return              The Id of the object that corresponds to the supported WissKI view path.
     * @throws RepositoryException
     * @throws MalformedQueryException
     * @throws QueryEvaluationException
     */
    public String getObjectIdByWisskiPath(String wisskiPath) throws RepositoryException, MalformedQueryException, QueryEvaluationException {

        String query = "SELECT ?id\n" +
                "WHERE {\n" +
                " ?wid <http://www.w3.org/2002/07/owl#sameAs> ?id .\n" +
                "  \n" +
                " FILTER regex(str(?wid), \"^ADD_ID_HERE$\", \"\")\n" +
                "}";

        query = query.replace("ADD_ID_HERE", wisskiPath);

        ObjectConnection objectConnection = this.anno4j.getObjectRepository().getConnection();
        TupleQuery tupleQuery = objectConnection.prepareTupleQuery(query);
        TupleQueryResult evaluateTupleQuery = tupleQuery.evaluate();

        BindingSet currentResult = evaluateTupleQuery.next();

        return String.valueOf(currentResult.getValue("id"));
    }

    /**
     * Method to retrieve the most specified Class/rdf:Class as String of a resource or entity that is defined by the supported id.
     *
     * @param id    The id of the entity/resource for which the most specific type or class (as String) is to be found.
     * @return      The most specific class or type as String for the resource defined by the supported id.
     * @throws RepositoryException
     */
    public String getLowestClassGivenIdAsString(String id) throws RepositoryException, MalformedQueryException, QueryEvaluationException {
        String annotation = this.anno4j.getConcept(id).getAnnotations()[0].toString();

        return this.selectValue(annotation);
    }

    /**
     * Method to retrieve the most specified Class/rdf:Class of a resource or entity that is defined by the supported id.
     *
     * @param id    The id of the entity/resource for which the most specific type or class is to be found.
     * @return      The most specific class or type for the resource defined by the supported id.
     * @throws RepositoryException
     */
    public Class<? extends ResourceObject> getLowestClassGivenId(String id) throws RepositoryException {
        return this.anno4j.getConcept(id);
    }

    /**
     * Method queries for the ID of the superordinate vismo:Resource of the DigitalRepresentation defined by the supported digRepId.
     *
     * @param digRepId The Id of the DigitalRepresentation node.
     * @return The Id of the superordinate vismo:Resource.
     */
    public String getResourceIdByDigitalRepresentation(String digRepId) throws RepositoryException, ParseException, MalformedQueryException, QueryEvaluationException {
        QueryService queryService = this.anno4j.createQueryService();

        queryService.addPrefix(VISMO.PREFIX, VISMO.NS);

        queryService.addCriteria("<" + VISMO.HAS_DIGITAL_REPRESENTATION + ">", digRepId);

        List<Resource> result = queryService.execute(Resource.class);

        return result.get(0).getResourceAsString();
    }

    /**
     * Method to retrieve the objectId of the entity that is connected to the vismo:DigitalRepresentation entity defined by the supported digRepId.
     *
     * @param digRepId  The id that defines the vismo:DigitalRepresentation entity, for which the associated object entity is to be found.
     * @return          The id of the object entity that is connected to the supported vismo:DigitalRepresentation.
     * @throws MetadataQueryException
     */
    public String getResourceIdByDigitalRepresentationSPARQL(String digRepId) throws MetadataQueryException {
        String query = "SELECT ?resource\n" +
                "WHERE { ?resource <http://visit.de/ontologies/vismo/hasDigitalRepresentation> <" + digRepId + "> }";

        ObjectConnection connection = null;
        try {
            connection = this.anno4j.getObjectRepository().getConnection();

            TupleQuery tupleQuery = connection.prepareTupleQuery(query);

            TupleQueryResult result = tupleQuery.evaluate();

            BindingSet next = result.next();

            return String.valueOf(next.getValue("resource"));
        } catch (RepositoryException | MalformedQueryException | QueryEvaluationException e) {
            throw new MetadataQueryException("Somethin went wrong when querying the objectId of a resource, given a mediaId.");
        }

    }

    /**
     * Method get a whole Java-Annotation as input and retrieves and returns its set value.
     *
     * @param annotation The Annotation-String to read the value from.
     * @return The value set for the input Annotation-String.
     */
    private String selectValue(String annotation) {
        int value = annotation.indexOf("value=");

        return annotation.substring(value + 6, annotation.length() - 1);
    }
}
















