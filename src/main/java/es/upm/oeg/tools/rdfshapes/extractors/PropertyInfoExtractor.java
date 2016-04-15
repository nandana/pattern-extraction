package es.upm.oeg.tools.rdfshapes.extractors;

import com.google.common.collect.Sets;
import com.hp.hpl.jena.query.ParameterizedSparqlString;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import es.upm.oeg.tools.rdfshapes.ClassMatrixGenerator;
import es.upm.oeg.tools.rdfshapes.Count;
import es.upm.oeg.tools.rdfshapes.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.stream.file.FileSinkDOT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static es.upm.oeg.tools.rdfshapes.utils.IOUtils.readFile;
import static es.upm.oeg.tools.rdfshapes.utils.SparqlUtils.executeQueryForList;
import static es.upm.oeg.tools.rdfshapes.utils.SparqlUtils.executeQueryForLong;
import static es.upm.oeg.tools.rdfshapes.utils.RDFTermUtils.getPrefixedTerm;

/**
 * Copyright 2014-2016 Ontology Engineering Group, Universidad Politécnica de Madrid, Spain
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @author Nandana Mihindukulasooriya
 * @since 1.0.0
 */
public class PropertyInfoExtractor {

    private static final Logger logger = LoggerFactory.getLogger(PropertyInfoExtractor.class);

    private static final String QUERY2_PATH = "src/main/resources/query/countIndividualsWithProperty.rq";
    private static final String QUERY_PATH = "src/main/resources/query/propertyDomain.rq";
    private static final String QUERY5_PATH = "src/main/resources/query/propertyDomainList.rq";
    private static final String QUERY3_PATH = "src/main/resources/query/propertyListOfClass.rq";
    private static final String QUERY4_PATH = "src/main/resources/query/propertyURIObjectList.rq";
    private static final String QUERY6_PATH = "src/main/resources/query/isTypeOf.rq";
    private static final String QUERY7_PATH = "src/main/resources/query/propertyList.rq";
    private static final String QUERY8_PATH = "src/main/resources/query/propertyDistinctSubjectCount.rq";

    private static final String DOMAIN_CLASS_VAR = "domainClass";
    public static final String COUNT_VAR = "count";
    public static final String CLASS_VAR = "class";
    public static final String OBJECT_VAR = "object";
    public static final String PROPERTY_VAR = "property";

    private static String propertyDomainQueryString;
    private static String propertyDomainListQueryString;
    private static String individualCountQueryString;
    private static String propertyListofClassQueryString;
    private static String propertyListQueryString;
    private static String propertyObjectsQueryString;
    private static String isTypeOfQueryString;
    private static String propertyDistinctSubjectCountQueryString;


    static {
        try {
            propertyDomainQueryString = readFile(QUERY_PATH, Charset.defaultCharset());
            propertyDomainListQueryString = readFile(QUERY5_PATH, Charset.defaultCharset());
            individualCountQueryString = readFile(QUERY2_PATH, Charset.defaultCharset());
            propertyListofClassQueryString = readFile(QUERY3_PATH, Charset.defaultCharset());
            propertyListQueryString = readFile(QUERY7_PATH, Charset.defaultCharset());
            propertyObjectsQueryString = readFile(QUERY4_PATH, Charset.defaultCharset());
            isTypeOfQueryString = readFile(QUERY6_PATH, Charset.defaultCharset());
            propertyDistinctSubjectCountQueryString = readFile(QUERY8_PATH, Charset.defaultCharset());
        } catch (IOException ioe) {
            throw new IllegalStateException("Error loading the query :" + QUERY_PATH);
        }
    }

    public static long getPropertyDistinctSubjectCount(String property, String sparqlEndpoint) {

        ParameterizedSparqlString pss = new ParameterizedSparqlString();
        pss.setCommandText(propertyDistinctSubjectCountQueryString);
        pss.setIri(PROPERTY_VAR, property);
        String propertyDistinctSubjectCountQuery = pss.toString();

        return executeQueryForLong(propertyDistinctSubjectCountQuery, sparqlEndpoint, COUNT_VAR);

    }



    public static ArrayList<Count> extractProperties(String sparqlEndpoint) {

        ArrayList<Count> properties = new ArrayList<>();

        ParameterizedSparqlString pss = new ParameterizedSparqlString();
        pss.setCommandText(propertyListQueryString);
        String propertyDomainQuery = pss.toString();

        List<Map<String, RDFNode>> resultsMap = executeQueryForList(propertyDomainQuery, sparqlEndpoint,
                Sets.newHashSet(PROPERTY_VAR, COUNT_VAR));

        for (Map<String, RDFNode> map : resultsMap) {
            RDFNode propertyNode = map.get(PROPERTY_VAR);
            RDFNode countNode = map.get(COUNT_VAR);

            if (propertyNode != null && countNode != null
                    && propertyNode.isURIResource() && countNode.isLiteral()) {
                String domainClass = propertyNode.asResource().getURI();
                long count = countNode.asLiteral().getLong();
                properties.add(new Count(domainClass, count));
            }
        }

        return properties;
    }


    public static ArrayList<Count> extractPropertiesOfClass(String clazz, String sparqlEndpoint) {

        ArrayList<Count> properties = new ArrayList<>();

        ParameterizedSparqlString pss = new ParameterizedSparqlString();
        pss.setCommandText(propertyListofClassQueryString);
        pss.setIri(CLASS_VAR, clazz);
        String propertyDomainQuery = pss.toString();

        List<Map<String, RDFNode>> resultsMap = executeQueryForList(propertyDomainQuery, sparqlEndpoint,
                Sets.newHashSet(PROPERTY_VAR, COUNT_VAR));

        for (Map<String, RDFNode> map : resultsMap) {
            RDFNode propertyNode = map.get(PROPERTY_VAR);
            RDFNode countNode = map.get(COUNT_VAR);

            if (propertyNode != null && countNode != null
                    && propertyNode.isURIResource() && countNode.isLiteral()) {
                String domainClass = propertyNode.asResource().getURI();
                long count = countNode.asLiteral().getLong();
                properties.add(new Count(domainClass, count));
            }
        }

        return properties;
    }

    public static ArrayList<Count> extractDomainClasses(String property, String sparqlEndpoint) {

        ArrayList<Count> domainClasses = new ArrayList<>();

        ParameterizedSparqlString pss = new ParameterizedSparqlString();
        pss.setCommandText(propertyDomainQueryString);
        pss.setIri(PROPERTY_VAR,property);
        String propertyDomainQuery = pss.toString();

        List<Map<String, RDFNode>> resultsMap = executeQueryForList(propertyDomainQuery, sparqlEndpoint,
                Sets.newHashSet(DOMAIN_CLASS_VAR, COUNT_VAR));

        for (Map<String, RDFNode> map : resultsMap) {
            RDFNode domainClassNode = map.get(DOMAIN_CLASS_VAR);
            RDFNode countNode = map.get(COUNT_VAR);

            if (domainClassNode != null && countNode != null
                    && domainClassNode.isURIResource() && countNode.isLiteral()) {
                String domainClass = domainClassNode.asResource().getURI();
                long count = countNode.asLiteral().getLong();
                domainClasses.add(new Count(domainClass, count));
            }
        }

        return domainClasses;
    }

    public static ArrayList<String> extractDomainClassesList(String property, String sparqlEndpoint) {

        ArrayList<String> domainClasses = new ArrayList<>();

        ParameterizedSparqlString pss = new ParameterizedSparqlString();
        pss.setCommandText(propertyDomainListQueryString);
        pss.setIri(PROPERTY_VAR,property);
        String propertyDomainQuery = pss.toString();

        List<Map<String, RDFNode>> resultsMap = executeQueryForList(propertyDomainQuery, sparqlEndpoint,
                Sets.newHashSet(DOMAIN_CLASS_VAR));

        for (Map<String, RDFNode> map : resultsMap) {
            RDFNode domainClassNode = map.get(DOMAIN_CLASS_VAR);

            if (domainClassNode != null && domainClassNode.isURIResource()) {
                String domainClass = domainClassNode.asResource().getURI();
                domainClasses.add(domainClass);
            }
        }

        return domainClasses;
    }

    public static ArrayList<String> extractRangeClassList(String clazz, String property, String sparqlEndpoint) {

        ArrayList<String> domainClasses = new ArrayList<>();

        ParameterizedSparqlString pss = new ParameterizedSparqlString();
        pss.setCommandText(propertyDomainListQueryString);
        pss.setIri(CLASS_VAR, clazz);
        pss.setIri(PROPERTY_VAR,property);
        String propertyDomainQuery = pss.toString();

        List<Map<String, RDFNode>> resultsMap = executeQueryForList(propertyDomainQuery, sparqlEndpoint,
                Sets.newHashSet(DOMAIN_CLASS_VAR));

        for (Map<String, RDFNode> map : resultsMap) {
            RDFNode domainClassNode = map.get(DOMAIN_CLASS_VAR);

            if (domainClassNode != null && domainClassNode.isURIResource()) {
                String domainClass = domainClassNode.asResource().getURI();
                domainClasses.add(domainClass);
            }
        }

        return domainClasses;
    }

    public static ArrayList<Count> extractPropertyObjects(String clazz, String property, String sparqlEndpoint) {

        ArrayList<Count> objectURIs = new ArrayList<>();

        ParameterizedSparqlString pss = new ParameterizedSparqlString();
        pss.setCommandText(propertyObjectsQueryString);
        pss.setIri(CLASS_VAR, clazz);
        pss.setIri(PROPERTY_VAR,property);
        String propertyObjectsQuery = pss.toString();


        List<Map<String, RDFNode>> resultsMap = executeQueryForList(propertyObjectsQuery, sparqlEndpoint,
                Sets.newHashSet(OBJECT_VAR, COUNT_VAR));


        for (Map<String, RDFNode> map : resultsMap) {
            RDFNode domainClassNode = map.get(OBJECT_VAR);
            RDFNode countNode = map.get(COUNT_VAR);

            if (domainClassNode != null && countNode != null
                    && domainClassNode.isURIResource() && countNode.isLiteral()) {
                String domainClass = domainClassNode.asResource().getURI();
                long count = countNode.asLiteral().getLong();
                objectURIs.add(new Count(domainClass, count));
            }
        }

        return objectURIs;
    }

    public static boolean isTypeOf(String individual, String clazz, String sparqlEndpoint) {

        ParameterizedSparqlString pss = new ParameterizedSparqlString();
        pss.setCommandText(isTypeOfQueryString);
        pss.setIri("individual", individual);
        pss.setIri("class", clazz);
        String boundQuery = pss.toString();

        try (QueryExecution qe = QueryExecutionFactory.sparqlService(sparqlEndpoint, boundQuery);) {
            boolean isTypeOf = qe.execAsk();
            //System.out.println(isSubClass);
            return isTypeOf;
        }

    }


    private static long extractIndividualWithPropertyCount(String clazz, String property, String sparqlEndpoint) {

        ParameterizedSparqlString pss = new ParameterizedSparqlString();
        pss.setCommandText(individualCountQueryString);
        pss.setIri(CLASS_VAR, clazz);
        pss.setIri(PROPERTY_VAR,property);
        String individualCountQuery = pss.toString();

        try {

            return executeQueryForLong(individualCountQuery, sparqlEndpoint, COUNT_VAR);

        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }


    public static void main(String[] args) throws Exception {

        String sparqlService = "http://es.dbpedia.org/sparql";
        String clazz = "http://dbpedia.org/ontology/SoccerPlayer";
        String lovEndpoint = "http://lov.okfn.org/dataset/lov/sparql";
        String dBpediaService = "http://dbpedia.org/sparql";

        PropertyInfoExtractor domainExtractor = new PropertyInfoExtractor();

        //ArrayList<Count> properties = domainExtractor.extractPropertiesOfClass(clazz);
        ArrayList<Count> properties = new ArrayList<>();
        properties.add(new Count("http://dbpedia.org/ontology/country", 100));



        System.out.println("Property Count : " + properties.size());

        for (Count propertyCount : properties) {

            String property = propertyCount.getSubject();
            String propShort = getPrefixedTerm(property);

            System.out.println(propShort + " - " + property);

            Graph graph = new SingleGraph("Subclass graph");

            System.out.println(String.format("Class: %s , Property: %s ", clazz, property));
            System.out.println();

            long individualCount = domainExtractor.extractIndividualWithPropertyCount(clazz, property, sparqlService);
            System.out.println(String.format("Individuals of class '%s' with property '%s' : %d", clazz, property, individualCount));
            System.out.println();

            List<Count> domainCount = domainExtractor.extractDomainClasses(property, sparqlService);

            if (domainCount.size() == 0) {
                continue;
            }

            List<String> domainClasses = new ArrayList<>();

            System.out.println("Extracted domain classes");
            for (Count count : domainCount) {
                System.out.println(String.format("%-60s , Count : %5d , Percentage: %5f", count.getSubject(),
                        count.getCount(), ((double) count.getCount()) / individualCount));
                if (!"http://www.w3.org/2002/07/owl#Thing".equals(count.getSubject())) {
                    domainClasses.add(count.getSubject());
                    Node node = graph.addNode(count.getSubject());
                    node.setAttribute("ui.label", getPrefixedTerm(count.getSubject()) + " , " + count.getCount());
                }
            }

            System.out.println();
            System.out.println("Extracting equivalent classes from lov");
            List<Edge> equivalentClasses = ClassMatrixGenerator.getEquivalentClassRelations(domainClasses, lovEndpoint);

            System.out.println();
            System.out.println("Extracting equivalent classes from DBpedia");
            List<Edge> equivalentClassesDBpedia = ClassMatrixGenerator.getEquivalentClassRelations(domainClasses, dBpediaService);
            equivalentClasses.addAll(equivalentClassesDBpedia);

            System.out.println();
            System.out.println("Extracting subclasses from lov");
            List<Edge> subClassRelations = ClassMatrixGenerator.getSubClassRelations(domainClasses, lovEndpoint);

            System.out.println();
            System.out.println("Extracting subclasses from DBpedia");
            List<Edge> subClassRelationsDBpedia = ClassMatrixGenerator.getSubClassRelations(domainClasses, dBpediaService);
            subClassRelations.addAll(subClassRelationsDBpedia);


            int i = 0;
            for (Edge edge : subClassRelations) {
                graph.addEdge(Integer.toString(i++), edge.getStartNode(), edge.getEndNode(), true);
            }

            for (Edge edge : equivalentClasses) {
                graph.addEdge(Integer.toString(i++), edge.getStartNode(), edge.getEndNode());
            }

            FileSinkDOT fs = new FileSinkDOT();
            fs.writeAll(graph, "graph/" + propShort);

        }

    }


}
