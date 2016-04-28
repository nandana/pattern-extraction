package es.upm.oeg.tools.rdfshapes.extractors;

import com.hp.hpl.jena.rdf.model.RDFNode;
import es.upm.oeg.tools.rdfshapes.utils.SPARQLServices;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
public class PropertyListExtractor extends QueryBase {

    private static final String PROPERTY_LIST_QUERY_PATH = "src/main/resources/common/property-list.rq";
    private static final String PROPERTY_LIST_QUERY_STRING;
    public static final String PROPERTY_VAR = "p";


    static {
        try {
            PROPERTY_LIST_QUERY_STRING = readFile(PROPERTY_LIST_QUERY_PATH, Charset.defaultCharset());
        } catch (IOException ioe) {
            throw new IllegalStateException("Error loading the query :" + PROPERTY_LIST_QUERY_PATH);
        }
    }

    /***
     * Extract the distinct list of properties from a SPARQL endpoint
     * @param sparqlEndpoint The SPARQL endpoint
     * @return the set of properties
     */
    public static Set<String> getAllProperties(String sparqlEndpoint) {

        Set<String> propertyList = new HashSet<>();
        List<RDFNode> results = executeQueryForList(PROPERTY_LIST_QUERY_STRING, sparqlEndpoint, PROPERTY_VAR);

        for (RDFNode node : results) {
            if(node.isURIResource()) {
                propertyList.add(node.asResource().getURI());
            }
        }

        return propertyList;
    }

    /***
     * Write the property list of the default graph of a given SPARQL query to a file.
     * @param sparqlEndpoint The SPARQL service endpoint to extract the
     * @param filePath path of the file where the output will be writtens
     */
    public static void exportPropertyList(String sparqlEndpoint, String filePath) throws IOException {

        Path path = FileSystems.getDefault().getPath(filePath);
        BufferedWriter writer =
                Files.newBufferedWriter( path, Charset.defaultCharset(),
                        StandardOpenOption.CREATE);


        Set<String> allProperties = getAllProperties(sparqlEndpoint);

        for (String property : allProperties) {
            writer.write(property);
            writer.newLine();
        }

        writer.flush();
        writer.close();

    }

    public static void main(String[] args) throws Exception {

        exportPropertyList(SPARQLServices.THREE_CIXTY, "src/main/resources/3cixty/properties.txt");

    }


}
