package model.vismo;

import com.github.anno4j.model.impl.ResourceObject;
import model.Resource;
import model.namespace.VISMO;
import org.openrdf.annotations.Iri;

import java.math.BigInteger;

@Iri(VISMO.REFERENCE_ENTRY)
public interface ReferenceEntry extends ResourceObject {

    @Iri(VISMO.ENTRY_PAGES)
    BigInteger getPages();

    @Iri(VISMO.ENTRY_PAGES)
    void setPages(BigInteger pages);

    @Iri(VISMO.IS_ENTRY_IN)
    Reference getEntryIn();

    @Iri(VISMO.IS_ENTRY_IN)
    void setEntryIn(Reference reference);

    @Iri(VISMO.ENTRY_IS_ABOUT)
    Resource getIsAbout();

    @Iri(VISMO.ENTRY_IS_ABOUT)
    void setIsAbout(Resource resource);
}
