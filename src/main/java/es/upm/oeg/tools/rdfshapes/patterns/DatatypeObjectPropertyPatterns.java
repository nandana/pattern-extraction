package es.upm.oeg.tools.rdfshapes.patterns;

import com.google.common.collect.ImmutableMap;
import com.hp.hpl.jena.rdf.model.RDFNode;
import es.upm.oeg.tools.rdfshapes.extractors.QueryBase;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.HashMap;
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
public class DatatypeObjectPropertyPatterns extends QueryBase {

    private static String classPropertyQueryPath = "src/main/resources/3cixty/class-properties.sparql";
    private static String propertyCardinalityQueryPath = "src/main/resources/3cixty/cardinality.sparql";
    private static String individualCountQueryPath = "src/main/resources/3cixty/instance-count.sparql";
    private static String objectCountQueryPath = "src/main/resources/query/countObjectsWithProperty.rq";
    private static String tripleCountQueryPath = "src/main/resources/query/countTriplesWithProperty.rq";
    private static String literalCountQueryPath = "src/main/resources/query/literalCount.rq";
    private static String blankCountQueryPath = "src/main/resources/query/blankCount.rq";
    private static String iriCountQueryPath = "src/main/resources/query/iriCount.rq";
    private static String classListPath = "src/main/resources/bne/classlist.txt";

    public static void main(String[] args) throws Exception {

        String endpoint = "http://infra2.dia.fi.upm.es:8899/sparql";

        List<String> classList = Files.
                readAllLines(Paths.get(classListPath),
                        Charset.defaultCharset());

        String classPropertyQueryString = readFile(classPropertyQueryPath, Charset.defaultCharset());
        String propertyCardinalityQueryString = readFile(propertyCardinalityQueryPath, Charset.defaultCharset());
        String individualCountQueryString = readFile(individualCountQueryPath, Charset.defaultCharset());
        String objectCountQueryString = readFile(objectCountQueryPath, Charset.defaultCharset());
        String tripleCountQueryString = readFile(tripleCountQueryPath, Charset.defaultCharset());
        String literalCountQueryString = readFile(literalCountQueryPath, Charset.defaultCharset());
        String blankCountQueryString = readFile(blankCountQueryPath, Charset.defaultCharset());
        String iriCountQueryString = readFile(iriCountQueryPath, Charset.defaultCharset());

        for (String clazz : classList) {

            Map<String, String> litMap = new HashMap<>();
            Map<String, String> iriMap = ImmutableMap.of("class", clazz);
            String queryString = bindQueryString(classPropertyQueryString, ImmutableMap.of(IRI_BINDINGS, iriMap, LITERAL_BINDINGS, litMap));
            List<RDFNode> nodeList = executeQueryForList(queryString, endpoint, "p");

            System.out.println("***");
            System.out.println("### **" + clazz + "**");
            System.out.println("***");
            System.out.println();

            for (RDFNode property : nodeList) {
                if (property.isURIResource()) {

                    int tripleCount;
                    int objectCount;
                    int literalCount;
                    int blankCount;
                    int iriCount;

                    String propertyURI = property.asResource().getURI();

                    litMap = new HashMap<>();
                    iriMap = ImmutableMap.of("class", clazz, "p", propertyURI);

                    queryString = bindQueryString(tripleCountQueryString, ImmutableMap.of(IRI_BINDINGS, iriMap, LITERAL_BINDINGS, litMap));

                    List<RDFNode> c = executeQueryForList(queryString, endpoint, "c");
                    if (c.size() > 0) {
                        tripleCount = c.get(0).asLiteral().getInt();
                    } else {
                        tripleCount = 0;
                    }

                    queryString = bindQueryString(objectCountQueryString, ImmutableMap.of(IRI_BINDINGS, iriMap, LITERAL_BINDINGS, litMap));

                    c = executeQueryForList(queryString, endpoint, "c");
                    if (c.size() > 0) {
                        objectCount = c.get(0).asLiteral().getInt();
                    } else {
                        objectCount = 0;
                    }

                    queryString = bindQueryString(literalCountQueryString, ImmutableMap.of(IRI_BINDINGS, iriMap, LITERAL_BINDINGS, litMap));
                    c = executeQueryForList(queryString, endpoint, "c");
                    if (c.size() > 0) {
                        literalCount = c.get(0).asLiteral().getInt();
                    } else {
                        literalCount = 0;
                    }

                    queryString = bindQueryString(blankCountQueryString, ImmutableMap.of(IRI_BINDINGS, iriMap, LITERAL_BINDINGS, litMap));
                    c = executeQueryForList(queryString, endpoint, "c");
                    if (c.size() > 0) {
                        blankCount = c.get(0).asLiteral().getInt();
                    } else {
                        blankCount = 0;
                    }

                    queryString = bindQueryString(iriCountQueryString, ImmutableMap.of(IRI_BINDINGS, iriMap, LITERAL_BINDINGS, litMap));
                    c = executeQueryForList(queryString, endpoint, "c");
                    if (c.size() > 0) {
                        iriCount = c.get(0).asLiteral().getInt();
                    } else {
                        iriCount = 0;
                    }

                    System.out.println("* " + propertyURI);
                    System.out.println();

                    System.out.println("| Object Count | Unique Objects | Literals | IRIs | Blank Nodes | ");
                    System.out.println("|---|---|---|---|---|");
                    System.out.println(String.format("|%d|%d (%.2f%%) |%d (%.2f%%)|%d (%.2f%%)|%d (%.2f%%)|",
                            tripleCount,
                            objectCount, ((((double) objectCount)/tripleCount)*100),
                            literalCount, ((((double) literalCount)/objectCount)*100),
                            iriCount, ((((double) iriCount)/objectCount)*100),
                            blankCount, ((((double) blankCount)/objectCount)*100)));
                    System.out.println();
                }
            }
        }

    }

}
