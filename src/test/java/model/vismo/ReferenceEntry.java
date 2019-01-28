package model.vismo;

import model.namespace.VISMO;
import org.openrdf.annotations.Iri;

@Iri(VISMO.REFERENCE_ENTRY)
public interface ReferenceEntry {

    @Iri(VISMO.ENTRY_PAGES)
    int getPages();

    @Iri(VISMO.ENTRY_PAGES)
    void setPages(int pages);

    @Iri(VISMO.IS_ENTRY_IN)
    Reference getEntryIn();

    @Iri(VISMO.IS_ENTRY_IN)
    void setEntryIn(Reference reference);
}
