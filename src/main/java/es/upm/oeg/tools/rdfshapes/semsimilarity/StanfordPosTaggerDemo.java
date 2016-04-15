package es.upm.oeg.tools.rdfshapes.semsimilarity;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

import java.io.StringReader;
import java.util.List;

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
public class StanfordPosTaggerDemo {

    public static void main(String[] args) {

        MaxentTagger tagger = new MaxentTagger("/home/nandana/tools/stanford-postagger-full-2015-12-09/models/english-left3words-distsim.tagger");
        StringReader reader = new StringReader("most Famous And The Members InP hoto");
        List<List<HasWord>> sentences = MaxentTagger.tokenizeText(reader);

        for (List<HasWord> sentence : sentences) {
            List<TaggedWord> tSentence = tagger.tagSentence(sentence);
            for (TaggedWord word : tSentence) {
                System.out.println(word);
                System.out.print(word.value());
                System.out.println(word.tag());
            }

        }

    }

}
