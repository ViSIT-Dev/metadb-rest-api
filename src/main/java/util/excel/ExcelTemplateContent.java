package util.excel;

import com.sun.org.apache.regexp.internal.RE;

import java.util.Arrays;
import java.util.HashMap;
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
            {"Ausgef�hrte Bauwerke(Architecture)"},
            {"Beteiligt an, Bauwerk(Architecture)"},
            {"In Auftrag gegebene Bauma�nahmen(String)"},
            {"Ausgef�hrte Bauma�nahmen(String)"},
            {"Beteiligt an, Bauma�nahmen(String)"},
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
            {"Ordenszugeh�rigkeit(String)"},
            {"Sakralbau(String)"},
            {"Profanbau(String)"},
            {"Innere Baubeschreibung(String)"},
            {"�u�ere Baubeschreibung(String)"},
            {"Funktion(String)"},
            {"Geschichte(String)"},
            {"Ereignis(Activity)"},
            {"Baugeschichte(String)"},
            {"Bau", "Datierung - Bau[5]", "Entstehung: Beginn des Datierungszeitraums(String)", "Entstehung: Ende des Datierungszeitraums(String)", "Entstehung: Freie Datierung(String)", "Entstehung: Jahrhundert(String)", "Auftraggeber Person - Bau(Person)", "Ausf�hrende Personen - Bau(Person)", "Beteiligte Personen - Bau(Person)", "Auftraggeber Gruppe - Bau(Group)", "Ausf�hrende Gruppe - Bau(Group)", "Beteiligte Gruppe - Bau(Group)"},
            {"Bauphasen", "Titel(String)", "Datierung - Bauphase[5]", "Beginn der Bauphase(String)", "Ende der Bauphase(String)", "Freie Datierung(String)", "Jahrhundert(String)", "Beschreibung(String)", "Funktion - Bauphase(String)", "Auftraggeber Person - Bauphase(Person)", "Ausf�hrende Personen - Bauphase(Person)", "Beteiligte Personen - Bauphase(Person)", "Auftraggeber Gruppe - Bauphase(Group)", "Ausf�hrende Gruppe - Bauphase(Group)", "Beteiligte Gruppe - Bauphase(Group)", "Kommentar - Bauphase(String)"},
            {"Enthaltene Bauteile(Architecture)"},
            {"Bauteil von(Architecture)"},
            {"Eigent�mer/Verwalter Person(Person)"},
            {"Eigent�mer/Verwalter Gruppe(Group)"},
            {"Eigent�mer/Verwalter Institution(Institution)"},
            {"Enthaltene Objekte(Object)"},
            {"Kommentar(String)"},
            {"Schlagwort(String)"},
            {"Literaturangaben", "Kurztitel(Reference)", "Seiten(String)"},
            {"Hilfreiche Links(String)"},
            {"Darstellung(Object)"}
    };

    private final static String[][] REFERENCE_FIELDS = {
            {"Reference"},
            {"Titelangaben", "�bergeordneter Titel(String)", "Titel(String)"},
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
            {"Herstellung", "K�nstler(Person)", "Werkstatt(Group)", "Datierung[5]", "Entstehungszeit: Freie Datierung(String)", "Entstehungszeit: Beginn des Datierungszeitraums(String)", "Entstehungszeit: Ende des Datierungszeitraums(String)", "Entstehungszeit: Jahrhundert(String)", "Herstellungsort(Place)", "Material(String)", "Technik(String)"},
            {"Masse", "Abmessung(String)", "Wert(String)"},
            {"Inschrift", "Art(String)", "Anbringung(String)", "Datierung(String)", "Signatur(String)", "Text(String)"},
            {"Institution/Eigent�mer/Verwalter(Institution)"},
            {"Inventarnummer(String)"},
            {"Standort - Museum(Institution)"},
            {"Standort - Bauwerk(Architecture)"},
            {"Provenienz", "Datierung des Besitzerwechsels[6]", "Taggenaue Datierung(String)", "Freie Datierung(String)", "Beginn des Datierungszeitraums(String)", "Ende des Datierungszeitraums(String)", "Jahrhundert(String)", "Eigent�mer - Person(Person)", "Eigent�mer - Gruppe(Group)", "Vorbesitzer - Person(Person)", "Vorbesitzer - Gruppe(Group)"}, // TODO Taggenau to Date?
            {"Darstellung(String)"},
            {"Objektbezug(Activity)"},
            {"Person(Person)"},
            {"Abgebildetes Bauwerk(Architecture)"},
            {"Abgebildeter Ort(Place)"},
            {"Abgebildete Aktivit�t(Activity)"},
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
            {"K�nstlername(String)"},
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
            {"Ausgef�hrte Bauwerke(Architecture)"},
            {"Beteiligt an - Bauwerke(Architecture)"},
            {"In Auftrag gegebene Bauma�nahmen(String)"},
            {"Ausgef�hrte Bauma�nahmen(String)"},
            {"Beteiligt an - Bauma�nahmen(String)"},
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

    public final static HashMap<String, String> ACTIVITY_LABEL_ID_MAP  = new HashMap<String, String>() {{
        put("Titel", "activity_idby_title");
        put("Beschreibung", "activity_description");
        put("Datierung - Untergruppe", "activity_dating");
        put("Taggenaue Datierung", "activity_dating | activity_dating_exact");
        put("Freie Datierung", "activity_dating | activity_dating_sometime");
        put("Beginn des Datierungszeitraums", "activity_dating | activity_dating_start");
        put("Ende des Datierungszeitraums", "activity_dating | activity_dating_end");
        put("Ort", "activity_tookplaceat_place");
        put("Beteiligte Personen", "activity_hadparticipant_person");
        put("Beteiligte Bauwerke", "activity_used_architecture");
        put("Beteiligtes Objekt", "activity_used_object");
        put("Schlagwort", "activity_iconography");
        put("Kommentar", "activity_comment");
        put("Literaturangaben - Untergruppe", "activity_refentry");
        put("Kurztitel", "activity_refentry | activity_refentry_in_reference");
        put("Seiten", "activity_refentry | activity_refentry_pages");
        put("Hilfreiche Links", "activity_helpfullinks");
    }};

    public final static HashMap<String, String> GROUP_LABEL_ID_MAP = new HashMap<String, String>() {{
        put("Name", "group_idby_actorappel");
        put("Werkstatt", "group_produced_object");
        put("Besitz, Bauwerk", "group_ownerof_architecture");
        put("In Auftrag gegebene Bauwerke", "group_motiv_arch_production");
        put("Ausgef�hrte Bauwerke", "group_carriedout_arch_production");
        put("Beteiligt an, Bauwerk", "group_infl_arch_production");
        put("In Auftrag gegebene Bauma�nahmen", "group_motiv_structevol");
        put("Ausgef�hrte Bauma�nahmen", "group_carriedout_structevol");
        put("Beteiligt an, Bauma�nahmen", "group_infl_structevol");
        put("Im Besitz befindliche Objekte", "group_receivedcustodyof_object");
        put("Ehemals im Besitz befindliche Objekte", "group_lostcustodyof_object");
        put("Schlagwort", "group_iconography");
        put("Literaturangaben - Untergruppe", "group_refentry");
        put("Kurztitel", "group_refentry | group_refentry_in_reference");
        put("Seiten", "group_refentry | group_refentry_pages");
    }};

    public final static HashMap<String, String> PLACE_LABEL_ID_MAP = new HashMap<String, String>() {{
        put("Ortsname", "place_idby_placeappel");
        put("Beschreibung", "place_description");
        put("Bauwerk", "place_holds_architecture");
        put("Ereignis", "place_witnessed_activity");
        put("Geburtsort von Personen", "place_wasbirthplaceof_person");
        put("Sterbeort von Personen", "place_wasdeathplaceof_person");
        put("Schlagwort", "place_keyword");
        put("Kommentar", "place_comment");
        put("Literaturangaben - Untergruppe", "place_refentry");
        put("Kurztitel", "place_refentry | place_refentry_in_reference");
        put("Seiten", "place_refentry | place_refentry_pages");
        put("Hilfreiche Links", "place_helpfullinks");
        put("Abbildung", "place_isdepictedby_object");
    }};

    public final static HashMap<String, String> PERSON_LABEL_ID_MAP = new HashMap<String, String>() {{
        put("Vorname", "person_firstname");
        put("Nachname", "person_lastname");
        put("K�nstlername", "person_pseudonym");
        put("Notname", "person_alternatename");
        put("Bezeichnung", "person_idby_actorappel");
        put("Geburt - Untergruppe", "person_birth");
        put("Geburtsdatum - Unteruntergruppe", "person_birth | person_birth_dating");
        put("Taggenaue Datierung", "person_birth | person_birth_dating | person_birth_dating_exact");
        put("Freie Datierung", "person_birth | person_birth_dating | person_birth_dating_sometime");
        put("Beginn des Datierungszeitraums", "person_birth | person_birth_dating | person_birth_dating_start");
        put("Ende des Datierungszeitraums", "person_birth | person_birth_dating | person_birth_dating_end");
        put("Geburtsort", "person_birth | person_birthplace");
        put("Mutter", "person_birth | person_mother");
        put("Vater", "person_birth | person_father");
        put("Tod - Untergruppe", "person_death");
        put("Sterbedatum - Unteruntergruppe", "person_death | person_death_dating");
        put("Taggenaue Datierung", "person_death | person_death_dating | person_death_dating_exact");
        put("Freie Datierung", "person_death | person_death_dating | person_death_dating_sometime");
        put("Beginn des Datierungszeitraums", "person_death | person_death_dating | person_death_dating_start");
        put("Ende des Datierungszeitraums", "person_death | person_death_dating | person_death_dating_end");
        put("Sterbeort", "person_death | person_deathplace");
        put("Amt/Beruf", "person_hastype_profession");
        put("Titel", "person_carries_title");
        put("Beschreibung", "person_description");
        put("Ehe - Untergruppe", "person_marriage");
        put("Ehepartner", "marriage_partner_person");
        put("Beginn der Ehe - Unteruntergruppe", "person_marriage | marriage_begin_dating");
        put("Taggenaue Datierung", "person_marriage | marriage_begin_dating | person_marriage_dating_exact");
        put("Freie Datierung", "person_marriage | person_marriage_dating_sometime");
        put("Beginn des Datierungszeitraums", "person_marriage | person_marriage_dating_start");
        put("Ende des Datierungszeitraums", "person_marriage | person_marriage_dating_end");
        put("Ende der Ehe - Unteruntergruppe", "person_marriage | marriage_end_dating");
        put("Taggenaue Datierung", "person_marriage | marriage_end_dating | marriage_end_dating_exact");
        put("Freie Datierung", "person_marriage | marriage_end_dating | marriage_end_dating_sometime");
        put("Beginn des Datierungszeitraums", "person_marriage | marriage_end_dating | marriage_end_dating_start");
        put("Ende des Datierungszeitraums", "person_marriage | marriage_end_dating | marriage_end_dating_end");
        put("Kinder", "person_parentof_person");
        put("Verwaltung/Eigentum", "person_ownerof_architecture");
        put("Teilnahme", "person_participatedin_activity");
        put("In Auftrag gegebene Bauwerke", "person_motiv_arch_production");
        put("Ausgef�hrte Bauwerke", "person_carriedout_arch_prod");
        put("Beteiligt an - Bauwerke", "person_infld_arch_production");
        put("In Auftrag gegebene Bauma�nahme", "person_motiv_structevol");
        put("Ausgef�hrte Bauma�nahmen", "person_carriedout_structevol");
        put("Beteiligt an - Bauma�nahmen", "person_infl_structevol");
        put("Im Besitz befindliche Objekte", "person_receivedcustody_object");
        put("Schlagwort", "person_keyword");
        put("Kommentar", "person_comment");
        put("Literaturangaben - Untergruppe", "person_refentry");
        put("Kurztitel", "person_refentry | person_refentry_in_reference");
        put("Seiten", "person_refentry | person_refentry_pages");
        put("Hilfreiche Links", "person_helpfullinks");
        put("Objektbezug", "person_isdepictedby_object"); //TODO not sure about this one
    }};
    
   
    public final static HashMap<String, String> ARCHITECTURE_LABEL_ID_MAP  = new HashMap<String, String>() {{
    	put("Name","architecture_idby_title");
    	put("Standort","architecture_location_place");
    	put("Geographische Zuordnung","arch_geographicaffiliation");
    	put("Kirchliche Gliederung","arch_bishopricaffiliation");
    	put("Ordenszugeh�rigkeit","arch_orderaffiliation");
    	put("Sakralbau","arch_sacraltype");
    	put("Profanbau","arch_has_seculartype");
    	put("Innere Baubeschreibung","architecture_innerdescription");
    	put("�u�ere Baubeschreibung","architecture_outerdescription");
    	put("Funktion","architecture_exemplify_function");
    	put("Geschichte","architecture_description"); //TODO not sure about this one
    	put("Ereignis","architecture_tookpartin_activity"); 
    	put("Baugeschichte","architecture_buildinghistory");
    	put("Bau - Untergruppe","arch_producedby_production");
    	put("Datierung - Bau - Unteruntergruppe","arch_producedby_production | arch_production_dating");
    	put("Entstehung: Beginn des Datierungszeitraums","arch_producedby_production | arch_production_dating | arch_prod_dating_start");
    	put("Entstehung: Ende des Datierungszeitraums","arch_producedby_production | arch_production_dating | arch_prod_dating_end");
    	put("Entstehung: Freie Datierung","arch_producedby_production | arch_production_dating | archproduction_sometime");
    	put("Entstehung: Jahrhundert","arch_producedby_production | arch_production_dating | arch_prod_dating_century");
    	put("Auftraggeber Person - Bau","arch_producedby_production | production_motivatedby_person");
    	put("Ausf�hrende Personen - Bau","arch_producedby_production | production_carriedoutby_person");
    	put("Beteiligte Personen - Bau","arch_producedby_production | production_inflby_person");
    	put("Auftraggeber Gruppe - Bau","arch_producedby_production | arch_prod_motivatedby_group");
    	put("Ausf�hrende Gruppe - Bau","arch_producedby_production | arch_prod_carriedoutby_group");
    	put("Beteiligte Gruppe - Bau","arch_producedby_production | arch_prod_inflby_group");
    	put("Bauphasen - Untergruppe","arch_modifiedby_structevolution");
    	put("Titel","arch_modifiedby_structevolution | structuralevolution_idby_title");
    	put("Datierung - Bauphase - Unteruntergruppe","arch_modifiedby_structevolution | arch_structevol_dating");
    	put("Beginn der Bauphase","arch_modifiedby_structevolution | arch_structevol_dating | arch_structevol_dating_start");
    	put("Ende der Bauphase","arch_modifiedby_structevolution | arch_structevol_dating | arch_structevol_dating_end");
    	put("Freie Datierung","arch_modifiedby_structevolution | arch_structevol_dating | arch_evol_dat_sometime");
    	put("Jahrhundert","arch_modifiedby_structevolution | arch_structevol_dating | arch_structevol_dating_century");
    	put("Beschreibung","arch_modifiedby_structevolution | structuralevolution_description");
    	put("Funktion - Bauphase","arch_modifiedby_structevolution | structevol_exemplifies_function");
    	put("Auftraggeber Person - Bauphase","arch_modifiedby_structevolution | structevol_motivatedby_person");
    	put("Ausf�hrende Personen - Bauphase","arch_modifiedby_structevolution | structevol_carriedoutby_person");
    	put("Beteiligte Personen - Bauphase","arch_modifiedby_structevolution | structevol_influencedby_person");
    	put("Auftraggeber Gruppe - Bauphase","arch_modifiedby_structevolution | structevol_motivby_group");
    	put("Ausf�hrende Gruppe - Bauphase","arch_modifiedby_structevolution | structevol_carriedoutby_group");
    	put("Beteiligte Gruppe - Bauphase","arch_modifiedby_structevolution | structevol_inflby_group");
    	put("Kommentar - Bauphase","arch_modifiedby_structevolution | structuralevolution_comment");
    	put("Enthaltene Bauteile","architecture_contains_arch");
    	put("Bauteil von","architecture_fallswithin_arch");
    	put("Eigent�mer/Verwalter Person","arch_currentowner_person");
    	put("Eigent�mer/Verwalter Gruppe","arch_currentowner_group");
    	put("Eigent�mer/Verwalter Institution","arch_currentowner_institution");
    	put("Enthaltene Objekte","arch_currentlyholds_object");
    	put("Kommentar","architecture_comment");
    	put("Schlagwort","architecture_keyword");
    	put("Literaturangaben - Untergruppe","arch_refentry");
    	put("Kurztitel","arch_refentry | arch_refentry_in_reference");
    	put("Seiten","arch_refentry | arch_refentry_pages");
    	put("Hilfreiche Links","architecture_helpfullinks");
    	put("Darstellung","architecture_iconography");
    }};
    
    public final static HashMap<String, String> REFERENCE_LABEL_ID_MAP  = new HashMap<String, String>() {{
    	put("Titelangaben - Untergruppe", "reference_title");
    	put("�bergeordneter Titel", "reference_title | reference_title_superordinate");
    	put("Titel", "reference_title | reference_title_title");
    	put("Typ", "reference_has_type");
    	put("Autor, Jahr, Ort - Kurztitel - Untergruppe", "reference_producedby_production");
    	put("Autor", "reference_producedby_production | production_authorname");
    	put("Erscheinungsort", "reference_producedby_production | ref_production_placeofpub");
    	put("Erscheinungsjahr", "reference_producedby_production | production_year");
    	put("Herausgeber", "reference_publisher");
    	put("Bandzahl", "reference_volume");
    	put("Reihe", "reference_series");
    	put("Institution", "reference_catalog_owner");
    	put("Ort der Ausstellung", "reference_catalog_location");
    	put("Jahr der Ausstellung - Untergruppe", "reference_catalog_dating");
    	put("Beginn der Ausstellung", "reference_catalog_dating | catalog_exhibition_start");
    	put("Ende der Ausstellung", "reference_catalog_dating | catalog_exhibition_end");
    	put("Seite/n", "reference_pages");
    	put("Verweis auf - Untergruppe", "reference_entry");
    	put("Verweis auf Objekt", "reference_entry | reference_entry_about_object");
    	put("Verweis auf Bauwerk", "reference_entry | reference_entry_about_arch");
    	put("Verweis auf Gruppe", "reference_entry | reference_entry_about_group");
    	put("Verweis auf Person", "reference_entry | reference_entry_about_person");
    	put("Verweis auf Ereignis", "reference_entry | reference_entry_about_activity");
    	put("Verweis auf Ort", "reference_entry | reference_entry_about_place");
    	put("Seite/n", "reference_entry | reference_entry_pages"); //TODO will the duplicate of "Seite/n" be a problem?
    	put("Schlagwort", "reference_keyword");
    }};


    public final static HashMap<String, String> OBJECT_LABEL_ID_MAP  = new HashMap<String, String>() {{
    	put("Titel", "object_identifiedby_title");
    	put("Objektbezeichnung", "object_has_description");
    	put("Funktion", "object_exemplifies_function");
    	put("Allgemeine Objektdatierung - Untergruppe", "object_dating");
    	put("Freie Datierung", "object_dating | object_dating_sometime");
    	put("Beginn des Datierungszeitraums", "object_dating | object_dating_start");
    	put("Ende des Datierungszeitraums", "object_dating | object_dating_end");
    	put("Jahrhundert", "object_dating | object_dating_century");
    	put("Objekt besteht aus Teilobjekten", "object_composedof_object");
    	put("Teilobjekt von", "object_partof_object");
    	put("Beschreibung", "object_description");
    	put("Herstellung - Untergruppe", "object_producedby_production");
    	put("K�nstler", "object_producedby_production | production_doneby_person");
    	put("Werkstatt", "object_producedby_production | production_doneby_group");
    	put("Datierung - Unteruntergruppe", "object_producedby_production | production_dating");
    	put("Entstehungszeit: Freie Datierung", "object_producedby_production | production_dating | object_prod_dating_sometime");
    	put("Entstehungszeit: Beginn des Datierungszeitraums", "object_producedby_production | production_dating | object_prod_dating_start");
    	put("Entstehungszeit: Ende des Datierungszeitraums", "object_producedby_production | production_dating | object_prod_dating_end");
    	put("Entstehungszeit: Jahrhundert", "object_producedby_production | production_dating | object_prod_dating_century");
    	put("Herstellungsort", "object_producedby_production | production_tookplaceat_place");
    	put("Material", "object_producedby_production | object_employs_material");
    	put("Technik", "object_producedby_production | production_used_technique");
    	put("Masse - Untergruppe", "object_has_dimension");
    	put("Abmessung", "object_has_dimension | dimension_has_measurementunit");
    	put("Wert", "object_has_dimension | dimension_hasvalue");
    	put("Inschrift - Untergruppe", "object_prefidentifier_inscriptio");
    	put("Art", "object_prefidentifier_inscriptio | inscription_has_type");
    	put("Anbringung", "object_prefidentifier_inscriptio | inscription_mounting");
    	put("Datierung", "object_prefidentifier_inscriptio | inscription_date");
    	put("Signatur", "object_prefidentifier_inscriptio | inscription_signatur");
    	put("Text", "object_prefidentifier_inscriptio | inscription_text");
    	put("Institution/Eigent�mer/Verwalter", "object_current_owner");
    	put("Inventarnummer", "object_inventory_number");
    	put("Standort - Museum", "object_current_location");
    	put("Standort - Bauwerk", "object_currentlocation_arch");
    	put("Provenienz - Untergruppe", "object_transferred_custody");
    	put("Datierung des Besitzerwechsels - Unteruntergruppe", "object_transferred_custody | object_toc_dating");
    	put("Taggenaue Datierung", "object_transferred_custody | object_toc_dating | object_toc_dating_exact");
    	put("Freie Datierung", "object_transferred_custody | object_toc_dating | object_toc_dating_sometime");
    	put("Beginn des Datierungszeitraums", "object_transferred_custody | object_toc_dating | object_toc_dating_start");
    	put("Ende des Datierungszeitraums", "object_transferred_custody | object_toc_dating | object_toc_dating_end");
    	put("Jahrhundert", "object_transferred_custody | object_toc_dating | object_toc_dating_century");
    	put("Eigent�mer - Person", "object_transferred_custody | custody_receiving_person");
    	put("Eigent�mer - Gruppe", "object_transferred_custody | custody_receiving_group");
    	put("Vorbesitzer - Person", "object_transferred_custody | custody_from_person");
    	put("Vorbesitzer - Gruppe", "object_transferred_custody | custody_from_group");
    	put("Darstellung", "object_iconography");
    	put("Objektbezug", "object_tookpartin_activity");
    	put("Person", "object_depicts_person");
    	put("Abgebildetes Bauwerk", "object_depicts_architecture");
    	put("Abgebildeter Ort", "object_depicts_place");
    	put("Abgebildete Aktivit�t", "object_depicts_activity");
    	put("Schlagwort", "object_keyword");
    	put("Kommentar", "object_comment");
    	put("Literaturangaben - Untergruppe", "object_refentry");
    	put("Kurztitel", "object_refentry | object_refentry_in_reference");
    	put("Seiten", "object_refentry | object_refentry_pages");
    	put("Hilfreiche Links", "object_helpfullinks");
    }};
    
    public final static HashMap<String, String> INSTITUTION_LABEL_ID_MAP  = new HashMap<String, String>() {{
    	put("Name", "institution_idby_appel");
    	put("Bauwerk", "institution_ownerof_arch");
    	put("Ort", "institution_fallswithin_place");
    	put("Adresse", "institution_address");
    	put("Ausstellung", "institution_owns_catalog");
    	put("Katalog", "institution_loc_catalog");
    	put("Hilfreiche Links", "institution_helpfullinks");
    }};
}