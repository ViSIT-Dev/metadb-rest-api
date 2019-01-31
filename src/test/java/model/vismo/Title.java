package model.vismo;

import com.github.anno4j.model.impl.ResourceObject;
import model.namespace.CIDOC;
import model.namespace.VISMO;
import org.openrdf.annotations.Iri;

// Should be added to a proper class hierarchy
@Iri(VISMO.TITLE)
public interface Title extends ResourceObject {

    @Iri(CIDOC.P3_HAS_NOTE)
    String getTitle();

    @Iri(CIDOC.P3_HAS_NOTE)
    void setTitle(String title);

    @Iri(VISMO.SUPERORDINATE_TITLE)
    String getSuperordinateTitle();

    @Iri(VISMO.SUPERORDINATE_TITLE)
    void setSuperordinateTitle(String title);
}
