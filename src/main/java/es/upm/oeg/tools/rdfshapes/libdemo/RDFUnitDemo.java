package es.upm.oeg.tools.rdfshapes.libdemo;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import es.upm.oeg.tools.rdfshapes.utils.RDFTermUtils;
import org.aksw.jena_sparql_api.core.QueryExecutionFactory;
import org.aksw.jena_sparql_api.model.QueryExecutionFactoryModel;
import org.aksw.rdfunit.services.PrefixNSService;
import org.aksw.rdfunit.services.SchemaService;
import org.aksw.rdfunit.sources.SchemaSource;
import org.aksw.rdfunit.utils.RDFUnitUtils;

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
public class RDFUnitDemo {

    static {
        RDFUnitUtils.fillSchemaServiceFromLOV();
        //RDFUnitUtils.fillSchemaServiceFromFile();
    }


    public static void main(String[] args) throws Exception {






//        try (QueryExecution qe = qef.createQueryExecution(queryString)) {
//            qe.execSelect().forEachRemaining(qs -> {
//
//                Resource s = qs.get("s").asResource();
//                Resource p = qs.get("p").asResource();
//                RDFNode o = qs.get("o");
//
//                model.add(s, ResourceFactory.createProperty(p.getURI()), o);
//
//                // save the data in a file to read later
//            });
//        }


//        for (SchemaSource schema : SchemaService.getSourceListAll(false,null)){
//            QueryExecutionFactory qef = new QueryExecutionFactoryModel(schema.getModel());
//
//            String queryString = PrefixNSService.getSparqlPrefixDecl() +
//                    " SELECT DISTINCT ?s ?p ?o WHERE { " +
//                    " ?s ?p ?o ." +
//                    " FILTER (?p IN (owl:sameAs, owl:equivalentProperty, owl:equivalentClass))" +
//                    // " FILTER (strStarts(?s, 'http://dbpedia.org') || strStarts(?o, 'http://dbpedia.org')))" +
//                    "}";
//
//            try (QueryExecution qe = qef.createQueryExecution(queryString)) {
//                qe.execSelect().forEachRemaining(qs -> {
//
//                    Resource s = qs.get("s").asResource();
//                    Resource p = qs.get("p").asResource();
//                    RDFNode o = qs.get("o");
//
//                    model.add(s, ResourceFactory.createProperty(p.getURI()), o);
//
//                    // save the data in a file to read later
//                });
//            }
//        }

    }

    public static void analyzeProperty(String property) {

        OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_RULE_INF, ModelFactory.createDefaultModel());

        String ns = RDFTermUtils.getNs(property);

        SchemaSource schema = SchemaService.getSourceFromUri(ns);

        QueryExecutionFactory qef = new QueryExecutionFactoryModel(schema.getModel());

        String queryString = PrefixNSService.getSparqlPrefixDecl() +
                " ask { " +
                "<" + property + ">  rdfs:range rdfs:Literal ." +
                "}";


        try (QueryExecution qe = qef.createQueryExecution(queryString)) {
            boolean isSubClass = qe.execAsk();
            //System.out.println(isSubClass);
            System.out.println("range" + isSubClass);
        }

        queryString = PrefixNSService.getSparqlPrefixDecl() +
                " ask { " +
                "<" + property + ">  a owl:DataTypeProperty ." +
                "}";

        try (QueryExecution qe = qef.createQueryExecution(queryString)) {
            boolean isSubClass = qe.execAsk();
            //System.out.println(isSubClass);
            System.out.println("Datatype" + isSubClass);
        }

        queryString = PrefixNSService.getSparqlPrefixDecl() +
                " ask { " +
                "<" + property + ">  a owl:ObjectProperty ." +
                "}";

        try (QueryExecution qe = qef.createQueryExecution(queryString)) {
            boolean isSubClass = qe.execAsk();
            //System.out.println(isSubClass);
            System.out.println("ObjectProperty" + isSubClass);
        }

        queryString = PrefixNSService.getSparqlPrefixDecl() +
                " select ?range { " +
                "<" + property + ">  rdfs:range ?range ." +
                "}";

    }

}
