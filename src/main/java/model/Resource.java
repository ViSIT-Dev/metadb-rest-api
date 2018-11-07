package model;

import com.github.anno4j.model.impl.ResourceObject;
import model.VISMO;
import model.technicalMetadata.DigitalRepresentation;
import org.openrdf.annotations.Iri;

import java.util.Set;

@Iri(VISMO.RESOURCE)
public interface Resource extends ResourceObject {

    @Iri(VISMO.HAS_DIGITAL_REPRESENTATION)
    public Set<DigitalRepresentation> getDigitalRepresentations();

    @Iri(VISMO.HAS_DIGITAL_REPRESENTATION)
    public void setDigitalRepresentations(Set<DigitalRepresentation> representations);

    public void addDigitalRepresentation(DigitalRepresentation representation);
}
