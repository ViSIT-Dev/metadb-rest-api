package rest.persistence.repository;

import com.github.anno4j.Anno4j;
import model.VISMO;
import model.technicalMetadata.DigitalRepresentation;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.repository.object.ObjectQuery;
import org.openrdf.repository.object.RDFObject;
import org.openrdf.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.LinkedList;
import java.util.List;

@Repository
public class DigitalRepresentationRepository {

    @Autowired
    private Anno4j anno4j;

    // TODO (Christian) Funktionalität für die DigitalRepresentation Queries implementieren
    //              - Gegeben Object-ID, liefere alle Strings zurück, die als technische Metadaten eines DigitalRepresentation-Objekts vorliegen
    //              - Gegeben Object-ID, lösche alle aktuellen technischen Metadaten Strings und ersetze sie mit den der Request mitgegebenen Strings

    /**
     * Private method that queries the technicalMetadata Strings given an ID of a vismo:Resource entity.
     *
     * @param id    The ID of the vismo:Resource from which the technicalMetadata is to be queried.
     * @return      A list of Strings that represent the technicalMetadata for the searched vismo:Resource entity.
     */
    public List<String> getTechnicalMetadataStrings(String id) throws RepositoryException, MalformedQueryException, QueryEvaluationException {
        ObjectConnection connection = this.anno4j.getObjectRepository().getConnection();

        String queryString = "SELECT ?d\n" +
                "   WHERE { <" + id + "> a <" + VISMO.RESOURCE + "> .\n" +
                "   <" + id + "> <" + VISMO.HAS_DIGITAL_REPRESENTATION + "> ?d }";

        ObjectQuery query = connection.prepareObjectQuery(queryString);

        Result<RDFObject> result = query.evaluate(RDFObject.class);

        List<RDFObject> list = result.asList();

        List<String> technicalMetadatas = new LinkedList<>();

        for(RDFObject object : list) {
            technicalMetadatas.add(((DigitalRepresentation) object).getTechnicalMetadata());
        }

        return technicalMetadatas;
    }
}
