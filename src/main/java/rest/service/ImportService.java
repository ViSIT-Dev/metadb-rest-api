package rest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import rest.persistence.repository.ImportRepository;
import rest.persistence.util.ImportQueryGenerator;

/**
 * Service class for the import functionality.
 */
@Service
public class ImportService {

    @Autowired
    private ImportRepository importRepository;

    @Autowired
    private ImportQueryGenerator importQueryGenerator;

    public void importJSON(String json) {

    }
}
