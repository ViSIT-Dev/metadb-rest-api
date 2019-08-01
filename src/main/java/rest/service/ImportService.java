package rest.service;

import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.UpdateExecutionException;
import org.openrdf.repository.RepositoryException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import rest.application.exception.ExcelParserException;
import rest.application.exception.IdMapperException;
import rest.application.exception.ImportException;
import rest.application.exception.QueryGenerationException;
import rest.persistence.repository.ImportRepository;
import rest.persistence.util.ExcelParser;
import rest.persistence.util.ImportQueryGenerator;

import java.io.IOException;

/**
 * Service class for the import functionality.
 */
@Service
public class ImportService {

    @Autowired
    private ImportRepository importRepository;

    @Autowired
    private ImportQueryGenerator importQueryGenerator;

    @Autowired
    private ExcelParser excelParser;

    public void importJSON(String json) throws ImportException {
        try {
            String updateQuery = this.importQueryGenerator.createUpdateQueryFromJSON(json);

            this.importRepository.persistJSONDataByUpdateQuery(updateQuery);
        } catch (QueryGenerationException | IdMapperException | UpdateExecutionException | MalformedQueryException | RepositoryException e) {
            throw new ImportException(e.getMessage());
        }
    }

    public void importExcelUpload(MultipartFile file, String context) throws ExcelParserException {
        try {
            String jsonFromParsedExcelFile = this.excelParser.createJSONFromParsedExcelFile(file);

            String updateQuery = "";

            if(context.isEmpty()) {
                updateQuery = this.importQueryGenerator.createUpdateQueryFromJSON(jsonFromParsedExcelFile);
            } else {
                updateQuery = this.importQueryGenerator.createUpdateQueryFromJSONIntoContext(jsonFromParsedExcelFile, context);
            }

            this.importRepository.persistJSONDataByUpdateQuery(updateQuery);
        } catch (Exception e) {
            throw new ExcelParserException(e.getMessage());
        }
    }
}
