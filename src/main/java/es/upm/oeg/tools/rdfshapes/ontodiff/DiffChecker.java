package es.upm.oeg.tools.rdfshapes.ontodiff;

import com.google.common.collect.ImmutableList;
import com.hp.hpl.jena.query.ParameterizedSparqlString;
import es.upm.oeg.tools.rdfshapes.utils.IOUtils;
import es.upm.oeg.tools.rdfshapes.utils.SparqlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;
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
public class DiffChecker {

    private static final Logger logger = LoggerFactory.getLogger(DiffChecker.class);

    private static final String RDFS_CLASS_DIFF_QS;

    private static final String RDFS_CLASS_COUNT_QS;

    private static final String RDF_PROPERTY_DIFF_QS;

    private static final String RDF_PROPERTY_COUNT_QS;

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
            RDFS_CLASS_DIFF_QS = IOUtils.readFile("ontodiff/rdfsClassDiff.rq", Charset.defaultCharset());
            RDFS_CLASS_COUNT_QS = IOUtils.readFile("ontodiff/rdfsClassCount.rq", Charset.defaultCharset());
            RDF_PROPERTY_DIFF_QS = IOUtils.readFile("ontodiff/rdfPropertyDiff.rq", Charset.defaultCharset());
            RDF_PROPERTY_COUNT_QS = IOUtils.readFile("ontodiff/rdfPropertyCount.rq", Charset.defaultCharset());
        } catch (IOException ioe) {
            logger.error("Error loading the queries", ioe);
            throw new IllegalStateException("Error loading the query :" + ioe.getMessage(), ioe);
        }
    }

    public static void main(String[] args) {

        String prefix = "http://schema.org";
        String sparqlEndpoint = "http://4v.dia.fi.upm.es:8890/sparql";

        List<String> graphs = ImmutableList.of("http://schema.org/2012-04-27.n3",
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


            DiffChecker diff = new DiffChecker(graphs, prefix, sparqlEndpoint);
            diff.rdfPropertyDiff();

    }


    private void rdfsClassCount() {
        //print the version counts
        ParameterizedSparqlString rdfsClassCountQuery = new ParameterizedSparqlString();
        rdfsClassCountQuery.setCommandText(RDFS_CLASS_COUNT_QS);
        rdfsClassCountQuery.setLiteral("prefix", prefix);

        for (String graph : graphs) {

            rdfsClassCountQuery.setIri("graph", graph);
            String queryString = rdfsClassCountQuery.toString();
            long count = SparqlUtils.executeQueryForLong(queryString, sparqlEndpoint, "count");

            System.out.println(graph + " : " + count);

        }
    }


    private void rdfPropertyCount() {

        ParameterizedSparqlString pss = new ParameterizedSparqlString();
        pss.setCommandText(RDF_PROPERTY_COUNT_QS);
        pss.setLiteral("prefix", prefix);

        System.out.println("RDF Property Count");

        for (String graph : graphs) {

            pss.setIri("graph", graph);
            String queryString = pss.toString();
            long count = SparqlUtils.executeQueryForLong(queryString, sparqlEndpoint, "count");

            System.out.println(graph + " : " + count);

        }
    }

    private void rdfClassDiff(){
        ParameterizedSparqlString pss = new ParameterizedSparqlString();
        pss.setCommandText(RDFS_CLASS_DIFF_QS);
        pss.setLiteral("prefix", prefix);

        System.out.println("Diff - Added");

        for (int i = 0; i < (graphs.size()-1) ; i++) {
            String graph1 = graphs.get(i+1);
            String graph2 = graphs.get(i);
            pss.setIri("graph1", graph1);
            pss.setIri("graph2", graph2);

            long count = SparqlUtils.executeQueryForLong(pss.toString(), sparqlEndpoint, "count");
            System.out.println( graph1 + " > " + graph2 + " : " + count);

        }

        System.out.println("Diff - Deleted");

        for (int i = 0; i < (graphs.size()-1) ; i++) {
            String graph1 = graphs.get(i);
            String graph2 = graphs.get(i+1);
            pss.setIri("graph1", graph1);
            pss.setIri("graph2", graph2);

            long count = SparqlUtils.executeQueryForLong(pss.toString(), sparqlEndpoint, "count");
            System.out.println( graph2 + " > " + graph1 + " : " + count);

        }

    }

    private void rdfPropertyDiff(){
        ParameterizedSparqlString pss = new ParameterizedSparqlString();
        pss.setCommandText(RDF_PROPERTY_DIFF_QS);
        pss.setLiteral("prefix", prefix);

        System.out.println("RDF Property Diff - Added");

        for (int i = 0; i < (graphs.size()-1) ; i++) {
            String graph1 = graphs.get(i+1);
            String graph2 = graphs.get(i);
            pss.setIri("graph1", graph1);
            pss.setIri("graph2", graph2);

            long count = SparqlUtils.executeQueryForLong(pss.toString(), sparqlEndpoint, "count");
            System.out.println( graph1 + " > " + graph2 + " : " + count);

        }

        System.out.println("RDF Property Diff - Deleted");

        for (int i = 0; i < (graphs.size()-1) ; i++) {
            String graph1 = graphs.get(i);
            String graph2 = graphs.get(i+1);
            pss.setIri("graph1", graph1);
            pss.setIri("graph2", graph2);

            long count = SparqlUtils.executeQueryForLong(pss.toString(), sparqlEndpoint, "count");
            System.out.println( graph2 + " > " + graph1 + " : " + count);

        }

    }

}
