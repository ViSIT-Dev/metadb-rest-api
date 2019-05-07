package model.vismo;

import model.Resource;
import model.namespace.CIDOC;
import model.namespace.VISMO;
import org.openrdf.annotations.Iri;

@Iri(VISMO.ACTIVITY)
public interface Activity extends Resource {

    @Iri(CIDOC.P7_TOOK_PLACE_AT)
    void setP7TookPlaceAt(Place place);

    @Iri(CIDOC.P7_TOOK_PLACE_AT)
    Place getP7TookPlaceAt();
}
