package es.upm.oeg.tools.rdfshapes.mappings;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;
import com.hp.hpl.jena.query.ParameterizedSparqlString;
import com.hp.hpl.jena.rdf.model.RDFNode;
import es.upm.oeg.tools.rdfshapes.extractors.QueryBase;
import es.upm.oeg.tools.rdfshapes.utils.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.ForkJoinPool;

import static es.upm.oeg.tools.rdfshapes.utils.SparqlUtils.executeQueryForList;
import static es.upm.oeg.tools.rdfshapes.utils.SparqlUtils.executeQueryForMap;

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
public class MisusedProps {

    public static final String SPARQL_ENDPOINT = "http://4v.dia.fi.upm.es:8890/sparql";
    //public static final String SPARQL_ENDPOINT = "http://172.17.0.1:8890/sparql";

    private static final String Q1_PATH = "mappings/q1.rq";
    private static final String Q2_PATH = "mappings/q2.rq";
    private static final String Q3_PATH = "mappings/q3.rq";
    private static final String Q4_PATH = "mappings/q4.rq";
    private static final String Q5_PATH = "mappings/q5.rq";
    private static final String Q1_String;
    private static final String Q2_String;
    private static final String Q3_String;
    private static final String Q4_String;
    private static final String Q5_String;

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#,###,###,##0.0000");

    private static final Logger logger = LoggerFactory.getLogger(MisusedProps.class);

    static {
        try {
            Q1_String = IOUtils.readFile(Q1_PATH, Charset.defaultCharset());
            Q2_String = IOUtils.readFile(Q2_PATH, Charset.defaultCharset());
            Q3_String = IOUtils.readFile(Q3_PATH, Charset.defaultCharset());
            Q4_String = IOUtils.readFile(Q4_PATH, Charset.defaultCharset());
            Q5_String = IOUtils.readFile(Q5_PATH, Charset.defaultCharset());
        } catch (IOException ioe) {
            logger.error("Error loading the queries", ioe);
            throw new IllegalStateException("Error loading the query :" + ioe.getMessage(), ioe);
        }
    }

    String graph1;

    String graph2;

    String rGraph1;

    String rGraph2;

    String infoboxPrefix1;

    String infoboxPrefix2;

    String classGraph1;

    String classGraph2;

    BufferedWriter writer;

    final Object lock = new Object();

    public static void main(String[] args) throws Exception {

        MisusedProps props = new MisusedProps();
        props.process();

    }

/*
http://es.dbpedia.org/r
http://es.dbpedia.org/lit/r
http://es.dbpedia.org
http://es.dbpedia.org/lit


*/


    //Initialize parameters
    private void init() throws IOException {
//        graph1 =  "http://en.dbpedia.org";
//        graph2 = "http://nl.dbpedia.org";
//        rGraph1 = "http://en.dbpedia.org/r";
//        rGraph2 = "http://nl.dbpedia.org/r";

        graph1 =  "http://en.dbpedia.org/lit";
        graph2 = "http://de.dbpedia.org/lit";
        rGraph1 = "http://en.dbpedia.org/lit/r";
        rGraph2 = "http://de.dbpedia.org/lit/r";

        infoboxPrefix1 = "http://mappings.dbpedia.org/server/mappings/en/";
        infoboxPrefix2 = "http://mappings.dbpedia.org/server/mappings/de/";

        classGraph1 = "http://en.dbpedia.org/map";
        classGraph2 = "http://de.dbpedia.org/map";



        Path path = FileSystems.getDefault().getPath("/home/nandana/data/mappings/en-de-lit.csv");
        writer = Files.newBufferedWriter(path, Charset.defaultCharset(),
                StandardOpenOption.CREATE);
    }

    //Extract the data
    public void process() throws Exception {

        //Initialize the parameters
        //TODO config as command line parameters
        init();

        //Extract the property pairs
        List<PropPair> propPairList = extractPropPairs();

        //Extract the metrics
        new ForkJoinPool(20).submit(() -> {
            propPairList.parallelStream()
                    .forEach(propPair -> {
                        collectMetrics(propPair);
                    });
        }).get();

        writer.close();


    }

    private List<PropPair> extractPropPairs() {

        List<PropPair> propPairList = new ArrayList<>();

        ParameterizedSparqlString pss = new ParameterizedSparqlString();
        pss.setCommandText(Q1_String);
        pss.setIri("graph1", graph1);
        pss.setIri("graph2", graph2);
        pss.setIri("rGraph1", rGraph1);
        pss.setIri("rGraph2", rGraph2);
        String q1 = pss.toString();

        System.out.println(q1);

        logger.debug("Query 1:\n{}", q1);

        List<Map<String, RDFNode>> resultsMap = executeQueryForList(q1, SPARQL_ENDPOINT,
                Sets.newHashSet("p1", "p2", "t1", "t2", "a1", "a2", "count"));

        for (Map<String, RDFNode> map : resultsMap) {
            String t1 = map.get("t1").asLiteral().getString();
            String t2 = map.get("t2").asLiteral().getString();
            String p1 = map.get("p1").asResource().getURI();
            String p2 = map.get("p2").asResource().getURI();
            String a1 = map.get("a1").asLiteral().getString();
            String a2 = map.get("a2").asLiteral().getString();
            long count = map.get("count").asLiteral().getLong();

            PropPair pair = new PropPair(t1, t2, a1, a2, p1, p2, count);
            propPairList.add(pair);
        }

        logger.info("{} properties found", propPairList.size());

        return propPairList;
    }


    private void collectMetrics(PropPair propPair) {

        Thread.currentThread().setName("collect-metrics-" + getPrefixedProperty(propPair.getPropA())
                + "-"+ getPrefixedProperty(propPair.getPropB()));

        DBO dbo = new DBO();

        logger.debug("Collecting metrics for {}, {}, {}, {}",
                getPrefixedProperty(propPair.getPropA()),
                getPrefixedProperty(propPair.getPropB()),
                propPair.getTemplateA(),
                propPair.getTemplateB()
        );

        ParameterizedSparqlString q2pss = new ParameterizedSparqlString();
        q2pss.setCommandText(Q2_String);
        q2pss.setIri("graph1", graph1);
        q2pss.setIri("graph2", graph2);
        q2pss.setIri("rGraph1", rGraph1);
        q2pss.setIri("rGraph2", rGraph2);
        q2pss.setIri("p1", propPair.getPropA());
        q2pss.setIri("p2", propPair.getPropB());
        q2pss.setLiteral("t1", propPair.getTemplateA());
        q2pss.setLiteral("t2", propPair.getTemplateB());
        q2pss.setLiteral("a1", propPair.getAttributeA());
        q2pss.setLiteral("a2", propPair.getAttributeB());
        String q2 = q2pss.toString();

        Map<String, RDFNode> resultsList;
        try {
            resultsList = executeQueryForMap(q2, SPARQL_ENDPOINT,
                    Sets.newHashSet("count"));
        } catch (Exception e) {
            logger.error("Error executing query: {}\n{}", e.getMessage(), q2, e);
            return;
        }

        if (resultsList.size() > 0) {
            long count = resultsList.get("count").asLiteral().getLong();
            propPair.setM2(count);
        }

        ParameterizedSparqlString q3pss = new ParameterizedSparqlString();
        q3pss.setCommandText(Q3_String);
        q3pss.setIri("graph", graph1);
        q3pss.setIri("rGraph1", rGraph1);
        q3pss.setIri("rGraph2", rGraph2);
        q3pss.setLiteral("t1", propPair.getTemplateA());
        q3pss.setLiteral("t2", propPair.getTemplateB());
        q3pss.setIri("p1", propPair.getPropA());
        q3pss.setIri("p2", propPair.getPropB());
        q2pss.setLiteral("a1", propPair.getAttributeA());
        q2pss.setLiteral("a2", propPair.getAttributeB());
        String q3a = q3pss.toString();

        try {
            resultsList = executeQueryForMap(q3a, SPARQL_ENDPOINT,
                    Sets.newHashSet("count"));
        } catch (Exception e) {
            logger.error("Error executing query: {}\n{}", e.getMessage(), q3a, e);
            return;
        }


        if (resultsList.size() > 0) {
            long count = resultsList.get("count").asLiteral().getLong();
            propPair.setM3a(count);
        }

        q3pss.setIri("graph", graph2);
        String q3b = q3pss.toString();


        try {
            resultsList = executeQueryForMap(q3b, SPARQL_ENDPOINT,
                    Sets.newHashSet("count"));
        } catch (Exception e) {
            logger.error("Error executing query: {}, \n{}", e.getMessage(), q3b, e);
            return;
        }


        if (resultsList.size() > 0) {
            long count = resultsList.get("count").asLiteral().getLong();
            propPair.setM3b(count);
        }

        ParameterizedSparqlString q4pss = new ParameterizedSparqlString();
        q4pss.setCommandText(Q4_String);
        q4pss.setIri("graph1", graph1);
        q4pss.setIri("graph2", graph2);
        q4pss.setIri("rGraph1", rGraph1);
        q4pss.setIri("rGraph2", rGraph2);
        q4pss.setLiteral("t1", propPair.getTemplateA());
        q4pss.setLiteral("t2", propPair.getTemplateB());
        q4pss.setIri("p1", propPair.getPropA());
        q4pss.setIri("p2", propPair.getPropB());
        q2pss.setLiteral("a1", propPair.getAttributeA());
        q2pss.setLiteral("a2", propPair.getAttributeB());
        String q4 = q4pss.toString();

        try {
            resultsList = executeQueryForMap(q4, SPARQL_ENDPOINT,
                    Sets.newHashSet("count"));
        } catch (Exception e) {
            logger.error("Error executing query: {}\n{}", e.getMessage(), q4, e);
            return;
        }


        if (resultsList.size() > 0) {
            long count = resultsList.get("count").asLiteral().getLong();
            propPair.setM4(count);
        }

        ParameterizedSparqlString q5pss = new ParameterizedSparqlString();
        q5pss.setCommandText(Q5_String);
        q5pss.setIri("graph", graph1);
        q5pss.setIri("rGraph1", rGraph1);
        q5pss.setIri("rGraph2", rGraph2);
        q5pss.setLiteral("t1", propPair.getTemplateA());
        q5pss.setLiteral("t2", propPair.getTemplateB());
        q5pss.setIri("p1", propPair.getPropA());
        q5pss.setIri("p2", propPair.getPropB());
        q2pss.setLiteral("a1", propPair.getAttributeA());
        q2pss.setLiteral("a2", propPair.getAttributeB());
        String q5a = q5pss.toString();

        try {
            resultsList = executeQueryForMap(q5a, SPARQL_ENDPOINT,
                    Sets.newHashSet("count"));
        } catch (Exception e) {
            logger.error("Error executing query: {}\n{}", e.getMessage(), q5a, e);
            return;
        }


        if (resultsList.size() > 0) {
            long count = resultsList.get("count").asLiteral().getLong();
            propPair.setM5a(count);
        }

        q5pss.setIri("graph", graph2);
        String q5b = q5pss.toString();

        try {
            resultsList = executeQueryForMap(q5b, SPARQL_ENDPOINT,
                    Sets.newHashSet("count"));
        } catch (Exception e) {
            logger.error("Error executing query: {}\n{}", e.getMessage(), q5a, e);
            return;
        }


        if (resultsList.size() > 0) {
            long count = resultsList.get("count").asLiteral().getLong();
            propPair.setM5b(count);
        }


        synchronized (lock) {
            try {
                writer.write(propPair.getTemplateA()
                        + ", " + propPair.getAttributeA()
                        + ", " + propPair.getTemplateB()
                        + ", " + propPair.getAttributeB()
                        + ", " + getPrefixedProperty(propPair.getPropA())
                        + ", " + getPrefixedProperty(propPair.getPropB())
                        + ", " + getClass(classGraph1, infoboxPrefix1 + propPair.getTemplateA())
                        + ", " + getClass(classGraph2, infoboxPrefix2 + propPair.getTemplateB())
                        + ", " + getPrefixedProperty(dbo.getDomain(propPair.getPropA()))
                        + ", " + getPrefixedProperty(dbo.getDomain(propPair.getPropB()))
                        + ", " + getPrefixedProperty(dbo.getRange(propPair.getPropA()))
                        + ", " + getPrefixedProperty(dbo.getRange(propPair.getPropB()))
                        + ", " + DECIMAL_FORMAT.format(((double) propPair.getM1()) / propPair.getM4())
                        + ", " + DECIMAL_FORMAT.format(((double) propPair.getM2()) / propPair.getM4())
                        + ", " + DECIMAL_FORMAT.format(((double) propPair.getM3a()) / propPair.getM5a())
                        + ", " + DECIMAL_FORMAT.format(((double) propPair.getM3b()) / propPair.getM5b())
                        + ", " + propPair.getM4()
                        + ", " + propPair.getM1()
                        + ", " + propPair.getM2()
                        + ", " + propPair.getM3a()
                        + ", " + propPair.getM3b()
                        + ", " + propPair.getM5a()
                        + ", " + propPair.getM5b()
                );
                writer.newLine();
                writer.flush();
            } catch (Exception e) {
                logger.error("Error serializing the results! {}", e.getMessage(), e);
            }
        }
    }

    private static String getPrefixedProperty(String property) {
        property = property.replace("http://dbpedia.org/ontology/", "dbo:");
        property = property.replace("http://www.w3.org/2001/XMLSchema#", "xsd:");
        property = property.replace("http://www.w3.org/2002/07/owl#", "owl:");
        return property.replace("http://xmlns.com/foaf/0.1/", "foaf:");
    }

    private static String getClass(String graph, String infobox) {

        StringBuffer sb = new StringBuffer();
        sb.append("PREFIX rr: <http://www.w3.org/ns/r2rml#> ");
        sb.append("select ?class where { ");
        sb.append("  graph <" + graph +"> { ");
        sb.append("    <" + infobox + "> a rr:TriplesMap ; ");
        sb.append("    rr:subjectMap ?subjectMap . ");
        sb.append("    ?subjectMap rr:class ?class . } } ");

        String query = sb.toString();

        final List<RDFNode> classList = QueryBase.executeQueryForList(query, SPARQL_ENDPOINT, "class");

        Set<String> classes = new HashSet<>();
        for (RDFNode clazz : classList) {
            if(clazz.isURIResource()) {
                classes.add(clazz.asResource().getURI());
            }
        }

        if (classes.size() > 0) {
            return Joiner.on(",").join(classes);
        } else {
            return "";
        }
    }

    private static String subClass(String classA, String classB) {

        if (classA == null || classB == null) {
            return "n/a";
        } else if (classA.equals(classA)) {
            return "same";
        } else if (DBO.isSubClass(classA, classB)) {
            return "A -> B";
        } else if (DBO.isSubClass(classB, classA)) {
            return "B -> A";
        } else {
            return "none";
        }
    }

    private static String subProperty(String propA, String propB) {

        if (propA == null || propB == null) {
            return "n/a";
        } else if (propA.equals(propA)) {
            return "same";
        } else if (DBO.isSubProperty(propA, propB)) {
            return "A -> B";
        } else if (DBO.isSubProperty(propB, propA)) {
            return "B -> A";
        } else {
            return "none";
        }
    }
}
