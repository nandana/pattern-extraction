package es.upm.oeg.tools.rdfshapes.extractors;

import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.RDFNode;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Copyright 2014-2016 Ontology Engineering Group, Universidad Politécnica de Madrid, Spain
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @author Nandana Mihindukulasooriya
 * @since 1.0.0
 */
public class QueryBase {

    public static final String LITERAL_BINDINGS  = "literal";
    public static final String IRI_BINDINGS  = "iri";

    public static final String OFFSET_PARAM = "offset";

    public static final String LIMIT_PARAM = "limit";


    public static String readFile(String path, Charset encoding)
            throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }


    public static String bindQueryString(String queryString, Map<String, Map<String, String>> bindings){

        ParameterizedSparqlString pss = new ParameterizedSparqlString();
        pss.setCommandText(queryString);

        if (bindings.containsKey(LITERAL_BINDINGS)) {
            Map<String, String> litBindings = bindings.get(LITERAL_BINDINGS);
            for(Map.Entry<String, String> binding : litBindings.entrySet()) {
                String key = binding.getKey();
                if(OFFSET_PARAM.equals(key) || LIMIT_PARAM.equals(key) ){
                    pss.setLiteral(key, Integer.parseInt(binding.getValue()));
                } else {
                    pss.setLiteral(key, binding.getValue());
                }
            }
        }

        if (bindings.containsKey(IRI_BINDINGS)) {
            Map<String, String> iriBindings = bindings.get(IRI_BINDINGS);
            for(Map.Entry<String, String> binding : iriBindings.entrySet()) {
                pss.setIri(binding.getKey(), binding.getValue());
            }
        }

        return pss.toString();

    }


    /***
     * A utility method to execute SPARQL query when only single valued
     * @param queryString The SPARQL query string
     * @param serviceEndpoint The endpoint to run the query against
     * @param vars the variables from the result
     * @return Map of the the variable and the value from the solution
     */
    public static Map<String, RDFNode> executeQueryForMap(String queryString, String serviceEndpoint, Set<String> vars) {

        Query query = QueryFactory.create(queryString);
        try (QueryExecution qexec = QueryExecutionFactory.sparqlService(serviceEndpoint, query)) {
            {
                ResultSet results = qexec.execSelect();

                // We only return the first solutions, may be a map of map
                for (; results.hasNext(); ) {
                    QuerySolution soln = results.nextSolution();
                    Map<String, RDFNode> resultsMap = new HashMap<String, RDFNode>();

                    for (String var : vars) {
                        if (soln.contains(var)) {
                            resultsMap.put(var, soln.get(var));
                        }
                    }

                    return resultsMap;
                }
            }

            // No solutions found
            return Collections.EMPTY_MAP;
        }
    }


    public static List<RDFNode> executeQueryForList(String queryString, String serviceEndpoint, String var) {


        List<RDFNode> resultsList = new ArrayList<>();

        Query query = QueryFactory.create(queryString);
        try (QueryExecution qexec = QueryExecutionFactory.sparqlService(serviceEndpoint, query)) {
            {
                ResultSet results = qexec.execSelect();

                // collect all the values
                for (; results.hasNext(); ) {
                    QuerySolution soln = results.nextSolution();
                    if (soln.contains(var)) {
                        resultsList.add(soln.get(var));
                    }
                }

                return resultsList;
            }
        }
    }

    public static List<Map<String, RDFNode>> executeQueryForList(String queryString, String serviceEndpoint, Set<String> vars) {

        List<Map<String, RDFNode>> resultsList = new ArrayList<>();

        Query query = QueryFactory.create(queryString);
        try (QueryExecution qexec = QueryExecutionFactory.sparqlService(serviceEndpoint, query)) {
            {
                ResultSet results = qexec.execSelect();

                // collect all the values
                for (; results.hasNext(); ) {
                    QuerySolution soln = results.nextSolution();
                    Map<String, RDFNode> resultsMap = new HashMap<String, RDFNode>();

                    for (String var : vars) {
                        if (soln.contains(var)) {
                            resultsMap.put(var, soln.get(var));
                        }
                    }

                    resultsList.add(resultsMap);
                }

                return resultsList;
            }
        }
    }

}
