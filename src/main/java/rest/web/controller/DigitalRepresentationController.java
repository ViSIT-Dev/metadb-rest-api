package rest.web.controller;

import org.apache.marmotta.ldpath.parser.ParseException;
import org.openrdf.OpenRDFException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import rest.Exception.DigitalRepositoryException;
import rest.service.DigitalRepresentationService;

import java.util.List;

// TODO Pfad für Mapping: Habe gelesen, dass wir "https://database.visit.uni-passau.de/" nicht angeben müssen, d.h. mapping für diesen Controller ist nur "/api..."
@RestController
//@RequestMapping(value="https://database.visit.uni-passau.de/api/")
public class DigitalRepresentationController {
    @Autowired private DigitalRepresentationService digitalRepresentationService;
    // TODO (Christian) Controller für die DigitalRepresentation Requirements. Nimmt alle HTTP Anfragen entgegen und gibt dementsprechend Aufrufe an den Service weiter

    // TODO Bitte die IDs als Parameter des Requests, nicht als Pfad-Variable übergeben
    /*
     Bezug IDs unserer Metadatenobjekte:
     Ich befürchte, dass wir die IDs nicht direkt als PathVariable übergeben können. Die IDs, mit denen wir arbeiten,
     sehen in der Regel irgendwie so aus: http://visit.de/model/a3k4jdnapwiej.
     Das heißt, dass wir hier ja teils URI Fragmente darin haben. Wenn wir das jetzt in ner Pfad-Variable verwursteln
     könnte das zu Probleme führen, weil die eigentlich URL dann ja nicht mehr zu erkennen ist.
      */

    // TODO Bitte nachschauen, was Spring macht wenn wir einen String zurück geben. Könnte nur zur Navigation führen...

    /**
     *
     * @param id Gives the Media id for Access on the Repository
     * @return returns a String of the Metadata found.
     * @throws DigitalRepositoryException
     */
    // TODO Mapping anpassen, es reicht get auf "/media/{id}" (falls ID als Pfad-Variable klappt, siehe oben)
    @GetMapping(value="/api/media/{id}")
    public String getSingleTechnicalMetadataByMediaID(@PathVariable("id") String id) {
        return digitalRepresentationService.getSingleTechnicalMetadataByMediaID(id);
    }

    /**
     *
     * @param id Gives the Object id for Access on the repository
     * @return returns a List of Strings of the Metadata found
     * @throws DigitalRepositoryException
     */
    @GetMapping(value="object/{id}")
    public List<String> getAllTechnicalMetadataStringsByObjectID(@PathVariable("id") String id) {
        return digitalRepresentationService.getAllTechnicalMetadataStringsByObjectID(id);
    }



    /*
    HTTP Requirements (neu, Stand 8.11.18):
    Standard-Pfad: https://database.visit.uni-passau.de/api/
    - GET:  Gegeben objectID, liefere alle technischen Metadaten    Pfad: standard/object
    - GET:  Gegeben medienID, liefere entsprechende (einzelne) technische Metadata  Pfad: standard/media
    - POST: Gegeben objectID, erzeuge neuen DigitalRepresentation Knoten und liefere medienID (ID des Knotens) zurück   Pfad: standard/object
    - PUT:  Gegeben medienID und neues JSON (technische Metadaten), update entsprechende technische Metadaten   Pfad: standard/media
     */
}
