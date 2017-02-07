package es.upm.oeg.tools.rdfshapes.ssn;

import com.hp.hpl.jena.query.ParameterizedSparqlString;
import es.upm.oeg.tools.rdfshapes.utils.IOUtils;
import es.upm.oeg.tools.rdfshapes.utils.SparqlUtils;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

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
public class DatasetCheck {

    public static void main(String[] args) throws Exception {

        String lodCache = "http://ontology.irstea.fr/weather/query";

        List<String> classList = Files.
                readAllLines(Paths.get("src/main/resources/ssn/classList.txt"),
                        Charset.defaultCharset());
        String classQuery  = IOUtils.readFile("ssn/class.rq", Charset.defaultCharset());


        List<String> propList = Files.
                readAllLines(Paths.get("src/main/resources/ssn/propertyList.txt"),
                        Charset.defaultCharset());
        String propertyQuery  = IOUtils.readFile("ssn/prop.rq", Charset.defaultCharset());

        List<String> v201604List = Files.
                readAllLines(Paths.get("src/main/resources/owled/p.txt"),
                        Charset.defaultCharset());

        System.out.println("------------------ CLASSES -------------------------------");

        for (String clazz : classList) {

            ParameterizedSparqlString pss = new ParameterizedSparqlString();
            pss.setCommandText(classQuery);
            pss.setIri("class", clazz);

            String queryString = pss.toString();
            //System.out.println(clazz);

            long count = SparqlUtils.executeQueryForLong(queryString, lodCache, "c");

            if (count > 0) {
                System.out.println(clazz + "," + count);
            }
        }

        System.out.println("------------------ PROPERTIES -------------------------------");

        for (String property : propList) {

            ParameterizedSparqlString pss = new ParameterizedSparqlString();
            pss.setCommandText(propertyQuery);
            pss.setIri("p", property);

            String queryString = pss.toString();
            //System.out.println(property);

            long count = SparqlUtils.executeQueryForLong(queryString, lodCache, "c");

            if (count > 0) {
                System.out.println(property + "," + count);
            }
        }

        System.out.println("Done!");


//        List<String> classList = Files.
//                readAllLines(Paths.get("src/main/resources/ssn/classList.txt"),
//                        Charset.defaultCharset());
//        String classQuery  = IOUtils.readFile("ssn/class.rq", Charset.defaultCharset());
//
//        for (String clazz : classList) {
//
//            ParameterizedSparqlString pss = new ParameterizedSparqlString();
//            pss.setCommandText(classQuery);
//            pss.setIri("class", clazz);
//
//            String queryString = pss.toString();
//            //System.out.println(queryString);
//
//            long count = SparqlUtils.executeQueryForLong(queryString, lodCache, "c");
//
//            System.out.println(count);
//
//        }

    }

}
