package rest.persistence.repository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.IOException;

@Repository
public class TemplateRepository {

    @Value("${visit.rest.sparql.endpoint.query}")
    private String sparqlEndpointQuery;

    @Value("${visit.rest.sparql.endpoint.update}")
    private String sparqlEndpointUpdate;

    @Value("${visit.rest.templates.basepath}")
    private String serverBasePath;

    private final static String PYTHON_SCRIPT = "CreateSPARQLTemplatesFromPathsXML.py";

    public void triggerTemplateGeneration() {
        String templatePath = "templates";
        String pythonPath = "python/" + PYTHON_SCRIPT;

        if(!this.sparqlEndpointQuery.equals("none") && !this.sparqlEndpointUpdate.equals("none")) {
            templatePath = this.serverBasePath + "/" + templatePath;
            pythonPath = this.serverBasePath + "/" + pythonPath;
        }

        // Insert various checks: folders existing, paths.xml existing

        ProcessBuilder pb = new ProcessBuilder("python", pythonPath);
        try {
            Process process = pb.start();

            process.destroy();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
