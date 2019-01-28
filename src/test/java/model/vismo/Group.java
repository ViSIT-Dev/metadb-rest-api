package model.vismo;


import model.Resource;
import model.namespace.VISMO;
import model.cidoc.E74Group;
import org.openrdf.annotations.Iri;

import java.util.Set;

@Iri(VISMO.GROUP)
public interface Group extends E74Group, Resource {

    @Iri(VISMO.KEYWORD)
    Set<String> getKeywords();

    @Iri(VISMO.KEYWORD)
    void setKeywords(Set<String> keywords);

    void addKeyword(String keyword);

    @Iri(VISMO.ICONOGRAPHY)
    String getIconography();

    @Iri(VISMO.ICONOGRAPHY)
    void setIconography(String iconography);

    @Iri(VISMO.REFERENCED_BY_ENTRY)
    ReferenceEntry getEntry();

    @Iri(VISMO.REFERENCED_BY_ENTRY)
    void setEntry(ReferenceEntry entry);
}
