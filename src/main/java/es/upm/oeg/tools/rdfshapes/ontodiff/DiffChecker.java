package es.upm.oeg.tools.rdfshapes.ontodiff;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.hp.hpl.jena.query.ParameterizedSparqlString;
import com.hp.hpl.jena.rdf.model.RDFNode;
import es.upm.oeg.tools.rdfshapes.utils.IOUtils;
import es.upm.oeg.tools.rdfshapes.utils.SparqlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;
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
public class DiffChecker {

    private static final Logger logger = LoggerFactory.getLogger(DiffChecker.class);

    private static final String DIFF_QS;

    private static final String DIFF_ANY_QS;

    private static final String COUNT_QS;

    private static final String COUNT_ANY_QS;

    private static final String RDFS_CLASS = "http://www.w3.org/2000/01/rdf-schema#Class";

    private static final String RDF_PROPERTY = "http://www.w3.org/1999/02/22-rdf-syntax-ns#Property";

    private static final String OWL_CLASS = "http://www.w3.org/2002/07/owl#Class";

    private static final String OWL_DATATYPE_PROPERTY = "http://www.w3.org/2002/07/owl#DatatypeProperty";

    private static final String OWL_OBJECT_PROPERTY = "http://www.w3.org/2002/07/owl#ObjectProperty";

    private static final String RDFS_SUBCLASS_PROPERTY = "http://www.w3.org/2000/01/rdf-schema#subClassOf";

    private static final String RDFS_SUBPROPERTY_PROPERTY = "http://www.w3.org/2000/01/rdf-schema#subPropertyOf";


    private List<String> graphs;

    private String prefix;

    private String sparqlEndpoint;

    public DiffChecker(List<String> graphs, String prefix, String sparqlEndpoint) {
        this.graphs = graphs;
        this.prefix = prefix;
        this.sparqlEndpoint = sparqlEndpoint;
    }

    static {
        try {
            DIFF_QS = IOUtils.readFile("ontodiff/diff.rq", Charset.defaultCharset());
            DIFF_ANY_QS = IOUtils.readFile("ontodiff/diff-any.rq", Charset.defaultCharset());
            COUNT_QS = IOUtils.readFile("ontodiff/count.rq", Charset.defaultCharset());
            COUNT_ANY_QS = IOUtils.readFile("ontodiff/count-any.rq", Charset.defaultCharset());
        } catch (IOException ioe) {
            logger.error("Error loading the queries", ioe);
            throw new IllegalStateException("Error loading the query :" + ioe.getMessage(), ioe);
        }
    }

    public static void main(String[] args) {

        //String prefix = "http://schema.org";

        String sparqlEndpoint = "http://4v.dia.fi.upm.es:8890/sparql";

        List<String> dbpGraphs = ImmutableList.of(
                "http://dbpedia.org/o/3.2",
                "http://dbpedia.org/o/3.3",
                "http://dbpedia.org/o/3.4",
                "http://dbpedia.org/o/3.5",
                "http://dbpedia.org/o/3.6",
                "http://dbpedia.org/o/3.7",
                "http://dbpedia.org/o/3.8",
                "http://dbpedia.org/o/3.9",
                "http://dbpedia.org/o/201504",
                "http://dbpedia.org/o/201510",
                "http://dbpedia.org/o/201604");
        String dbpPrefix = "http://dbpedia.org/ontology/";


        List<String> schemaGraphs = ImmutableList.of("http://schema.org/2012-04-27.n3",
                "http://schema.org/2012-06-26.n3",
                "http://schema.org/2012-07-26.n3",
                "http://schema.org/2012-11-08.n3",
                "http://schema.org/2013-04-05.n3",
                "http://schema.org/2013-07-24.n3",
                "http://schema.org/2013-08-07.n3",
                "http://schema.org/2013-11-19.n3",
                "http://schema.org/2013-12-04.n3",
                "http://schema.org/2014-02-05.n3",
                "http://schema.org/2014-04-04.n3",
                "http://schema.org/2014-04-16.n3",
                "http://schema.org/2014-05-16.n3",
                "http://schema.org/2014-05-27.n3",
                "http://schema.org/2014-06-16.n3",
                "http://schema.org/2014-07-08.n3",
                "http://schema.org/2014-07-28.n3",
                "http://schema.org/2014-08-19.n3",
                "http://schema.org/2014-09-12.n3",
                "http://schema.org/2014-12-11.n3",
                "http://schema.org/2015-02-04.n3",
                "http://schema.org/2015-05-12.n3",
                "http://schema.org/2015-08-06.n3",
                "http://schema.org/2015-11-05.n3");

        String schemaPrefix = "http://schema.org/";

        List<String> orgGraphs = ImmutableList.of("http://w3.org/org/2010-06-06.n3",
                "http://w3.org/org/2010-10-08.n3",
                "http://w3.org/org/2012-09-30.n3",
                "http://w3.org/org/2012-10-06.n3",
                "http://w3.org/org/2013-02-15.n3",
                "http://w3.org/org/2013-12-16.n3",
                "http://w3.org/org/2014-01-02.n3",
                "http://w3.org/org/2014-01-25.n3",
                "http://w3.org/org/2014-02-05.n3",
                "http://w3.org/org/2014-04-12.n3" );

        List<String> provGraphs = ImmutableList.of( "http://w3.org/prov/2012-05-03.n3",
                "http://w3.org/prov/2012-07-24.n3",
                "http://w3.org/prov/2012-12-11.ttl",
                "http://w3.org/prov/2013-03-12.ttl",
                "http://w3.org/prov/2013-04-30.n3",
                "http://w3.org/prov/2014-06-07.n3",
                "http://w3.org/prov/2015-01-11.n3"
        );
        String provPrefix = "http://www.w3.org/ns/prov#";

        List<String> foafGraphs = ImmutableList.of("http://foaf/2005-04-03.n3",
                "http://foaf/2005-05-19.n3",
                "http://foaf/2005-06-03.n3",
                "http://foaf/2007-01-14.n3",
                "http://foaf/2007-05-24.n3",
                "http://foaf/2007-10-02.n3",
                "http://foaf/2009-12-15.n3",
                "http://foaf/2010-01-01.n3",
                "http://foaf/2010-08-09.n3",
                "http://foaf/2014-01-14.n3");

        String foafPrefix  = "http://xmlns.com/foaf/0.1/";



        DiffChecker diff = new DiffChecker(foafGraphs, foafPrefix, sparqlEndpoint);
        diff.printDiff(OWL_CLASS);
    }


    private void printCount(String type) {
        //print the version counts
        ParameterizedSparqlString pss = new ParameterizedSparqlString();
        pss.setCommandText(COUNT_QS);
        pss.setLiteral("prefix", prefix);
        pss.setIri("type", type);

        //System.out.println("Counts (" + type + ")" );

        for (String graph : graphs) {

            pss.setIri("graph", graph);
            String queryString = pss.toString();
            long count = SparqlUtils.executeQueryForLong(queryString, sparqlEndpoint, "count");

            System.out.println(graph + " : " + count);

        }
    }

    private void printCountAny(String prop) {

        ParameterizedSparqlString pss = new ParameterizedSparqlString();
        pss.setCommandText(COUNT_ANY_QS);
        pss.setLiteral("prefix", prefix);
        pss.setIri("prop", prop);

        System.out.println("Property : " + prop);

        for (String graph : graphs) {

            pss.setIri("graph", graph);
            String queryString = pss.toString();
            long count = SparqlUtils.executeQueryForLong(queryString, sparqlEndpoint, "count");

            System.out.println(graph + " : " + count);

        }

    }

    private void printDiff(String type){
        ParameterizedSparqlString pss = new ParameterizedSparqlString();
        pss.setCommandText(DIFF_QS);
        pss.setLiteral("prefix", prefix);
        pss.setIri("type", type);

        System.out.println("Diff - Added");

        for (int i = 0; i < (graphs.size()-1) ; i++) {
            String graph1 = graphs.get(i+1);
            String graph2 = graphs.get(i);
            pss.setIri("graph1", graph1);
            pss.setIri("graph2", graph2);

            //System.out.println(pss.toString());

            //long count = SparqlUtils.executeQueryForLong(pss.toString(), sparqlEndpoint, "count");
            //System.out.println( graph1 + " > " + graph2 + " : " + count);

            List<Map<String, RDFNode>> test = SparqlUtils.executeQueryForList(pss.toString(), sparqlEndpoint, ImmutableSet.of("x"));
            System.out.println( graph1 + " > " + graph2 + " :  #########################################################" );

            for (Map<String, RDFNode> result : test) {
                System.out.println(result.get("x"));
            }


        }

        System.out.println("Diff - Deleted");

        for (int i = 0; i < (graphs.size()-1) ; i++) {
            String graph1 = graphs.get(i);
            String graph2 = graphs.get(i+1);
            pss.setIri("graph1", graph1);
            pss.setIri("graph2", graph2);

            //System.out.println(pss.toString());

            //long count = SparqlUtils.executeQueryForLong(pss.toString(), sparqlEndpoint, "count");
            //System.out.println( graph2 + " > " + graph1 + " : " + count);

            List<Map<String, RDFNode>> test = SparqlUtils.executeQueryForList(pss.toString(), sparqlEndpoint, ImmutableSet.of("x"));
            //System.out.println( graph2 + " > " + graph1 + " :  #########################################################" );

            for (Map<String, RDFNode> result : test) {
                //System.out.println(result.get("x"));
            }

        }

    }

    private void printAnyDiff(String prop){
        ParameterizedSparqlString pss = new ParameterizedSparqlString();
        pss.setCommandText(DIFF_ANY_QS);
        pss.setLiteral("prefix", prefix);
        pss.setIri("prop", prop);

        System.out.println("Property : " + prop);

        System.out.println("Diff - Added");

        for (int i = 0; i < (graphs.size()-1) ; i++) {
            String graph1 = graphs.get(i+1);
            String graph2 = graphs.get(i);
            pss.setIri("graph1", graph1);
            pss.setIri("graph2", graph2);

            //System.out.println(pss.toString());

            long count = SparqlUtils.executeQueryForLong(pss.toString(), sparqlEndpoint, "count");
            System.out.println( graph1 + " > " + graph2 + " : " + count);

        }

        System.out.println("Diff - Deleted");

        for (int i = 0; i < (graphs.size()-1) ; i++) {
            String graph1 = graphs.get(i);
            String graph2 = graphs.get(i+1);
            pss.setIri("graph1", graph1);
            pss.setIri("graph2", graph2);

            //System.out.println(pss.toString());

            long count = SparqlUtils.executeQueryForLong(pss.toString(), sparqlEndpoint, "count");
            System.out.println( graph2 + " > " + graph1 + " : " + count);

        }

    }



}
