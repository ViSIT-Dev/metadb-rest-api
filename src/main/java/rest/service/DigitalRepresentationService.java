package rest.service;

import org.openrdf.OpenRDFException;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import rest.persistence.repository.DigitalRepresentationRepository;

import java.util.List;

/**
 * Service which does the access on the DigitalRepresentationRepository.
 */
@Service
public class DigitalRepresentationService {

    private DigitalRepresentationRepository digitalRepresentationRepository;

    /**
     * @param id Gives the id for the Access on the Digital Representation Object Repository.
     * @return Returns a List of Strings of the Metadata found.
     * @throws OpenRDFException
     */
    public List<String> getTechnicalMetadataStrings(@NonNull String id) throws OpenRDFException {
        return digitalRepresentationRepository.getTechnicalMetadataStrings(id);
    }
}
