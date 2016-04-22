package es.upm.oeg.tools.rdfshapes.libdemo;

import net.sf.extjwnl.data.IndexWord;
import net.sf.extjwnl.data.POS;
import net.sf.extjwnl.data.Synset;
import net.sf.extjwnl.data.Word;
import net.sf.extjwnl.dictionary.Dictionary;

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
public class WordNetApiDemo {

    public static void main(String[] args) throws Exception {

        //Load the dictionary
        Dictionary dictionary = Dictionary.getDefaultResourceInstance();

        //Load the list of DBO properties
        List<String> dboPropList = Files.
                readAllLines(Paths.get("/home/nandana/data/dbpedia/2015-04/en/dbo-props.csv"),
                        Charset.defaultCharset());
        dboPropList.remove(0);
        System.out.println(String.format("%d DBpedia ontology properties loaded ...", dboPropList.size()));

        List<String> dbpPropList = Files.
                readAllLines(Paths.get("/home/nandana/data/dbpedia/2015-04/en/dbp-props.csv"),
                        Charset.defaultCharset());
        dbpPropList.remove(0);
        System.out.println(String.format("%d DBpedia ontology properties loaded ...", dbpPropList.size()));

        Map<String, Integer> dboMap = new HashMap<>();
        for (String dboProp : dboPropList) {
            String[] strings = dboProp.split("\\|");
            dboMap.put(strings[0].trim(), Integer.parseInt(strings[1].trim()));
        }

        Map<String, Integer> dbpMap = new HashMap<>();
        for (String dbpProp : dbpPropList) {
            String[] strings = dbpProp.split("\\|");
            dbpMap.put(strings[0].trim(), Integer.parseInt(strings[1].trim()));
        }

        for (String dboProperty : dboMap.keySet()) {

            IndexWord word = dictionary.getIndexWord(POS.NOUN, dboProperty);
            if (word != null) {
                List<Synset> senses = word.getSenses();
                for (Synset synset : senses) {
                    for (Word synWord : synset.getWords()) {
                        if (!dboProperty.equals(synWord.getLemma()) && dbpMap.containsKey(synWord.getLemma())) {
                            System.out.println(String.format("%-15s | %8d | %-15s | %8d| %-100s", dboProperty, dboMap.get(dboProperty),  synWord.getLemma(), dbpMap.get(synWord.getLemma()), synset.getGloss()));
                        }
                    }
                }

            }
        }

    }

}
