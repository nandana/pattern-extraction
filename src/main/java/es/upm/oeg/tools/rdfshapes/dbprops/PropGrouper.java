package es.upm.oeg.tools.rdfshapes.dbprops;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import org.elasticsearch.common.base.Joiner;
import org.languagetool.JLanguageTool;
import org.languagetool.language.BritishEnglish;
import org.languagetool.rules.RuleMatch;

import java.io.BufferedWriter;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
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
public class PropGrouper {

    private static MaxentTagger tagger = new MaxentTagger("/home/nandana/tools/stanford-postagger-full-2015-12-09/models/english-left3words-distsim.tagger");

    public static void main(String[] args) throws Exception {

        BufferedWriter writer =
                Files.newBufferedWriter(
                        Paths.get("/home/nandana/data/dbpedia/2015-04/en/prop-analysis.csv"),
                        Charset.forName("UTF-8"),
                        StandardOpenOption.CREATE);

        writer.write(String.format("%-20s|%-2s|%2s|%2s|%2s|%2s|%2s|%2s|%2s|%2s|%2s|%-25s|%-40s|%-20s|%-20s", "DBP", "LC" , "UC", "CC","W", "Sp", "TC", "NC", "VC", "AC", "DC"
                , "Words", "Tagged", "Typos", "Suggestions"));
        writer.newLine();

        JLanguageTool langTool = new JLanguageTool(new BritishEnglish());

        List<String> dbpPropList = Files.
                readAllLines(Paths.get("/home/nandana/data/dbpedia/2015-04/en/dbp-props.csv"),
                        Charset.defaultCharset());
        dbpPropList.remove(0);
        System.out.println(String.format("%d DBpedia ontology properties loaded ...", dbpPropList.size()));

        Map<String, Integer> dbpMap = new HashMap<>();
        for (String dbpProp : dbpPropList) {
            String[] strings = dbpProp.split("\\|");
            dbpMap.put(strings[0].trim(), Integer.parseInt(strings[1].trim()));
        }

        int allSimple = 0;
        int allCapital = 0;
        int camelCase = 0;
        int foundInDict = 0;

        for (String property : dbpMap.keySet()) {

            // Replace all numerics with empty strings
            property = property.replaceAll("\\d+.*", "");
            if("".equals(property)){
                continue;
            }

            //all simple letters
            if (property.toLowerCase().equals(property)) {
                allSimple++;
                List<RuleMatch> matches = langTool.check(property);
                boolean typoFound = false;

                // Iterate through all the rules given by langTools
                //TODO if we remove all the irrelevant rules, this process can be optimized
                for (RuleMatch match : matches) {
                    //We only check if the langtool found a typo
                    if(match.getRule().getId().equals("MORFOLOGIK_RULE_EN_GB")) {
                        typoFound = true;
                        boolean isComposite = false;

                        //we check for special case of all simple composite words- e.g., activitytype -> activity type
                        for (String replacement : match.getSuggestedReplacements()) {
                            //replace all spaces with empty string
                            String composite = replacement.replaceAll(" ", "");
                            if (property.equals(composite)) {
                                isComposite = true;

                                Map<String, String> posTagging = posTagging(replacement);

                                String[] words = replacement.split("\\s+");
                                writer.write(String.format("%-20s|%-2d|%2d|%2d|%2d|%2d|%2d|%2s|%2s|%2s|%2s|%-25s|%-40s|%-20s|%-20s", property, 1 , 0, 0, words.length, 0, 0,
                                        posTagging.get("noun"), posTagging.get("verb"), posTagging.get("adjective"), posTagging.get("adverb")
                                        , property, posTagging.get("tagged") ,"", Joiner.on(",").join(match.getSuggestedReplacements())));
                                writer.newLine();
                                break;
                            }
                        }

                        // This is not the special case
                        if (!isComposite) {
                            Map<String, String> posTagging = posTagging(property);
                            writer.write(String.format("%-20s|%-2d|%2d|%2d|%2d|%2d|%2d|%2s|%2s|%2s|%2s|%-25s|%-40s|%-20s|%-20s", property, 1 , 0, 0, 1, 1, 1,
                                    posTagging.get("noun"), posTagging.get("verb"), posTagging.get("adjective"), posTagging.get("adverb")
                                    , property, posTagging.get("tagged") ,property, Joiner.on(",").join(match.getSuggestedReplacements())));
                            writer.newLine();
                        }
                    }
                }
                if (!typoFound) {
                    Map<String, String> posTagging = posTagging(property);
                    writer.write(String.format("%-20s|%-2d|%2d|%2d|%2d|%2d|%2d|%2s|%2s|%2s|%2s|%-25s|%-40s|%-20s|%-20s", property, 1 , 0, 0, 1, 0, 0,
                            posTagging.get("noun"), posTagging.get("verb"), posTagging.get("adjective"), posTagging.get("adverb")
                            , property, posTagging.get("tagged"),"", ""));
                    writer.newLine();
                }


            } else if (property.toUpperCase().equals(property)) {
                allCapital++;
                List<RuleMatch> matches = langTool.check(property);
                boolean typoFound = false;
                for (RuleMatch match : matches) {
                    if(match.getRule().getId().equals("MORFOLOGIK_RULE_EN_GB")) {

                        Map<String, String> posTagging = posTagging(property);

                        writer.write(String.format("%-20s|%-2d|%2d|%2d|%2d|%2d|%2d|%2s|%2s|%2s|%2s|%-25s|%-40s|%-20s|%-20s", property, 0 , 1, 0, 1, 1, 1,
                                posTagging.get("noun"), posTagging.get("verb"), posTagging.get("adjective"), posTagging.get("adverb")
                                , property, posTagging.get("tagged"), property, Joiner.on(",").join(match.getSuggestedReplacements())));
                        writer.newLine();
                        typoFound = true;
                    }
                }
                if (!typoFound) {
                    Map<String, String> posTagging = posTagging(property);
                    writer.write(String.format("%-20s|%-2d|%2d|%2d|%2d|%2d|%2d|%2s|%2s|%2s|%2s|%-25s|%-40s|%-20s|%-20s", property, 0 , 1, 0, 1, 0, 0,
                            posTagging.get("noun"), posTagging.get("verb"), posTagging.get("adjective"), posTagging.get("adverb")
                            , property, posTagging.get("tagged"),  "", ""));
                    writer.newLine();
                }

            //Camel case
            } else {
                camelCase++;
                List<String> wordList = new ArrayList<>();
                List<String> typos = new ArrayList<>();
                List<String> suggestions = new ArrayList<>();

                for (String w : property.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])")) {
                    wordList.add(w);
                }

                for (String word : wordList) {
                    List<RuleMatch> matches = langTool.check(word);
                    for (RuleMatch match : matches) {
                        if(match.getRule().getId().equals("MORFOLOGIK_RULE_EN_GB")) {
                            typos.add(word);
                            suggestions.addAll(match.getSuggestedReplacements());
                        }
                    }
                }

                Map<String, String> posTagging = posTagging(Joiner.on(" ").join(wordList));

                if (typos.size() > 0) {
                    writer.write(String.format("%-20s|%-2d|%2d|%2d|%2d|%2d|%2d|%2s|%2s|%2s|%2s|%-25s|%-40s|%-20s|%-20s", property, 0 , 0, 1, wordList.size(), 1, typos.size(),
                            posTagging.get("noun"), posTagging.get("verb"), posTagging.get("adjective"), posTagging.get("adverb")
                            , Joiner.on(",").join(wordList), posTagging.get("tagged"), Joiner.on(",").join(typos) ,Joiner.on(",").join(suggestions)));
                    writer.newLine();
                } else {
                    writer.write(String.format("%-20s|%-2d|%2d|%2d|%2d|%2d|%2d|%2s|%2s|%2s|%2s|%-25s|%-40s|%-20s|%-20s", property, 0 , 0, 1, wordList.size(), 0, 0,
                            posTagging.get("noun"), posTagging.get("verb"), posTagging.get("adjective"), posTagging.get("adverb")
                            , Joiner.on(",").join(wordList), posTagging.get("tagged"), "" ,""));
                    writer.newLine();
                }
            }
        }

        System.out.println("All simple " + allSimple);
        System.out.println("All capital " + allCapital);
        System.out.println("Camel case " + camelCase);


    }

    private static Map<String, String> posTagging(String text) {

        Map<String, String> results = new HashMap<>();

        int nounCount = 0;
        int verbCount = 0;
        int adjectiveCount = 0;
        int adverbCount = 0;
        List<String> taggedWords = new ArrayList<>();

        StringReader reader = new StringReader(text);
        List<List<HasWord>> sentences = MaxentTagger.tokenizeText(reader);

        for (List<HasWord> sentence : sentences) {
            List<TaggedWord> tSentence = tagger.tagSentence(sentence);
            for (TaggedWord word : tSentence) {
                taggedWords.add(word.toString());
                String tag = word.tag();
                switch (tag) {
                    case "NN":
                        nounCount++;
                        break;
                    case "NNS":
                        nounCount++;
                        break;
                    case "NNP":
                        nounCount++;
                        break;
                    case "NNPS":
                        nounCount++;
                        break;
                    case "VB":
                        verbCount++;
                        break;
                    case "VBD":
                        verbCount++;
                        break;
                    case "VBG":
                        verbCount++;
                        break;
                    case "VBN":
                        verbCount++;
                        break;
                    case "VBP":
                        verbCount++;
                        break;
                    case "VBZ":
                        verbCount++;
                        break;
                    case "JJ":
                        adjectiveCount++;
                        break;
                    case "JJR":
                        adjectiveCount++;
                        break;
                    case "JJS":
                        adjectiveCount++;
                        break;
                    case "RB":
                        adverbCount++;
                        break;
                    case "RBR":
                        adverbCount++;
                        break;
                    case "RBS":
                        adverbCount++;
                        break;
                    default:
                        break;
                }
            }
        }

        results.put("noun", String.valueOf(nounCount));
        results.put("verb", String.valueOf(verbCount));
        results.put("adjective", String.valueOf(adjectiveCount));
        results.put("adverb", String.valueOf(adverbCount));
        results.put("tagged", Joiner.on(" ").join(taggedWords));

        return results;
    }


}
