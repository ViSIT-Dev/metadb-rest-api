package model.vismo;


import model.Resource;
import model.VISMO;
import model.cidoc.E74Group;
import org.openrdf.annotations.Iri;

@Iri(VISMO.GROUP)
public interface Group extends E74Group, Resource {

    // TODO (Manu) Change these to multiple entries for better testability

    @Iri(VISMO.KEYWORD)
    String getKeyword();

    @Iri(VISMO.KEYWORD)
    void setKeyword(String keyword);

    @Iri(VISMO.ICONOGRAPHY)
    String getIconography();

    @Iri(VISMO.ICONOGRAPHY)
    void setIconography(String iconography);
}
