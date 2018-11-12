package rest.service;

import org.apache.marmotta.ldpath.parser.ParseException;
import org.openrdf.OpenRDFException;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import rest.Exception.DigitalRepositoryException;
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
     */
    public String getSingleTechnicalMetadataByMediaID(@NonNull String id) {
        try {
            return digitalRepresentationRepository.getSingleTechnicalMetadataByMediaID(id);
        }catch (Exception e){
            throw new DigitalRepositoryException(e.getMessage());
        }
    }

    /**
     *
     * @param id Gives the Object id for Access on the repository.
     * @return returns a List of Strings of the Metadata found.
     */
    public List<String> getAllTechnicalMetadataStringsByObjectID(@NonNull String id) {
        try{
            return digitalRepresentationRepository.getAllTechnicalMetadataStringsByObjectID(id);
        }catch (Exception e){
            throw new DigitalRepositoryException(e.getMessage());
        }

    }

}
