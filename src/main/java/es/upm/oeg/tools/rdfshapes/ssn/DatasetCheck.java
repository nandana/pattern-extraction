package es.upm.oeg.tools.rdfshapes.ssn;

import com.hp.hpl.jena.query.ParameterizedSparqlString;
import es.upm.oeg.tools.rdfshapes.utils.IOUtils;
import es.upm.oeg.tools.rdfshapes.utils.SparqlUtils;

import java.io.IOException;
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


    public static final String classQuery;

    public static final String propertyQuery;

    public static final String lodCache = "http://lod.openlinksw.com/sparql";

    public static final String aemetSparql = "http://4v.dia.fi.upm.es:9191/sparql";

    static {
        try{
            propertyQuery=IOUtils.readFile("ssn/prop.rq",Charset.defaultCharset());
            classQuery = IOUtils.readFile("ssn/class.rq",Charset.defaultCharset());
        } catch (IOException e){
            throw new IllegalStateException("Error loading the queries ...");
        }
    }


    public static void main(String[] args) throws Exception {


        List<String> ssnClassList = Files.
                readAllLines(Paths.get("src/main/resources/ssn/classList.txt"),
                        Charset.defaultCharset());

        List<String> ssnPropertyList = Files.
                readAllLines(Paths.get("src/main/resources/ssn/propertyList.txt"),
                        Charset.defaultCharset());


        List<String> subClassList = Files.
                readAllLines(Paths.get("src/main/resources/ssn/subClass.txt"),
                        Charset.defaultCharset());

        List<String> v201604List = Files.
                readAllLines(Paths.get("src/main/resources/owled/p.txt"),
                        Charset.defaultCharset());


        //checkInstances(ssnClassList, aemetSparql);
        check4triples(ssnPropertyList, aemetSparql);


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


    public static void checkInstances(List<String> classList, String endpoint) {

        for (String clazz : classList) {

            ParameterizedSparqlString pss = new ParameterizedSparqlString();
            pss.setCommandText(classQuery);
            pss.setIri("class", clazz);

            String queryString = pss.toString();
            System.out.println(queryString);

            long count = SparqlUtils.executeQueryForLong(queryString, endpoint, "c");

            if (count > 0) {
                System.out.println(clazz + "," + count);
            }
        }

        System.out.println("Done!");

    }

    public static void check4triples(List<String> propertyList, String endpoint) {

        for (String property : propertyList) {

            ParameterizedSparqlString pss = new ParameterizedSparqlString();
            pss.setCommandText(propertyQuery);
            pss.setIri("p", property);

            String queryString = pss.toString();

            long count = SparqlUtils.executeQueryForLong(queryString, endpoint, "c");

            if (count > 0) {
                System.out.println(property + "," + count);
            }
        }

        System.out.println("Done!");

    }



}
