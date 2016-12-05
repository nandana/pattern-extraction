package es.upm.oeg.tools.rdfshapes.dbproptest;


import com.google.common.collect.ImmutableSet;
import com.hp.hpl.jena.query.ParameterizedSparqlString;
import com.hp.hpl.jena.rdf.model.RDFNode;
import es.upm.oeg.tools.rdfshapes.utils.IOUtils;
import es.upm.oeg.tools.rdfshapes.utils.SparqlUtils;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class ExampleInstances {

    public static void main(String[] args) throws Exception {

        String endpoint = "http://4v.dia.fi.upm.es:10042/sparql";

        String exampleQuery  = IOUtils.readFile("dbproptest/example.rq", Charset.defaultCharset());

        List<String> propList = Files.
                readAllLines(Paths.get("src/main/resources/dbproptest/strangep.txt"),
                        Charset.defaultCharset());

        for (String prop : propList) {

            ParameterizedSparqlString pss = new ParameterizedSparqlString();
            pss.setCommandText(exampleQuery);
            pss.setIri("p", prop);

            String queryString = pss.toString();

            final List<Map<String, RDFNode>> resultsList = SparqlUtils.executeQueryForList(queryString, endpoint, ImmutableSet.of("s", "o"));

            System.out.println("Property:" + prop);

            for (Map<String, RDFNode> results : resultsList) {
                System.out.println( results.get("s").asResource().getURI() + " , " + results.get("o").toString());
            }

            System.out.println("----------------------------------------------------------------");

        }

    }
}

