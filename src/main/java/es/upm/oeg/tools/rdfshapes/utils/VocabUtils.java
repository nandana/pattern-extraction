package es.upm.oeg.tools.rdfshapes.utils;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.http.QueryExecutionFactoryHttp;
import org.aksw.jena_sparql_api.model.QueryExecutionFactoryModel;
import org.aksw.rdfunit.services.PrefixNSService;
import org.apache.jena.atlas.web.HttpException;
import org.apache.jena.riot.RiotException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

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
public class VocabUtils {

    private static final Logger logger = LoggerFactory.getLogger(VocabUtils.class);

    private Map<String, Model> ontoCache = new HashMap<>();


    public static void analyzeProperty(String property) {

        //We are trying to load the ontology model
        //OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_RULE_INF, ModelFactory.createDefaultModel());
        Model model = ModelFactory.createDefaultModel();
        //Flag to check if the ontology is loaded
        boolean ontoLoaded = false;
        boolean propertyFound = false;
        boolean dereferenced = false;
        QueryExecutionFactory qef = null;

        try {
            model.read(property);
            if (!model.isEmpty()) {
                logger.debug("Loded the ontology from {}", property);
                ontoLoaded = true;
                qef = new QueryExecutionFactoryModel(model);
                propertyFound = isPropretyFound(property, qef);
                if (propertyFound) {
                    dereferenced = true;
                }
            }

        } catch (HttpException e) {
            logger.debug("Dereferecing ontology using URL '{}' failed. {}", property, e.getMessage());
        } catch (RiotException e) {
            logger.debug("Error parsing the derefereced ontology. {}", e.getMessage());
        }

        if (!ontoLoaded || !propertyFound) {
            //If the ontology can't be loaded from the URL, we check in LOV
            qef = new QueryExecutionFactoryHttp(SPARQLServices.LOV);
            propertyFound = isPropretyFound(property, qef);
            if (propertyFound) {
                logger.debug("Property '{}' was found in LOV", property);
            }
        }

        extractRange(property, qef);

    }

    public static boolean isPropretyFound(String res, QueryExecutionFactory qef) {

        String queryString = PrefixNSService.getSparqlPrefixDecl() +
                " ask { " +
                "<" + res + ">  ?p ?o ." +
                "}";
        try (QueryExecution qe = qef.createQueryExecution(queryString)) {
            return qe.execAsk();
        }

    }

    public static void extractRange(String property, QueryExecutionFactory qef) {

        logger.debug("Extracting the range of the property '{}'", property);

//        String queryString = PrefixNSService.getSparqlPrefixDecl() +
//                " select  ?g ?range { graph ?g { " +
//                "<" + property + ">  rdfs:range ?range ." +
//                " } }";


        String queryString = PrefixNSService.getSparqlPrefixDecl() +
                " select ?range ?graph where { graph ?graph { " +
                "<" + property + ">  rdfs:range ?range ." +
                "  }}";

        System.out.println(queryString);

        try (QueryExecution qe = qef.createQueryExecution(queryString)) {

            ResultSet resultSet = qe.execSelect();
            while (resultSet.hasNext()) {
                QuerySolution qs = resultSet.next();
                //Resource g = qs.get("g").asResource();
                Resource range = qs.get("range").asResource();
                //System.out.println("Range:" + range.getURI() + "(" + g.getURI() + ")");
                System.out.println("Range:" + range.getURI());
            }

        }

    }

}
