package rest.web.controller;

import org.springframework.stereotype.Controller;

@Controller
public class DigitalRepresentationController {

    // TODO (Christian) Controller für die DigitalRepresentation Requirements (siehe unten). Nimmt alle HTTP Anfragen entgegen und gibt dementsprechend Aufrufe an den Service weiter

    /*
    HTTP Requirements (neu, Stand 8.11.18):
    Standard-Pfad: https://database.visit.uni-passau.de/api/
    - GET:  Gegeben objectID, liefere alle technischen Metadaten    Pfad: standard/object
    - GET:  Gegeben medienID, liefere entsprechende (einzelne) technische Metadata  Pfad: standard/media
    - POST: Gegeben objectID, erzeuge neuen DigitalRepresentation Knoten und liefere medienID (ID des Knotens) zurück   Pfad: standard/object
    - PUT:  Gegeben medienID und neues JSON (technische Metadaten), update entsprechende technische Metadaten   Pfad: standard/media
     */
}
