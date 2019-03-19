package util.excel;

import com.sun.org.apache.regexp.internal.RE;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * This Class comprises several input components for the to be generated Excel Template.
 */
public class ExcelTemplateContent {

    public final static LinkedList<String> ENTITIES = new LinkedList<>(Arrays.asList("Activity", "Group", "Architecture", "Reference", "Object", "Institution", "Person", "Place"));

    private final static String[][] ACTIVITY_FIELDS = {
            {"Activity"},
            {"Titel(String)"},
            {"Beschreibung(String)"},
            {"Datierung", "Taggenaue Datierung(String)", "Freie Datierung(String)", "Beginn des Datierungszeitraums(String)","Ende des Datierungszeitraums(String)"},
            {"Ort(Place)"},
            {"Beteiligte Personen(Person)"},
            {"Beteiligte Bauwerke(Architecture)"},
            {"Beteiligtes Objekt(Object)"},
            {"Schlagwort(String)"},
            {"Kommentar(String)"},
            {"Literaturangaben", "Kurztitel(Reference)", "Seiten(String)"},
            {"Hilfreiche Links(String)"}
    };

    private final static String[][] GROUP_FIELDS = {
            {"Group"},
            {"Name(String)"},
            {"Werkstatt(Object)"},
            {"Besitz, Bauwerk(Architecture)"},
            {"In Auftrag gegebene Bauwerke(Architecture)"},
            {"Ausgeführte Bauwerke(Architecture)"},
            {"Beteiligt an, Bauwerk(Architecture)"},
            {"In Auftrag gegebene Baumaßnahmen(String)"},
            {"Ausgeführte Baumaßnahmen(String)"},
            {"Beteiligt an, Baumaßnahmen(String)"},
            {"Im Besitz befindliche Objekte(Object)"},
            {"Ehemals im Besitz befindliche Objekte(Object)"},
            {"Schlagwort(String)"},
            {"Literaturangaben", "Kurztitel(Reference)", "Seiten(String)"}
    };

    private final static String[][] ARCHITECTURE_FIELDS = {
            {"Architecture"},
            {"Name(String)"},
            {"Standort(Place)"},
            {"Geographische Zuordnung(String)"},
            {"Kirchliche Gliederung(String)"},
            {"Ordenszugehörigkeit(String)"},
            {"Sakralbau(String)"},
            {"Profanbau(String)"},
            {"Innere Baubeschreibung(String)"},
            {"Äußere Baubeschreibung(String)"},
            {"Funktion(String)"},
            {"Geschichte(String)"},
            {"Ereignis(Activity)"},
            {"Baugeschichte(String)"},
            {"Bau", "Datierung[5]", "Entstehung:Beginn des Datierungszeitraums(String)", "Entstehung: Ende des Datierungszeitraums(String)", "Entstehung: Freie Datierung(String)", "Entstehung: Jahrhundert(String)", "Auftraggeber Person(Person)", "Ausführende Personen(Person)", "Beteiligte Personen(Person)", "Auftraggeber Gruppe(Group)", "Ausführende Gruppe(Group)", "Beteiligte Gruppe(Group)"},
            {"Bauphasen", "Titel(String)", "Datierung[5]", "Beginn der Bauphase(String)", "Ende der Bauphase(String)", "Freie Datierung(String)", "Jahrhundert(String)", "Beschreibung(String)", "Funktion(String)", "Auftraggeber Person(Person)", "Ausführende Personen(Person)", "Beteiligte Personen(Person)", "Auftraggeber Gruppe(Group)", "Ausführende Gruppe(Group)", "Beteiligte Gruppe(Group)", "Kommentar(String)"},
            {"Enthaltene Bauteile(Architecture)"},
            {"Bauteil von(Architecture)"},
            {"Eigentümer/Verwalter Person(Person)"},
            {"Eigentümer/Verwalter Gruppe(Group)"},
            {"Eigentümer/Verwalter Institution(Institution)"},
            {"Enthaltene Objekte(Object)"},
            {"Kommentar(String)"},
            {"Schlagwort(String)"},
            {"Literaturangaben", "Kurztitel(Reference)", "Seiten(String)"},
            {"Hilfreiche Links(String)"},
            {"Darstellung(Object)"}
    };

    private final static String[][] REFERENCE_FIELDS = {
            {"Reference"},
            {"Titelangaben", "Übergeordneter Titel(String)", "Titel(String)"},
            {"Typ(String)"},
            {"Autor, Jahr, Ort - Kurztitel", "Autor(Person)", "Erscheinungsort(Place)", "Erscheinungsjahr(String)"},
            {"Herausgeber(String)"},
            {"Bandzahl(String)"},
            {"Reihe(String)"},
            {"Institution(Institution)"},
            {"Ort der Ausstellung(Institution)"},
            {"Jahr der Ausstellung", "Beginn der Ausstellung(String)", "Ende der Ausstellung(String)"},
            {"Seite/n(String)"},
            {"Verweis auf", "Verweis auf Objekt(Object)", "Verweis auf Bauwerk(Architecture)", "Verweis auf Gruppe(Group)", "Verweis auf Person(Person)", "Verweis auf Ereignis(Activity)", "Verweis auf Ort(Place)", "Seite/n(String)"},
            {"Schlagwort(String)"}
    };

    private final static String[][] OBJECT_FIELDS = {
            {"Object"},
            {"Titel(String)"},
            {"Objektbezeichnung(String)"},
            {"Funktion(String)"},
            {"Allgemeine Objektdatierung", "Freie Datierung(String)", "Beginn des Datierungszeitraums(String)", "Ende des Datierungszeitraums(String)", "Jahrhundert(String)"},
            {"Objekt besteht aus Teilobjekten(Object)"},
            {"Teilobjekt von(Object)"},
            {"Beschreibung(String)"},
            {"Herstellung", "Künstler(Person)", "Werkstatt(Group)", "Datierung[5]", "Entstehungszeit: Freie Datierung(String)", "Entstehungszeit: Beginn des Datierungszeitraums(String)", "Entstehungszeit: Ende des Datierungszeitraums(String)", "Entstehungszeit: Jahrhundert(String)", "Herstellungsort(Place)", "Material(String)", "Technik(String)"},
            {"Masse", "Abmessung(String)", "Wert(String)"},
            {"Inschrift", "Art(String)", "Anbringung(String)", "Datierung(String)", "Signatur(String)", "Text(String)"},
            {"Institution/Eigentümer/Verwalter(Institution)"},
            {"Inventarnummer(String)"},
            {"Standort - Museum(Institution)"},
            {"Standort - Bauwerk(Architecture)"},
            {"Provenienz", "Datierung des Besitzerwechsels[6]", "Taggenaue Datierung(String)", "Freie Datierung(String)", "Beginn des Datierungszeitraums(String)", "Ende des Datierungszeitraums(String)", "Jahrhundert(String)", "Eigentümer - Person(Person)", "Eigentümer - Gruppe(Group)", "Vorbesitzer - Person(Person)", "Vorbesitzer - Gruppe(Group)"}, // TODO Taggenau to Date?
            {"Darstellung(String)"},
            {"Objektbezug(Activity)"},
            {"Person(Person)"},
            {"Abgebildetes Bauwerk(Architecture)"},
            {"Abgebildeter Ort(Place)"},
            {"Abgebildete Aktivität(Activity)"},
            {"Schlagwort(String)"},
            {"Kommentar(String)"},
            {"Literaturangaben", "Kurztitel(Reference)", "Seiten(String)"},
            {"Hilfreiche Links(String)"}
    };

    private final static String[][] INSTITUTION_FIELDS = {
            {"Institution"},
            {"Name(String)"},
            {"Bauwerk(Architecture)"},
            {"Ort(Place)"},
            {"Adresse(String)"},
            {"Ausstellung(Reference)"},
            {"Katalog(Reference)"},
            {"Hilfreiche Links(String)"}
    };

    private final static String[][] PERSON_FIELDS = {
            {"Person"},
            {"Vorname(String)"},
            {"Nachname(String)"},
            {"Künstlername(String)"},
            {"Notname(String)"},
            {"Bezeichnung(String)"},
            {"Geburt", "Geburtsdatum[5]", "Taggenaue Datierung(String)", "Freie Datierung(String)", "Beginn des Datierungszeitraums(String)", "Ende des Datierungszeitraums(String)", "Geburtsort(Place)", "Mutter(Person)", "Vater(Person)"},
            {"Tod", "Sterbedatum[5]", "Taggenaue Datierung(String)", "Freie Datierung(String)", "Beginn des Datierungszeitraums(String)", "Ende des Datierungszeitraums(String)", "Sterbeort(Place)"},
            {"Amt/Beruf(String)"},
            {"Titel(String)"},
            {"Beschreibung(String)"},
            {"Ehe", "Ehepartner(Person)", "Beginn der Ehe[5]", "Taggenaue Datierung(String)", "Freie Datierung(String)", "Beginn des Datierungszeitraums(String)", "Ende des Datierungszeitraums(String)", "Ende der Ehe[5]", "Taggenaue Datierung(String)", "Freie Datierung(String)", "Beginn des Datierungszeitraums(String)", "Ende des Datierungszeitraums(String)"},
            {"Kinder(Person)"},
            {"Verwaltung/Eigentum(Architecture)"},
            {"Teilnahme(Activity)"},
            {"In Auftrag gegebene Bauwerke(Architecture)"},
            {"Ausgeführte Bauwerke(Architecture)"},
            {"Beteiligt an - Bauwerke(Architecture)"},
            {"In Auftrag gegebene Baumaßnahmen(String)"},
            {"Ausgeführte Baumaßnahmen(String)"},
            {"Beteiligt an - Baumaßnahmen(String)"},
            {"Im Besitz befindliche Objekte(Object)"},
            {"Ehemals im Besitz befindliche Objekte(Object)"},
            {"Schlagwort(String)"},
            {"Kommentar(String)"},
            {"Literaturangaben", "Kurztitel(Reference)", "Seiten(String)"},
            {"Hilfreiche Links(String)"},
            {"Objektbezug(Object)"}
    };

    private final static String[][] PLACE_FIELDS = {
            {"Place"},
            {"Ortsname(String)"},
            {"Beschreibung(String)"},
            {"Bauwerk(Architecture)"},
            {"Ereignis(Activity)"},
            {"Geburtsort von Personen(Person)"},
            {"Sterbeort von Personen(Person)"},
            {"Schlagwort(String)"},
            {"Kommentar(String)"},
            {"Literaturangaben", "Kurztitel(Reference)", "Seiten(String)"},
            {"Hilfreiche Links(String)"},
            {"Abbildung(Object)"}
    };

    final static List<String[][]> SHEETS = new LinkedList<String[][]>(Arrays.asList(ACTIVITY_FIELDS, GROUP_FIELDS, ARCHITECTURE_FIELDS, REFERENCE_FIELDS, OBJECT_FIELDS, INSTITUTION_FIELDS, PERSON_FIELDS, PLACE_FIELDS));
}
