package rest.service;

import org.apache.marmotta.ldpath.parser.ParseException;
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
     * @param id Gives the Media id for Access on the Repository.
     * @return returns a String of the Metadata found.
     * @throws OpenRDFException
     */
    public String getSingleTechnicalMetadataByMediaID(@NonNull String id) throws OpenRDFException, ParseException {
        return digitalRepresentationRepository.getSingleTechnicalMetadataByMediaID(id);
    }

    /**
     *
     * @param id Gives the Object id for Access on the repository
     * @return returns a List of Strings of the Metadata found
     * @throws OpenRDFException
     */
    public List<String> getAllTechnicalMetadataStringsByObjectID(@NonNull String id) throws OpenRDFException {
        return digitalRepresentationRepository.getAllTechnicalMetadataStringsByObjectID(id);
    }

}
