package es.upm.oeg.tools.rdfshapes;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.hp.hpl.jena.rdf.model.RDFNode;
import es.upm.oeg.tools.rdfshapes.extractors.QueryBase;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CardinalityChecker extends QueryBase {

    private static String classPropertyQueryPath = "src/main/resources/3cixty/class-properties.sparql";
    private static String propertyCardinalityQueryPath = "src/main/resources/3cixty/cardinality.sparql";
    private static String individualCountQueryPath = "src/main/resources/dbpedia-instance-count.sparql";

    public static void main(String[] args) throws Exception {

        String endpoint = "http://3cixty.eurecom.fr/sparql";

        List<String> classList = Files.
                readAllLines(Paths.get("src/main/resources/3cixty/classlist.txt"),
                        Charset.defaultCharset());

        String classPropertyQueryString = readFile(classPropertyQueryPath, Charset.defaultCharset());
        String propertyCardinalityQueryString = readFile(propertyCardinalityQueryPath, Charset.defaultCharset());
        String individualCountQueryString = readFile(individualCountQueryPath, Charset.defaultCharset());

        DecimalFormat df = new DecimalFormat("0.00");

        for (String clazz : classList) {

            Map<String, String> litMap = new HashMap<>();
            Map<String, String> iriMap = ImmutableMap.of("class", clazz);

            String queryString = bindQueryString(individualCountQueryString, ImmutableMap.of(IRI_BINDINGS, iriMap, LITERAL_BINDINGS, litMap));

            int individualCount;
            List<RDFNode> c = executeQueryForList(queryString, endpoint, "c");
            if (c.size() == 1) {
                individualCount = c.get(0).asLiteral().getInt();
            } else {
                continue;
            }

            System.out.println("### " + clazz + " (" + individualCount + ")");
            System.out.println();

            queryString = bindQueryString(classPropertyQueryString, ImmutableMap.of(IRI_BINDINGS, iriMap, LITERAL_BINDINGS, litMap));

            List<RDFNode> nodeList = executeQueryForList(queryString, endpoint, "p");
            for (RDFNode property : nodeList) {
                if (property.isURIResource()) {

                    String propertyURI = property.asResource().getURI();

                    System.out.println("* " + propertyURI);
                    System.out.println();

                    Map<String, String> litMap2 = new HashMap<>();
                    Map<String, String> iriMap2 = ImmutableMap.of("class", clazz, "p", propertyURI);

                    queryString = bindQueryString(propertyCardinalityQueryString, ImmutableMap.of(IRI_BINDINGS, iriMap2, LITERAL_BINDINGS, litMap2));

                    List<Map<String, RDFNode>> solnMaps = executeQueryForList(queryString, endpoint, ImmutableSet.of("card", "count"));

                    if (solnMaps.size() > 0) {

                        System.out.println("| Cardinality  | Count   | Percentage|");
                        System.out.println("|---|---|---|");

                        for (Map<String, RDFNode> soln : solnMaps) {
                            int count = soln.get("count").asLiteral().getInt();
                            System.out.println("|" + soln.get("card").asLiteral().getInt() + "|"+ count + "|" + df.format((((double)count) / individualCount) * 100) + "%|");
                        }
                        System.out.println();
                    }

                }
            }

        }


    }


}