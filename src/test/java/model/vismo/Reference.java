package model.vismo;

import model.namespace.VISMO;
import org.openrdf.annotations.Iri;

@Iri(VISMO.REFERENCE)
public interface Reference {

    @Iri(VISMO.KEYWORD)
    String getKeyword();

    @Iri(VISMO.KEYWORD)
    void setKeyword(String keyword);
}
