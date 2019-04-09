package rest.application.exception;

/**
 * Exception mainly used by the ExcelParser Class, issuing that something went wrong during the process of parsing an
 * input Excel file for the Visit REST API.
 */
public class ExcelParserException extends Exception {

    public ExcelParserException(String message) {
        super(message);
    }
}
