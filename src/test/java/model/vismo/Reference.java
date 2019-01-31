package model.vismo;

import com.github.anno4j.model.impl.ResourceObject;
import model.namespace.CIDOC;
import model.namespace.VISMO;
import org.openrdf.annotations.Iri;

import java.util.Set;

@Iri(VISMO.REFERENCE)
public interface Reference extends ResourceObject {

    @Iri(CIDOC.P3_HAS_NOTE)
    Set<String> getKeywords();

    @Iri(CIDOC.P3_HAS_NOTE)
    void setKeywords(Set<String> keywords);

    void addKeyword(String keyword);

    @Iri(VISMO.CONTAINS_ENTRY)
    Set<ReferenceEntry> getEntries();

    @Iri(VISMO.CONTAINS_ENTRY)
    void setEntries(Set<ReferenceEntry> entries);

    void addEntry(ReferenceEntry entry);

    @Iri(CIDOC.P1_IS_IDENTIFIED_BY)
    Title getTitle();

    @Iri(CIDOC.P1_IS_IDENTIFIED_BY)
    void setTitle(Title title);
}
