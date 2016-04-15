package es.upm.oeg.tools.rdfshapes.utils;

import com.google.common.collect.Sets;
import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.RDFNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Copyright 2014-2016 Ontology Engineering Group, Universidad Politécnica de Madrid, Spain
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @author Nandana Mihindukulasooriya
 * @since 1.0.0
 */
public class SparqlUtils {

    private static final Logger logger = LoggerFactory.getLogger(SparqlUtils.class);

    public static List<Map<String, RDFNode>> executeQueryForList(String queryString, String serviceEndpoint, Set<String> vars) {

        logger.debug("Executing query: {}", queryString);

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


    /***
     * A utility method to execute SPARQL query when only single valued
     * @param queryString The SPARQL query string
     * @param serviceEndpoint The endpoint to run the query against
     * @param vars the variables from the result
     * @return Map of the the variable and the value from the solution
     */
    public static Map<String, RDFNode> executeQueryForMap(String queryString, String serviceEndpoint, Set<String> vars) {

        logger.debug("Executing query: {}", queryString);

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

    public static long executeQueryForLong(String queryString, String serviceEndpoint, String var) {

        logger.debug("Executing query: {}", queryString);

        //Execute the query
        Map<String, RDFNode> nodeMap = executeQueryForMap(queryString, serviceEndpoint, Sets.newHashSet(var));

        RDFNode rdfNode = nodeMap.get(var);
        if (rdfNode == null) {
            throw new IllegalStateException(String.format("The result does not have a binding for the variable '%s' ", var));
        }

        if (rdfNode.isLiteral()) {
            return rdfNode.asLiteral().getLong();
        } else {
            throw new IllegalStateException(String.format("The binding for the variable '%s' is not a literal", var));
        }

    }

}
