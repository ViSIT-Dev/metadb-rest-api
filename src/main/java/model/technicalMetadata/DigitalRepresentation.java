package model.technicalMetadata;

import com.github.anno4j.model.impl.ResourceObject;
import model.VISMO;
import org.openrdf.annotations.Iri;

@Iri(VISMO.DIGITAL_REPRESENTATION)
public interface DigitalRepresentation extends ResourceObject {

    @Iri(VISMO.TECHNICAL_METADATA)
    public String getTechnicalMetadata();

    @Iri(VISMO.TECHNICAL_METADATA)
    public void setTechnicalMetadata(String metadata);
}
