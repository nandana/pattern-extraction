package es.upm.oeg.tools.rdfshapes.ssn;

import com.google.common.collect.ImmutableSet;
import com.hp.hpl.jena.query.ParameterizedSparqlString;
import com.hp.hpl.jena.rdf.model.RDFNode;
import es.upm.oeg.tools.rdfshapes.utils.IOUtils;
import es.upm.oeg.tools.rdfshapes.utils.SparqlUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
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
public class LODStats {

    private static final String lodStats = "http://stats.lod2.eu/sparql";

    private static final String propertyQuery;

    private static final String classQuery ;


    static {
        try{
            propertyQuery=IOUtils.readFile("ssn/lodstat-prop.rq",Charset.defaultCharset());
            classQuery = IOUtils.readFile("ssn/lodstat-class.rq",Charset.defaultCharset());
        } catch (IOException e){
            throw new IllegalStateException("Error loading the queries ...");
        }
    }

    public static void main(String[] args) throws Exception {


        List<String> propList = Files.
                readAllLines(Paths.get("src/main/resources/ssn/propertyList.txt"),
                        Charset.defaultCharset());

        List<String> classList = Files.
                readAllLines(Paths.get("src/main/resources/ssn/classList.txt"),
                        Charset.defaultCharset());

        printInstanceCount(classList);


    }

    public static void printInstanceCount(List<String> classList) {

        for (String clazz : classList) {

            ParameterizedSparqlString pss = new ParameterizedSparqlString();
            pss.setCommandText(classQuery);
            pss.setIri("class", clazz);

            String queryString = pss.toString();
            System.out.println(queryString);

            List<Map<String, RDFNode>> maps = SparqlUtils.executeQueryForList(queryString, lodStats, ImmutableSet.of("c"));

            for (Map<String, RDFNode> map : maps) {

                String url = "none";
                if (map.get("url") != null) {
                    url = map.get("url").asResource().getURI();
                }

                System.out.println(clazz + "," + map.get("c").asLiteral() + "," + url);
            }

        }

    }


    public static void check4Triples(List<String> propertyList, String endpoint){

        for (String property : propertyList) {

            ParameterizedSparqlString pss = new ParameterizedSparqlString();
            pss.setCommandText(propertyQuery);
            pss.setIri("p", property);

            String queryString = pss.toString();

            List<Map<String, RDFNode>> maps = SparqlUtils.executeQueryForList(queryString, lodStats, ImmutableSet.of("c", "url"));

            for (Map<String, RDFNode> map : maps) {

                String url = "none";
                if (map.get("url") != null) {
                    url = map.get("url").asResource().getURI();
                }
                System.out.println(property + "," + map.get("c").asLiteral() + "," + url);
            }

        }

    }


}
