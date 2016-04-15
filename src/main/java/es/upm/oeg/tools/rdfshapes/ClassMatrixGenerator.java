package es.upm.oeg.tools.rdfshapes;

import com.hp.hpl.jena.query.ParameterizedSparqlString;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;

import static es.upm.oeg.tools.rdfshapes.utils.IOUtils.readFile;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ClassMatrixGenerator {

    protected static String subClassQuery;
    protected static String equivalentClassQuery;
    String lovEndpoint = "http://lov.okfn.org/dataset/lov/sparql";
    String dbpediaEndpoint = "http://dbpedia.org/sparql";
    String sparqlEndpoint = lovEndpoint;

    static  {

        try {
            List<String> classList = Files.
                    readAllLines(Paths.get("src/main/resources/country-range.txt"),
                            Charset.defaultCharset());

            subClassQuery = readFile("src/main/resources/subClass.rq", Charset.defaultCharset());
            equivalentClassQuery = readFile("src/main/resources/equivalentClass.rq", Charset.defaultCharset());

        } catch (IOException ioe) {
            throw new IllegalStateException("Error loading the query");
        }

    }


    public static List<Edge>  getEquivalentClassRelations(List<String> classList, String sparqlEndpoint) {

        List<Edge> edges = new ArrayList<>();

        for (int i = 0 ; i < classList.size(); i++) {

            String classA = classList.get(i);

            for (int j = i + 1; j < classList.size() ; j++) {

                String classB = classList.get(j);
                //System.out.println("<" + classA + " , " + classB + ">");

                if (isEquivalentClass(classA, classB, sparqlEndpoint)) {
                    System.out.println(classA + " equivalentClassOf " + classB);
                    edges.add(new Edge(classA, classB));
                } else if (isEquivalentClass(classB, classA, sparqlEndpoint)) {
                    System.out.println(classB + " equivalentClassOf " + classA);
                    edges.add(new Edge(classA, classB));
                }
            }
        }

        return edges;

    }

    public static List<Edge> getSubClassRelations(List<String> classList, String sparqlEndpoint) {

        List<Edge> edges = new ArrayList<>();


        for (int i = 0 ; i < classList.size(); i++) {

            String classA = classList.get(i);

            for (int j = i + 1; j < classList.size() ; j++) {

                String classB = classList.get(j);

                if (isSubClass(classA, classB, sparqlEndpoint)) {
                    System.out.println(classA + " subClassOf " + classB);
                    edges.add(new Edge(classA, classB));
                }
                if (isSubClass(classB, classA, sparqlEndpoint)) {
                    System.out.println(classB + " subClassOf " + classA);
                    edges.add(new Edge(classB, classA));
                }
            }
        }

        return edges;
    }

    public static boolean isSubClass(String classA, String classB, String endpoint) {

        ParameterizedSparqlString pss = new ParameterizedSparqlString();
        pss.setCommandText(subClassQuery);
        pss.setIri("classA", classA);
        pss.setIri("classB", classB);
        String boundQuery = pss.toString();

        try (QueryExecution qe = QueryExecutionFactory.sparqlService(endpoint, boundQuery);) {
            boolean isSubClass = qe.execAsk();
            //System.out.println(isSubClass);
            return isSubClass;
        }

    }

    public static boolean isEquivalentClass(String classA, String classB, String endpoint) {

        ParameterizedSparqlString pss = new ParameterizedSparqlString();
        pss.setCommandText(equivalentClassQuery);
        pss.setIri("classA", classA);
        pss.setIri("classB", classB);
        String boundQuery = pss.toString();

        try (QueryExecution qe = QueryExecutionFactory.sparqlService(endpoint, boundQuery);) {
            boolean isEquivalentClass = qe.execAsk();
            //System.out.println(isSubClass);
            return isEquivalentClass;
        }

    }



}
