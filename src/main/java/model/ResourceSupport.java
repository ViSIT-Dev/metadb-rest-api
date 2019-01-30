package model;

import com.github.anno4j.annotations.Partial;
import com.github.anno4j.model.impl.ResourceObjectSupport;
import model.technicalMetadata.DigitalRepresentation;

import java.util.HashSet;

@Partial
public abstract class ResourceSupport extends ResourceObjectSupport implements Resource {

    public void addDigitalRepresentation(DigitalRepresentation representation) {
        HashSet<DigitalRepresentation> representations = new HashSet<>();

        if(this.getDigitalRepresentations() != null) {
            representations.addAll(this.getDigitalRepresentations());
        }

        representations.add(representation);
        this.setDigitalRepresentations(representations);
    }
}
