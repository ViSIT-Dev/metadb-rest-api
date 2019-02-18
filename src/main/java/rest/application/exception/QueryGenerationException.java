package rest.application.exception;

/**
 * Exception used by the ImportQueryGenerator indicating that something in the generation process has gone wrong.
 */
public class QueryGenerationException extends Exception {

    public QueryGenerationException(String message) {super(message);}
}
