package es.upm.oeg.tools.rdfshapes;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.hp.hpl.jena.rdf.model.RDFNode;
import es.upm.oeg.tools.rdfshapes.extractors.QueryBase;
import es.upm.oeg.tools.rdfshapes.stats.CardinalityCount;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.*;

public class CardinalityChecker extends QueryBase {

    private static String classPropertyQueryPath = "src/main/resources/3cixty/class-properties.sparql";
    private static String propertyCardinalityQueryPath = "src/main/resources/3cixty/cardinality.sparql";
    private static String individualCountQueryPath = "src/main/resources/3cixty/instance-count.sparql";
    private static String classListPath = "src/main/resources/bne/classlist.txt";

    public static void main(String[] args) throws Exception {

        String endpoint = "http://infra2.dia.fi.upm.es:8899/sparql";

        List<String> classList = Files.
                readAllLines(Paths.get(classListPath),
                        Charset.defaultCharset());

        String classPropertyQueryString = readFile(classPropertyQueryPath, Charset.defaultCharset());
        String propertyCardinalityQueryString = readFile(propertyCardinalityQueryPath, Charset.defaultCharset());
        String individualCountQueryString = readFile(individualCountQueryPath, Charset.defaultCharset());

        DecimalFormat df = new DecimalFormat("0.0000");
        DecimalFormat dfPercentage = new DecimalFormat("0.00");

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

            // If there are zero individuals, continue
            if (individualCount == 0) {
                throw new IllegalStateException("Check whether " + classListPath + " and " + endpoint + " match.");
            }

            System.out.println("***");
            System.out.println("### **" + clazz + "** (" + individualCount + ")");
            System.out.println("***");
            System.out.println();

            queryString = bindQueryString(classPropertyQueryString, ImmutableMap.of(IRI_BINDINGS, iriMap, LITERAL_BINDINGS, litMap));

            List<RDFNode> nodeList = executeQueryForList(queryString, endpoint, "p");
            for (RDFNode property : nodeList) {
                if (property.isURIResource()) {

                    DescriptiveStatistics stats = new DescriptiveStatistics();

                    String propertyURI = property.asResource().getURI();

                    System.out.println("* " + propertyURI);
                    System.out.println();

                    Map<String, String> litMap2 = new HashMap<>();
                    Map<String, String> iriMap2 = ImmutableMap.of("class", clazz, "p", propertyURI);

                    queryString = bindQueryString(propertyCardinalityQueryString, ImmutableMap.of(IRI_BINDINGS, iriMap2, LITERAL_BINDINGS, litMap2));

                    List<Map<String, RDFNode>> solnMaps = executeQueryForList(queryString, endpoint, ImmutableSet.of("card", "count"));

                    int sum = 0;
                    List<CardinalityCount> cardinalityList = new ArrayList<>();

                    if (solnMaps.size() > 0) {

                        for (Map<String, RDFNode> soln : solnMaps) {
                            int count = soln.get("count").asLiteral().getInt();
                            int card = soln.get("card").asLiteral().getInt();

                            for (int i = 0 ; i < count; i++) {
                                stats.addValue(card);
                            }
                            sum += count;

                            CardinalityCount cardinalityCount = new CardinalityCount(card, count, (((double)count) / individualCount) * 100);
                            cardinalityList.add(cardinalityCount);

                        }

                        // Check for zero cardinality instances
                        int count = individualCount - sum;
                        if (count > 0) {
                            for (int i = 0; i < count; i++) {
                                stats.addValue(0);
                            }
                            CardinalityCount cardinalityCount = new CardinalityCount(0, count, (((double)count) / individualCount) * 100);
                            cardinalityList.add(cardinalityCount);
                        }
                    }

                    Collections.sort(cardinalityList, new Comparator<CardinalityCount>() {
                        @Override
                        public int compare(CardinalityCount o1, CardinalityCount o2) {
                            if (o1.getCount() > o2.getCount()) {
                                return -1;
                            } else if (o1.getCount() < o2.getCount()) {
                                return 1;
                            } else {
                                return 0;
                            }
                        }
                    });

                    //Printing the first line
                    System.out.print("| Cardinality |");
                    cardinalityList.forEach(cardinality -> System.out.print( cardinality.getCardinality() + "|"));
                    System.out.println();    
                    
                    System.out.print("|---|");
                    cardinalityList.forEach(cardinality -> System.out.print("---|"));
                    System.out.println();

                    System.out.print("| Count |");
                    cardinalityList.forEach(cardinality -> System.out.print( cardinality.getCount() + "|"));
                    System.out.println();

                    System.out.print("| Percentage |");
                    cardinalityList.forEach(cardinality -> System.out.print( dfPercentage.format(cardinality.getPrecentage()) + "%|"));
                    System.out.println();


                    //System.out.println("|" + card + "|"+ count + "|" + dfPercentage.format((((double)count) / individualCount) * 100) + "%|");
                    //System.out.println("|" + 0 + "|" + count + "|" + dfPercentage.format((((double) count) / individualCount) * 100) + "%|");

                    System.out.println();



                    System.out.println("| Min | Max | Mean | Quad.Mean | Geo.Mean | StdDev. | Variance | P.Variance | Skewness | Kurtosis | 25P | 50P | 75P | ");
                    System.out.println("|---|---|---|---|---|---|---|---|---|---|---|---|---|");
                    System.out.println("|" + df.format(stats.getMin()) + "|" + df.format(stats.getMax()) + "|" + df.format(stats.getMean()) + "|" + df.format(stats.getQuadraticMean()) + "|"
                            + df.format(stats.getGeometricMean()) + "|" + df.format(stats.getStandardDeviation()) + "|" + df.format(stats.getVariance()) + "|" + df.format(stats.getPopulationVariance()) + "|"
                            + df.format(stats.getSkewness()) + "|" + df.format(stats.getKurtosis()) + "|" + df.format(stats.getPercentile(25)) + "|" + df.format(stats.getPercentile(50)) + "|"
                            + df.format(stats.getPercentile(75)) + "|" );
                }
            }

        }


    }


}