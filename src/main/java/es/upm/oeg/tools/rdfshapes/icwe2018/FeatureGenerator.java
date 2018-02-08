package es.upm.oeg.tools.rdfshapes.icwe2018;

import es.upm.oeg.tools.rdfshapes.extractors.PropertyInfoExtractor;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Nandana Mihindukulasooriya
 * @since 2/02/18.
 */
public class FeatureGenerator {

    private static final String SPARQL_ENDPOINT = "http://es.dbpedia.org/sparql";

    public static void main(String[] args) throws Exception {


        List<String> holidayList = Files.
                readAllLines(Paths.get("/home/nandana/srcroot/icwe2018/gs/set1/holiday-property.txt"),
                        Charset.defaultCharset());

//        holidayList.stream().forEach(s -> {
//            if (s.startsWith("http://es.dbpedia.org/property/")) {
//                System.out.println(s);
//            }
//        });

//        final Set<String> propertySet = new HashSet<>();
//
//        holidayList.stream().forEach(s ->
//                {
//                    ArrayList<String> properties = PropertyInfoExtractor.extractProperties(s, SPARQL_ENDPOINT);
//
//                    properties.stream().forEach(r -> {
//                        if (!r.startsWith("http://dbpedia.org/property/")) {
//                            propertySet.add(r);
//                        }
//                    });
//                }
//        );
//
//        propertySet.stream().forEach(s -> System.out.println(s));

    }



}
