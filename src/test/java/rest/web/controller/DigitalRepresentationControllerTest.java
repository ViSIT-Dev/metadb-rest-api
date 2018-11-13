package rest.web.controller;

import com.github.anno4j.Anno4j;
import model.Resource;
import model.technicalMetadata.DigitalRepresentation;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfigException;
import org.springframework.test.web.servlet.MockMvc;
import rest.BaseWebTest;
import rest.Exception.DigitalRepositoryException;
import rest.persistence.repository.DigitalRepresentationRepository;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.junit.Assert.*;

/**
 * Test Class with methods in order to test the DigitalRepresentationController.
 */
public class DigitalRepresentationControllerTest extends BaseWebTest {

    // TODO (Christian) Bitte hier tests entwickeln, die die Funktionalitäten um die DigitalRepresentation Requirements abdecken
    private String objectID;
    private String mediaID1;
    private String mediaID2;
    private String urlLikeID = "http://visit.de/model/id1";

    /*
    Darum brauchst Du dich nicht kümmern, das macht alles die mockMVC Konfiguration.
     */
//    private DigitalRepresentationController digitalRepresentationController = new DigitalRepresentationController();
//    private DigitalRepresentationRepository digitalRepresentationRepository;

    private final String standardUrl = "https://database.visit.uni-passau.de/api/";

    /**
     * Testmethod, expects a DigitalRepositoryException to come with a false given MediaID
     * @throws Exception
     */
   @Test(expected = DigitalRepositoryException.class)
    public void getSingleTechnicalMetadataByFalseMediaIDTest() throws Exception{
//       /*Erstelle einen zufälligen Alphanumerischen String mit Länge 47*/
//        String random = RandomStringUtils.randomAlphanumeric(47);
//        /*Erstelle Simulation der Klasse DigitalRepresentationRepository falls diese später aufgerufen wird*/
//        DigitalRepresentationRepository digitalRepresentationRepository = mock(DigitalRepresentationRepository.class);
//        /*falls Anno4J angefragt wird gebe ein neues anno4J Object zurück*/
//        when(digitalRepresentationRepository.getAnno4j()).thenReturn(this.anno4j);
//        //teste den Repository
//        given(digitalRepresentationRepository.getSingleTechnicalMetadataByMediaID(this.mediaID1)).willReturn("test1");
//        /*Teste den Controller*/
//        this.getMockMvc().perform(get(standardUrl+"media/"+random));

       /*
        Das Object mit deiner ID "random" kannst du nicht abfragen, da es nicht im Datenstand hinterlegt ist.
        Dies kannst Du in der Methode createTestModel tun. Dort sind bereits eine Resource und mittlerweile 3 verschiedene
        DigitalRepresentation Objekte drin.
        */
    }

    @Test
    public void getSingleTechnicalMetadataByExistingIDTest() throws Exception {
        /*
        Kurzer kleiner Test, um das Vorhandensein eines Objekts zu fragen. Du musst Dich mit dem mockMVC eigentlich
        nicht um Repositories oder Controller zu kümmern, diese kennt er automatisch aus der Konfiguration.

        Dementsprechend ist der Testablauf für Controller: Daten in der createTestModel und dann einen Request mit dem
        mockMVC zusammen bauen, der Deine entsprechende Funktion des Controllers aufruft.

        Im Moment printed der Test nur die Ausgabe, die zurück kommt. Das kann man dann noch aufbohren, um z.B. Inhalte
        des Response JSONs zu untersuchen.

        Der Request müsste durchlaufen, wirft aber zu irgend einem Zeitpunkt Deine Exception.
        Korrektur: Wirft NICHT deine Exception, sondern läuft durch :)
        ABER: Siehe nächsten Test. Problem mit der übergebenen ID.

        Zusatzerkenntnis: Im Body steht direkt der technische Metadaten-String. Scheint also zu klappen, diese so rauszugeben.
        Mal sehen ob das mit mehreren Strings auch so klappt.
         */

        String requestURL = standardUrl + "media/" + mediaID1;
        this.mockMvc.perform(get(requestURL)).andDo(print());
    }

    @Test
    public void testGetSingleTechnicalMetadataByExistingIDWithURLlikeID () throws Exception {
        /*
        Wie vermutet: Mit Anno4j-auto-generierten IDs funktioniert die Pfad-Variable.
        Wenn wir aber eine andere ID setzen, so wie die urlLikeID = "http://visit.de/model/id1", dann klappt die Anfrage nicht
        weil er den Pfad nicht kennt.

        Lass den Test laufen, Du siehst dann in der Ausgabe unter "Request URI" welchen Pfad er anfrägt.
         */

        String requestURL = standardUrl + "media/" + urlLikeID;
        this.mockMvc.perform(get(requestURL)).andDo(print());
    }

    /**
     * Testmethod, expects a List to come with a ObjectID given
     */
    @Test
    public void getAllTechnicalMetadataStringsByObjectIDTest(){

    }

    @Override
    public void createTestModel() throws RepositoryException, IllegalAccessException, InstantiationException {
        /*
         Alle Repositories sind in der "BaseWebTest" Klasse definiert. Auf diese kannst Du also von überall aus drauf
         zugreifen, um entsprechende Testdaten einzufügen. Hier haben wir also eine Resource (ID: objectID) mit zwei
         DigitalRepresentation Objekten (IDs: mediaID1 und mediaID2).
         Auf diese Daten solltest Du dann Deine Tests aufbauen.
          */

        Anno4j anno4j = this.digitalRepresentationRepository.getAnno4j();

        Resource resource = anno4j.createObject(Resource.class);
        this.objectID = resource.getResourceAsString();

        DigitalRepresentation rep1 = anno4j.createObject(DigitalRepresentation.class);
        this.mediaID1 = rep1.getResourceAsString();
        String test = "test1";
        rep1.setTechnicalMetadata(test);

        DigitalRepresentation rep2 = anno4j.createObject(DigitalRepresentation.class);
        this.mediaID2 = rep2.getResourceAsString();
        String test2 = "test2";
        rep2.setTechnicalMetadata(test2);

        /*
        So erstellst Du ein Objekt mit eigens gewählter ID (hinten per "urlLikeID", kann aber auch beliebiger String sein).
        Da es eine Semantic Web URI sein muss, brauchen wir gewisse URL Requirements, http, slashes, etc.
         */
        DigitalRepresentation repWithURLlikeID = anno4j.createObject(DigitalRepresentation.class, (org.openrdf.model.Resource) new URIImpl(urlLikeID));
        repWithURLlikeID.setTechnicalMetadata("urlLikeIDtechMetadata");

        resource.addDigitalRepresentation(rep1);
        resource.addDigitalRepresentation(rep2);
        resource.addDigitalRepresentation(repWithURLlikeID);
    }
}