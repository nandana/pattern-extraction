package es.upm.oeg.tools.rdfshapes.libdemo;

import edu.cmu.lti.lexical_db.ILexicalDatabase;
import edu.cmu.lti.lexical_db.NictWordNet;
import edu.cmu.lti.ws4j.RelatednessCalculator;
import edu.cmu.lti.ws4j.impl.*;
import edu.cmu.lti.ws4j.util.WS4JConfiguration;

import java.io.BufferedWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
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
public class SimilarityCalculationDemo {

    private static ILexicalDatabase db = new NictWordNet();
    private static RelatednessCalculator hirstStOnge = new HirstStOnge(db);
    private static RelatednessCalculator leacockChodorow = new LeacockChodorow(db);
    private static RelatednessCalculator lesk = new Lesk(db);
    private static RelatednessCalculator wuPalmer = new WuPalmer(db);
    private static RelatednessCalculator resnik = new Resnik(db);
    private static RelatednessCalculator jiangConrath = new JiangConrath(db);
    private static RelatednessCalculator lin = new Lin(db);
    private static RelatednessCalculator path = new Path(db);



    private static RelatednessCalculator[] rcs = {
            new HirstStOnge(db), new LeacockChodorow(db), new Lesk(db),  new WuPalmer(db),
            new Resnik(db), new JiangConrath(db), new Lin(db), new Path(db)
    };

    private static void run( String word1, String word2 ) {

        for ( RelatednessCalculator rc : rcs ) {
            double s = rc.calcRelatednessOfWords(word1, word2);
            System.out.println( rc.getClass().getName()+"\t"+s );
        }
    }

    public static void main(String[] args)  throws Exception {


        BufferedWriter writer =
                Files.newBufferedWriter(
                        Paths.get("/home/nandana/data/dbpedia/2015-04/en/semantic-similarity2.csv"),
                        Charset.forName("UTF-8"),
                        StandardOpenOption.CREATE);

        writer.write(String.format("%-15s|%-20s|%6s|%6s|%6s|%6s|%6s|%6s|%6s|%6s|%6s", "DBO", "DBP" , "Total", "Hirst.", "Leaco."
                                            , "Lesk", "WuPalm.", "Resnik", "Jiang.", "Lin", "Path"));
        writer.newLine();


        List<String> dboPropList = Files.
                readAllLines(Paths.get("/home/nandana/data/dbpedia/2015-04/en/dbo-props.csv"),
                        Charset.defaultCharset());
        dboPropList.remove(0);

        Map<String, Integer> dboMap = new HashMap<>();
        for (String dboProp : dboPropList) {
            String[] strings = dboProp.split("\\|");
            dboMap.put(strings[0].trim(), Integer.parseInt(strings[1].trim()));
        }

        List<String> dbpPropList = Files.
                readAllLines(Paths.get("/home/nandana/data/dbpedia/2015-04/en/dbp-props.csv"),
                        Charset.defaultCharset());
        dbpPropList.remove(0);

        Map<String, Integer> dbpMap = new HashMap<>();
        for (String dbpProp : dbpPropList) {
            String[] strings = dbpProp.split("\\|");
            dbpMap.put(strings[0].trim(), Integer.parseInt(strings[1].trim()));
        }


        int i = 0;
        for (String dboProp : dboMap.keySet()) {

            for (String dbpProp : dbpMap.keySet()) {

                String word1 = dboProp;
                String word2 = dbpProp;

                if (word1.equals(word2)) {
                    continue;
                }

                double sum = 0;

                WS4JConfiguration.getInstance().setMFS(true);
                double hirstStOngeValue = hirstStOnge.calcRelatednessOfWords(word1, word2);
                sum += hirstStOngeValue;

                double leacockChodorowValue = leacockChodorow.calcRelatednessOfWords(word1, word2);
                sum += leacockChodorowValue;

                double leskValue = lesk.calcRelatednessOfWords(word1, word2);
                sum += leskValue;

                double wuPalmerValue = wuPalmer.calcRelatednessOfWords(word1, word2);
                sum += wuPalmerValue;

                double resnikValue = resnik.calcRelatednessOfWords(word1, word2);
                sum += resnikValue;

                double jiangConrathValue = jiangConrath.calcRelatednessOfWords(word1, word2);
                sum += jiangConrathValue;

                double linValue = lin.calcRelatednessOfWords(word1, word2);
                sum += linValue;

                double pathValue = path.calcRelatednessOfWords(word1, word2);
                sum += pathValue;

                if (sum > 10) {
                    System.out.println(String.format("SemSimilirity(%s, %s) = %f, %f, %f, %f, %f, %f, %f, %f, %f ", word1, word2, sum,
                            hirstStOngeValue, leacockChodorowValue, leskValue, wuPalmerValue, resnikValue, jiangConrathValue, linValue, pathValue));
                    writer.write(String.format("%-15s|%20s|%6.4f|%6.4f|%6.4f|%6.4f|%6.4f|%6.4f|%6.4f|%6.4f|%6.4f", word1, word2 , sum, hirstStOngeValue, leacockChodorowValue
                            , leskValue, wuPalmerValue, resnikValue, jiangConrathValue, linValue, pathValue));
                    writer.newLine();
                }
            }


        }
        writer.flush();
        writer.close();

    }

    private static double normalize(double value, double min, double max) {

        System.out.println("max - min" + (max - min));
        System.out.println("value" + value);
        System.out.println( "normalized" + (value / (max - min)));

        return  (value / (max - min));

    }


}
