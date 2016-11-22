package es.upm.oeg.tools.rdfshapes.ontodiff;

import com.google.common.collect.ImmutableList;
import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.RDFNode;
import es.upm.oeg.tools.rdfshapes.utils.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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
public class VersionAnalysis {

    private static final Logger logger = LoggerFactory.getLogger(VersionAnalysis.class);

    private static final String IS_PRESENT_QS;

    static {
        try {
            IS_PRESENT_QS = IOUtils.readFile("ontodiff/isPresent.rq", Charset.defaultCharset());
        } catch (IOException ioe) {
            logger.error("Error loading the queries", ioe);
            throw new IllegalStateException("Error loading the query :" + ioe.getMessage(), ioe);
        }
    }


    public static void main(String[] args) throws Exception {

        String sparqlEndpoint = "http://4v.dia.fi.upm.es:8890/sparql";

        List<String> dbpGraphs = ImmutableList.of(
                "http://dbpedia.org/o/3.2",
                "http://dbpedia.org/o/3.3",
                "http://dbpedia.org/o/3.4",
                "http://dbpedia.org/o/3.5",
                "http://dbpedia.org/o/3.6",
                "http://dbpedia.org/o/3.7",
                "http://dbpedia.org/o/3.8",
                "http://dbpedia.org/o/3.9",
                "http://dbpedia.org/o/2014",
                "http://dbpedia.org/o/201504",
                "http://dbpedia.org/o/201510",
                "http://dbpedia.org/o/201604");
        String dbpPrefix = "http://dbpedia.org/ontology/";

        List<String> propList = Files.
                //readAllLines(Paths.get("src/main/resources/dbproptest/all-props.txt"),
                readAllLines(Paths.get("src/main/resources/ontodiff/allClasses.txt"),
                        Charset.defaultCharset());


        for (String property : propList) {

            System.out.print(property + ",");

            int v = 3;
            int count = 0;
            int min = -1;
            int max = -1;

            for (String graph : dbpGraphs) {

                ParameterizedSparqlString pss = new ParameterizedSparqlString();
                pss.setCommandText(IS_PRESENT_QS);
                pss.setIri("graph", graph);
                pss.setIri("prop", property);

                String queryString = pss.toString();
                Query query = QueryFactory.create(queryString);

                try (QueryExecution qexec = QueryExecutionFactory.sparqlService(sparqlEndpoint, query)) {
                    {
                        boolean present = qexec.execAsk();
                        if (present) {
                            count++;
                            if (min == -1) {
                                min = v;
                            }
                            max = v;
                        }

                        System.out.print(present + ",");
                    }
                }

                v++;

            }

            System.out.print(min + ",");
            System.out.print(max + ",");
            System.out.print(count + ",");

            if ((max - min) != (count - 1) ) {
                System.out.println(true);
            } else {
                System.out.println(false);
            }

        }

    }


}
