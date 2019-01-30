package model.vismo;

import com.github.anno4j.model.impl.ResourceObject;
import model.namespace.VISMO;
import org.openrdf.annotations.Iri;

import java.util.Set;

@Iri(VISMO.REFERENCE)
public interface Reference extends ResourceObject {

    @Iri(VISMO.KEYWORD)
    String getKeyword();

    @Iri(VISMO.KEYWORD)
    void setKeyword(String keyword);

    @Iri(VISMO.CONTAINS_ENTRY)
    Set<ReferenceEntry> getEntries();

    @Iri(VISMO.CONTAINS_ENTRY)
    void setEntries(Set<ReferenceEntry> entries);

    void addEntry(ReferenceEntry entry);
}
