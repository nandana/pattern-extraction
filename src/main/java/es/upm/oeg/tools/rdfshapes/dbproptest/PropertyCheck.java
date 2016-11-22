package es.upm.oeg.tools.rdfshapes.dbproptest;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
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
public class PropertyCheck {

    public static void main(String[] args) throws Exception {

        Map<String,Integer> v201604 = new HashMap<>();

        List<String> propList = Files.
                readAllLines(Paths.get("src/main/resources/dbproptest/results/data.csv"),
                        Charset.defaultCharset());

        List<String> v201604List = Files.
                readAllLines(Paths.get("src/main/resources/owled/pv35.txt"),
                        Charset.defaultCharset());

        for (String prop : propList) {
            String[] split = prop.split(",");
            v201604.put(split[1], Integer.parseInt(split[5]));
        }

        for (String prop : v201604List) {
            int count = v201604.get(prop);
            if (count != 0) {
                System.out.println(prop);
            }
        }

    }


}
