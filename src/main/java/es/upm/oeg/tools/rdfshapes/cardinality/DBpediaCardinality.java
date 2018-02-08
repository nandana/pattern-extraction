package es.upm.oeg.tools.rdfshapes.cardinality;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;
import com.hp.hpl.jena.query.ParameterizedSparqlString;
import com.hp.hpl.jena.rdf.model.RDFNode;
import es.upm.oeg.tools.rdfshapes.utils.SparqlUtils;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * @author Nandana Mihindukulasooriya
 * @since 25/04/17.
 */
public class DBpediaCardinality {


    private static String dbClassList = "src/main/resources/cardinality/dbp-classlist.txt";
    private static String topPropertiesQueryPath = "src/main/resources/cardinality/top-properties.rq";
    private static String instanceCountQueryPath = "src/main/resources/cardinality/instance-count.rq";
    private static String propertyCardinalityQueryPath = "src/main/resources/cardinality/property-cardinality.rq";
    private static String topPropertiesQuery;
    private static String instanceCountQuery;
    private static String propertyCardinalityQuery;

    private static DecimalFormat df = new DecimalFormat("0.00000");


    static {

        try {
            topPropertiesQuery = readFile(topPropertiesQueryPath, Charset.defaultCharset());
            instanceCountQuery = readFile(instanceCountQueryPath, Charset.defaultCharset());
            propertyCardinalityQuery = readFile(propertyCardinalityQueryPath, Charset.defaultCharset());
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }

    }

    private static final String ENDPOINT = "http://4v.dia.fi.upm.es:10043/sparql";
    //private static final String ENDPOINT = "http://es.dbpedia.org/sparql";

    public static void main(String[] args) throws Exception {

        List<String> classList = Files.readAllLines(Paths.get(dbClassList),
                        Charset.defaultCharset());

        for (String clazz : classList) {

            long instanceCount = getInstacneCount(clazz);
            List<String> topProperties = getTopProperties(clazz, 40);


            for (String property : topProperties) {

                List<Cardinality> cardinalityCounts = getCardinalityCounts(clazz, property, instanceCount);

                int mode = 0;
                long modeCount = 0;
                double modePercentage = 0;

                double zeroPercentage = 0;
                double onePercentage = 0;

                DescriptiveStatistics stats = new DescriptiveStatistics();
                DescriptiveStatistics distinctCards = new DescriptiveStatistics();
                DescriptiveStatistics cardPrecentages = new DescriptiveStatistics();
                Map<Integer, Double> cardMap = new HashMap<>();


                for (Cardinality cardinality : cardinalityCounts) {
                    distinctCards.addValue(cardinality.card);
                    cardPrecentages.addValue(cardinality.precentage);
                    cardMap.put(cardinality.card, cardinality.precentage);

                    for (int i = 0; i < cardinality.count; i++) {
                        stats.addValue(cardinality.card);
                    }

                    if (cardinality.count > modeCount) {
                        mode = cardinality.card;
                        modeCount = cardinality.count;
                        modePercentage = cardinality.precentage;
                    }

                    if (cardinality.card == 0) {
                        zeroPercentage = cardinality.getPrecentage();
                    } else if (cardinality.card == 1) {
                        onePercentage = cardinality.getPrecentage();
                    }
                }

//                cardinalityCounts.stream().forEach(c -> System.out.println(c.getCard() + "," + c.getCount() + "," + c.getPrecentage()));
//
//                System.out.println("Min: " + stats.getMin());
//                System.out.println("Max: " + stats.getMax());
//                System.out.println("Mean: " + stats.getMean());
//                System.out.println("Mode: " + mode);
//                System.out.println("Quadratic Mean: " + stats.getQuadraticMean());
//                System.out.println("Kurtosis: " + stats.getKurtosis());
//                System.out.println("Standard Deviation: " + stats.getStandardDeviation());
//                System.out.println("Skewness: " + stats.getSkewness());
//                System.out.println("Variance: " + stats.getVariance());
//                System.out.println("P98: " + stats.getPercentile(98));
//                System.out.println("P2: " + stats.getPercentile(2));
//                System.out.println("P75: " + stats.getPercentile(75));
//                System.out.println("P25: " + stats.getPercentile(25));
//
//                System.out.println("Distinct Cards: " + distinctCards.getN());
//                System.out.println("Mean Distinct Card: " + distinctCards.getMean());
//                System.out.println("Quadratic Mean Distinct Card: " + distinctCards.getQuadraticMean());
//                System.out.println("Kurtosis Distinct : " + distinctCards.getQuadraticMean());
//                System.out.println("Standard Deviation Dis. : " + distinctCards.getQuadraticMean());
//                System.out.println("Skewness Dis.: " + distinctCards.getSkewness());
//                System.out.println("Variance Dis.: " + distinctCards.getVariance());
//
//                System.out.println("Min Percentage: " + cardPrecentages.getMin());
//                System.out.println("Max Percentage: " + cardPrecentages.getMax());
//                System.out.println("Mode Percentage: " + modePercentage);
//                System.out.println("Zero Percentage: " + zeroPercentage);
//                System.out.println("One Percentage: " + onePercentage);
//                System.out.println("Mean Percentage: " + cardPrecentages.getMean());
//                System.out.println("Standard Deviation Percentage : " + cardPrecentages.getQuadraticMean());
//                System.out.println("Skewness Percentage: " + cardPrecentages.getSkewness());
//                System.out.println("Variance Percentage: " + cardPrecentages.getVariance());


                List<String> featureList = new ArrayList<>();
                featureList.add(clazz);
                featureList.add(property);
                featureList.add(String.valueOf(stats.getMin()));
                featureList.add(String.valueOf(stats.getMax()));
                featureList.add(df.format(stats.getMean()));
                featureList.add(String.valueOf(mode));
                featureList.add(df.format(stats.getQuadraticMean()));
                featureList.add( Double.isNaN(stats.getKurtosis()) ? "0.00000" : df.format(stats.getKurtosis()));
                featureList.add(df.format(stats.getStandardDeviation()));
                featureList.add( Double.isNaN(stats.getSkewness()) ? "0.00000" : df.format(stats.getSkewness()));
                featureList.add(df.format(stats.getVariance()));
                featureList.add(df.format(stats.getPercentile(98)));
                featureList.add(df.format(stats.getPercentile(2)));
                featureList.add(df.format(stats.getPercentile(75)));
                featureList.add(df.format(stats.getPercentile(25)));

                featureList.add(String.valueOf(distinctCards.getN()));
                featureList.add(df.format(distinctCards.getMean()));
                featureList.add(df.format(distinctCards.getQuadraticMean()));
                featureList.add(Double.isNaN(distinctCards.getKurtosis()) ? "0.00000" : df.format(distinctCards.getKurtosis()));
                featureList.add(df.format(distinctCards.getStandardDeviation()));
                featureList.add(Double.isNaN(distinctCards.getSkewness()) ? "0.00000" : df.format(distinctCards.getSkewness()));
                featureList.add(df.format(distinctCards.getVariance()));

                featureList.add(df.format(cardPrecentages.getMin()));
                featureList.add(df.format(cardPrecentages.getMax()));
                featureList.add(df.format(modePercentage));
                featureList.add(df.format(zeroPercentage));
                featureList.add(df.format(onePercentage));
                featureList.add(df.format(cardPrecentages.getMean()));
                featureList.add(df.format(cardPrecentages.getQuadraticMean()));
                featureList.add( Double.isNaN(cardPrecentages.getKurtosis()) ? "0.00000" : df.format(cardPrecentages.getKurtosis()));
                featureList.add(df.format(cardPrecentages.getStandardDeviation()));
                featureList.add( Double.isNaN(cardPrecentages.getSkewness()) ? "0.00000" : df.format(cardPrecentages.getSkewness()));
                featureList.add(df.format(cardPrecentages.getVariance()));

                System.out.println(Joiner.on(",").join(featureList));


//                List<String> featureList = new ArrayList<>();
//                featureList.add(clazz);
//                featureList.add(property);
//                featureList.add(String.valueOf(stats.getN()));
//                featureList.add(String.valueOf(stats.getMin()));
//                featureList.add(String.valueOf(stats.getMax()));
//                featureList.add(String.valueOf(stats.getMean()));
//                for (int i = 0; i < 14; i++) {
//                    featureList.add(cardMap.get(i) != null ? df.format(cardMap.get(i).doubleValue()) : "0.00000");
//                }
//                System.out.println(Joiner.on(",").join(featureList));

            }

        }

    }

    public static String readFile(String path, Charset encoding)
            throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    private static long getInstacneCount(String clazz) {

        ParameterizedSparqlString pss = new ParameterizedSparqlString();
        pss.setCommandText(instanceCountQuery);
        pss.setIri("class", clazz);
        String queryString = pss.toString();

        return SparqlUtils.executeQueryForLong(queryString,ENDPOINT,"count");

    }

    public static List<String> getTopProperties(String clazz, int limit) {

        ParameterizedSparqlString pss = new ParameterizedSparqlString();
        pss.setCommandText(topPropertiesQuery);
        pss.setIri("class", clazz);
        pss.setLiteral("limit", limit);

        String queryString = pss.toString();

        List<RDFNode> props = SparqlUtils.executeQueryForList(queryString, ENDPOINT, "p");

        return  props.stream()
                .map(p -> p.asResource().getURI().toString())
                .collect(Collectors.toList());
    }

    public static List<Cardinality> getCardinalityCounts(String clazz, String property, long instanceCount) {

        List<Cardinality> cardinalities = new ArrayList<>();
        long total = 0;

        ParameterizedSparqlString pss = new ParameterizedSparqlString();
        pss.setCommandText(propertyCardinalityQuery);
        pss.setIri("class", clazz);
        pss.setIri("p", property);
        String queryString = pss.toString();

        List<Map<String, RDFNode>> maps = SparqlUtils.executeQueryForList(queryString, ENDPOINT, ImmutableSet.of("card", "count"));

        for (Map<String, RDFNode> map : maps) {
            if (map.containsKey("card") && map.containsKey("count")) {
                int card = map.get("card").asLiteral().getInt();
                long count = map.get("count").asLiteral().getLong();
                total += count;

                Cardinality cardinality = new Cardinality(card, count, (((double) count)/instanceCount));
                cardinalities.add(cardinality);

            } else {
                new IllegalStateException("Each result should contain the binding for ?card and ?count");
            }
        }

        long zeroCount = instanceCount - total;
        if (zeroCount > 0) {
            Cardinality cardinality = new Cardinality(0, zeroCount, ((double) zeroCount)/instanceCount);
            cardinalities.add(cardinality);
        }

//        double test = 0;
//        for (Cardinality c : cardinalities) {
//            test += c.getPrecentage();
//        }
//        System.out.println("total precentage" + test);

        return cardinalities;
    }


}
