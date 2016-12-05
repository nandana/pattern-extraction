package es.upm.oeg.tools.rdfshapes.dbpstat;

import com.google.common.base.Joiner;
import es.upm.oeg.tools.rdfshapes.stringsimilarity.Jaccard;
import es.upm.oeg.tools.rdfshapes.stringsimilarity.JaroWinkler;
import es.upm.oeg.tools.rdfshapes.stringsimilarity.NormalizedLevenshtein;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

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
public class PropSimilarity {

    public static void main(String[] args) throws Exception {


//        List<String> allProps = Files.
//                readAllLines(Paths.get("src/main/resources/dbpstat/prop-all.txt"),
//                        Charset.defaultCharset());
//
//        List<String> frProps = Files.
//                readAllLines(Paths.get("src/main/resources/dbpstat/prop-nl.txt"),
//                        Charset.defaultCharset());

        List<String> rdfProps = Files.
                readAllLines(Paths.get("src/main/resources/caceres/rdf.txt"),
                        Charset.defaultCharset());

        List<String> tripadvisorProps = Files.
                readAllLines(Paths.get("src/main/resources/caceres/tenedor.txt"),
                        Charset.defaultCharset());


        Jaccard jaccard = new Jaccard();

        NormalizedLevenshtein levenshtein = new NormalizedLevenshtein();

        JaroWinkler jaroWinkler = new JaroWinkler();

        for (String rdfProp : rdfProps) {
            for (String tripAdvisorProp : tripadvisorProps) {

                String tripAdvisor = tripAdvisorProp;
                String rdf = rdfProp;

                tripAdvisorProp = tripAdvisorProp.toLowerCase().replace("restaurante", "").replace("tapería", "");
                rdfProp = rdfProp.toLowerCase().replace("restaurante", "").replace("tapería", "");


                if (jaroWinkler.similarity(tripAdvisorProp, rdfProp) > 0.9) {
                    System.out.println(rdf + "," + tripAdvisor);
                }
            }
        }

    }

    public static List<String> tokenize(String property) {
        String[] tokens = property.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])");
        return Arrays.asList(tokens);
    }

}
