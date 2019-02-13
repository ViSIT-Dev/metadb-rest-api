package rest.persistence.repository;

import org.python.util.PythonInterpreter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.Scanner;

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
        String completePath = "/Users/Manu/Programs/Anaconda/anaconda3/bin/python";

        if(!this.sparqlEndpointQuery.equals("none") && !this.sparqlEndpointUpdate.equals("none")) {
            templatePath = this.serverBasePath + "/" + templatePath;
            pythonPath = this.serverBasePath + "/" + pythonPath;
        }

        // Insert various checks: folders existing, paths.xml existing

//        ProcessBuilder pb = new ProcessBuilder("python", pythonPath);
        try {
//            Process process = pb.start();

            String returnValue = "";
            String command = completePath + " " + "/Users/Manu/IdeaProjects/MetaDBRestAPIPadim/python/CreateSPARQLTemplatesFromPathsXML.py";

            Process process = Runtime.getRuntime().exec(command);

            Scanner stdin = new Scanner(new BufferedInputStream(process.getInputStream()));
            while (stdin.hasNext())
                returnValue = returnValue + stdin.nextLine();
            stdin.close();
            System.out.println("Output: " + returnValue);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void triggerTemplateGeneration2() {
        String templatePath = "templates";
        String pythonPath = "python/" + PYTHON_SCRIPT;

        if(!this.sparqlEndpointQuery.equals("none") && !this.sparqlEndpointUpdate.equals("none")) {
            templatePath = this.serverBasePath + "/" + templatePath;
            pythonPath = this.serverBasePath + "/" + pythonPath;
        }

        PythonInterpreter.initialize(System.getProperties(), System.getProperties(), new String[0]);
        PythonInterpreter pythonInterpreter = new PythonInterpreter();

        pythonInterpreter.exec("from bs4 import BeautifulSoup");
        pythonInterpreter.execfile(pythonPath);
    }
}
