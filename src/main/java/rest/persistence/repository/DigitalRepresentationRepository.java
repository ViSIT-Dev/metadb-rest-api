package rest.persistence.repository;

import com.github.anno4j.Anno4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class DigitalRepresentationRepository {

    @Autowired
    private Anno4j anno4j;

    // TODO (Christian) Funktionalität für die DigitalRepresentation Queries implementieren
    //              - Gegeben Object-ID, liefere alle Strings zurück, die als technische Metadaten eines DigitalRepresentation-Objekts vorliegen
    //              - Gegeben Object-ID, lösche alle aktuellen technischen Metadaten Strings und ersetze sie mit den der Request mitgegebenen Strings
}
