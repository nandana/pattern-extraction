package es.upm.oeg.tools.rdfshapes.graph;

import com.hp.hpl.jena.query.ParameterizedSparqlString;
import es.upm.oeg.tools.rdfshapes.utils.IOUtils;
import es.upm.oeg.tools.rdfshapes.utils.SparqlUtils;

import java.io.BufferedWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

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
public class ConnectedProperties {

    public static void main(String[] args) throws Exception {

//        BufferedWriter writer =
//                Files.newBufferedWriter(
//                        Paths.get("src/main/resources/graphs/connected-props-results.txt"),
//                        Charset.forName("UTF-8"),
//                        StandardOpenOption.CREATE);

        List<String> propList = Files.
                readAllLines(Paths.get("src/main/resources/graphs/prop-sub.txt"),
                        Charset.defaultCharset());


        System.out.println("propertyA|propertyB|c1|c2|t1|t2");

        final ExecutorService executor = Executors.newFixedThreadPool(5);

        for (int i = 0; i < propList.size(); i++) {
            for (int j = i ; j < propList.size(); j++) {

                executor.submit(new ConnectedPropertyQuery(propList.get(i), propList.get(j)));

            }

        }

    }



}
