package es.upm.oeg.tools.rdfshapes.mappings;

import com.google.common.collect.Sets;
import com.hp.hpl.jena.query.ParameterizedSparqlString;
import com.hp.hpl.jena.rdf.model.RDFNode;
import es.upm.oeg.tools.rdfshapes.utils.IOUtils;
import jdk.nashorn.internal.runtime.ECMAException;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

    //private static final String Q1_PATH = "src/main/resources/mappings/q1.rq";
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

    public static final String EN_OBJ_G1 = "http://dbpedia.org";
    public static final String ES_OBJ_G1 = "http://es.dbpedia.org";

    public static final String EN_TEMPLATE_G = "http://dbpedia.org/templates";
    public static final String ES_TEMPLATE_G = "http://dbpedia.org/es-templates";

    public static final String EN_TEMPLATE_PREFIX = "http://dbpedia.org/resource/Template:";
    public static final String ES_TEMPLATE_PREFIX = "http://es.dbpedia.org/resource/Plantilla:";

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

    String templateGraph1;

    String templateGraph2;

    BufferedWriter writer;

    final Object lock = new Object();

    public static void main(String[] args) throws Exception {

        MisusedProps props = new MisusedProps();
        props.process();

    }

    //Initialize parameters
    private void init() throws IOException {
        graph1 = EN_OBJ_G1;
        graph2 = ES_OBJ_G1;
        templateGraph1 = EN_TEMPLATE_G;
        templateGraph2 = ES_TEMPLATE_G;

        Path path = FileSystems.getDefault().getPath("/home/nandana/en-es-uri.csv");
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
        pss.setIri("templateGraph1", templateGraph1);
        pss.setIri("templateGraph2", templateGraph2);
        String q1 = pss.toString();

        logger.debug("Query 1:\n{}", q1);

        List<Map<String, RDFNode>> resultsMap = executeQueryForList(q1, SPARQL_ENDPOINT,
                Sets.newHashSet("p1", "p2", "t1", "t2", "count"));

        for (Map<String, RDFNode> map : resultsMap) {
            String t1 = map.get("t1").asResource().getURI();
            String t2 = map.get("t2").asResource().getURI();
            String p1 = map.get("p1").asResource().getURI();
            String p2 = map.get("p2").asResource().getURI();
            long count = map.get("count").asLiteral().getLong();

            PropPair pair = new PropPair(t1, t2, p1, p2, count);
            propPairList.add(pair);
        }

        logger.info("{} properties found", propPairList.size());

        return propPairList;
    }


    private void collectMetrics(PropPair propPair) {

        logger.debug("Collecting metrics for {}, {}, {}, {}",
                getPrefixedProperty(propPair.getPropA()),
                getPrefixedProperty(propPair.getPropB()),
                propPair.getTemplateA().replace(EN_TEMPLATE_PREFIX, ""),
                propPair.getTemplateB().replace(ES_TEMPLATE_PREFIX, "")
        );

        ParameterizedSparqlString q2pss = new ParameterizedSparqlString();
        q2pss.setCommandText(Q2_String);
        q2pss.setIri("graph1", graph1);
        q2pss.setIri("graph2", graph2);
        q2pss.setIri("templateGraph1", templateGraph1);
        q2pss.setIri("templateGraph2", templateGraph2);
        q2pss.setIri("p1", propPair.getPropA());
        q2pss.setIri("p2", propPair.getPropB());
        q2pss.setIri("t1", propPair.getTemplateA());
        q2pss.setIri("t2", propPair.getTemplateB());
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
        q3pss.setIri("templateGraph1", templateGraph1);
        q3pss.setIri("templateGraph2", templateGraph2);
        q3pss.setIri("t1", propPair.getTemplateA());
        q3pss.setIri("t2", propPair.getTemplateB());
        q3pss.setIri("p1", propPair.getPropA());
        q3pss.setIri("p2", propPair.getPropB());
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
        q4pss.setIri("templateGraph1", templateGraph1);
        q4pss.setIri("templateGraph2", templateGraph2);
        q4pss.setIri("t1", propPair.getTemplateA());
        q4pss.setIri("t2", propPair.getTemplateB());
        q4pss.setIri("p1", propPair.getPropA());
        q4pss.setIri("p2", propPair.getPropB());
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
        q5pss.setIri("templateGraph1", templateGraph1);
        q5pss.setIri("templateGraph2", templateGraph2);
        q5pss.setIri("t1", propPair.getTemplateA());
        q5pss.setIri("t2", propPair.getTemplateB());
        q5pss.setIri("p1", propPair.getPropA());
        q5pss.setIri("p2", propPair.getPropB());
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
                writer.write(propPair.getTemplateA().replace(EN_TEMPLATE_PREFIX, "")
                        + ", " + propPair.getTemplateB().replace(ES_TEMPLATE_PREFIX, "")
                        + ", " + getPrefixedProperty(propPair.getPropA())
                        + ", " + getPrefixedProperty(propPair.getPropB())
                        + ", " + DECIMAL_FORMAT.format(((double) propPair.getM1()) / propPair.getM4())
                        + ", " + DECIMAL_FORMAT.format(((double) propPair.getM2()) / propPair.getM4())
                        + ", " + DECIMAL_FORMAT.format(((double) propPair.getM3a()) / propPair.getM5a())
                        + ", " + DECIMAL_FORMAT.format(((double) propPair.getM3b()) / propPair.getM5b())
                        + ", " + propPair.getM4()
                        + ", " + propPair.getM1()
                        + ", " + propPair.getM2()
                        + ", " + propPair.getM3a()
                        + ", " + propPair.getM3b()
                        + ", " + propPair.getM4()
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
        return property.replace("http://dbpedia.org/ontology/", "dbo:");
    }
}
