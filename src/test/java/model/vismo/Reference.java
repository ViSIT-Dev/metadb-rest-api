package model.vismo;

import com.github.anno4j.model.impl.ResourceObject;
import model.namespace.VISMO;
import org.openrdf.annotations.Iri;

@Iri(VISMO.REFERENCE)
public interface Reference extends ResourceObject {

    @Iri(VISMO.KEYWORD)
    String getKeyword();

    @Iri(VISMO.KEYWORD)
    void setKeyword(String keyword);
}
