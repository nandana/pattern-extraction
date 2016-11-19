package es.upm.oeg.tools.rdfshapes.dbpstat;

import es.upm.oeg.tools.rdfshapes.stringsimilarity.Jaccard;
import es.upm.oeg.tools.rdfshapes.stringsimilarity.JaroWinkler;
import es.upm.oeg.tools.rdfshapes.stringsimilarity.NormalizedLevenshtein;
import org.elasticsearch.common.base.Joiner;

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


        List<String> allProps = Files.
                readAllLines(Paths.get("src/main/resources/dbpstat/prop-all.txt"),
                        Charset.defaultCharset());

        List<String> frProps = Files.
                readAllLines(Paths.get("src/main/resources/dbpstat/prop-nl.txt"),
                        Charset.defaultCharset());
        System.out.println("Size : " + frProps.size());

        Jaccard jaccard = new Jaccard();

        NormalizedLevenshtein levenshtein = new NormalizedLevenshtein();

        JaroWinkler jaroWinkler = new JaroWinkler();

        for (String frProp : frProps) {
            for (String prop : allProps) {
                System.out.println(Joiner.on(", ").join(tokenize(prop)));
                if (levenshtein.similarity(frProp, prop) > 0.9
                        & !frProp.equals(prop)) {
                    System.out.println("FR: " + frProp + ", " + prop);
                }
            }
        }

    }

    public static List<String> tokenize(String property) {
        String[] tokens = property.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])");
        return Arrays.asList(tokens);
    }

}
