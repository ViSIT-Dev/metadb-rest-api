package util.excel;

import com.sun.org.apache.regexp.internal.RE;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * This Class comprises several input components for the to be generated Excel Template.
 */
public class ExcelTemplateContent {


    private final static String[][] ACTIVITY_FIELDS = {
            {"Activity"},
            {"Titel(String)"},
            {"Beschreibung(String)"},
            {"Datierung", "Taggenaue Datierung(Date)", "Freie Datierung(String)", "Beginn des Datierungszeitraums(String)","Ende des Datierungszeitraums(String)"},
            {"Ort(Architecture)"},
            {"Beteiligte Personen(Person)"},
            {"Beteiligte Bauwerke(Architecture)"},
            {"Beteiligtes Objekt(Object)"},
            {"Schlagwort(String)"},
            {"Kommentar(String)"},
            {"Literaturangaben", "Kurztitel(Reference)", "Seiten(String)"},
            {"Hilfreiche Links(String)"}
    };

    private final static String[][] GROUP_FIELDS = {
            {"Group"}
    };

    private final static String[][] ARCHITECTURE_FIELDS = {
            {"Architecture"}
    };

    private final static String[][] REFERENCE_FIELDS = {
            {"Reference"}
    };

    private final static String[][] OBJECT_FIELDS = {
            {"Object"}
    };

    private final static String[][] INSTITUTION_FIELDS = {
            {"Institution"}
    };

    private final static String[][] PERSON_FIELDS = {
            {"Person"}
    };

    private final static String[][] PLACE_FIELDS = {
            {"Place"}
    };

    final static List<String[][]> SHEETS = new LinkedList<String[][]>(Arrays.asList(ACTIVITY_FIELDS, GROUP_FIELDS, ARCHITECTURE_FIELDS, REFERENCE_FIELDS, OBJECT_FIELDS, INSTITUTION_FIELDS, PERSON_FIELDS, PLACE_FIELDS));
}
