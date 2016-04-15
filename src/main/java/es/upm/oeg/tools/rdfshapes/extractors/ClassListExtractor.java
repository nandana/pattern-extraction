package es.upm.oeg.tools.rdfshapes.extractors;

import com.google.common.collect.Sets;
import com.hp.hpl.jena.rdf.model.RDFNode;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static es.upm.oeg.tools.rdfshapes.utils.IOUtils.readFile;
import static es.upm.oeg.tools.rdfshapes.utils.SparqlUtils.executeQueryForList;

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
public class ClassListExtractor extends QueryBase {

    private static final String ORDERED_CLASS_LIST_QUERY_PATH = "src/main/resources/classListOrdered.rq";
    private static final String ORDERED_CLASS_LIST_QUERY_STRING;

    public static final String CLASS_VAR = "class";
    public static final String COUNT_VAR = "count";


    static {
        try {
            ORDERED_CLASS_LIST_QUERY_STRING = readFile(ORDERED_CLASS_LIST_QUERY_PATH, Charset.defaultCharset());
        } catch (IOException ioe) {
            throw new IllegalStateException("Error loading the query :" + ORDERED_CLASS_LIST_QUERY_PATH);
        }
    }


    public static List<String> getOrderedClassList(String sparqlEndpoint) {

        List<String> classList = new ArrayList<>();
        List<RDFNode> results = executeQueryForList(ORDERED_CLASS_LIST_QUERY_STRING, sparqlEndpoint, CLASS_VAR);

        for (RDFNode node : results) {
            if(node.isURIResource()) {
                classList.add(node.asResource().getURI());
            }
        }

        return classList;
    }

    public static Set<String> getClassList(String sparqlEndpoint) {

        return null;
    }

}
