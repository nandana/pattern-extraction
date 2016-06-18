package es.upm.oeg.tools.rdfshapes.libdemo;

//import edu.upc.freeling.*;

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
/*
public class FeelingDemo {

    // Modify this line to be your FreeLing installation directory
    private static final String FREELINGDIR = "/usr/local";
    private static final String DATA = FREELINGDIR + "/share/freeling/";
    private static final String LANG = "en";

    private static Tokenizer tk;
    private static Splitter sp;
    private static Nec neclass;
    private static HmmTagger tg;
    static Tagset ts;
    static Maco mf;

    public static void main(String[] args) {
        //export LD_LIBRARY_PATH=/home/nandana/tools/freeling-3.1/APIs/java/libfreeling_javaAPI.so:$LD_LIBRARY_PATH

        System.loadLibrary( "freeling_javaAPI" );

        Util.initLocale("default");

        // Create options set for maco analyzer.
        // Default values are Ok, except for data files.
        MacoOptions op = new MacoOptions( LANG );

        op.setActiveModules(false, true, true, true,
                true, true, true,
                true, true, true);

        op.setDataFiles(
                "",
                DATA+LANG+"/locucions.dat",
                DATA + LANG + "/quantities.dat",
                DATA + LANG + "/afixos.dat",
                DATA + LANG + "/probabilitats.dat",
                DATA + LANG + "/dicc.src",
                DATA + LANG + "/np.dat",
                DATA + "common/punct.dat");

        tk = new Tokenizer( DATA + LANG + "/tokenizer.dat" );
        sp = new Splitter( DATA + LANG + "/splitter.dat" );
        neclass = new Nec( DATA + LANG + "/nerc/nec/nec-ab-poor1.dat" );
        tg = new HmmTagger( DATA + LANG + "/tagger.dat", true, 2 );
        ts = new Tagset(DATA + LANG+"/tagset.dat");

        //morphological analyzer
        mf = new Maco( op );

        // Extract the tokens from the line of text.
        ListWord wordList = tk.tokenize("dogs");
        Sentence sentence = new Sentence(wordList);

        //morphological analysis
        mf.analyze(sentence);

        // Perform part-of-speech tagging.
        tg.analyze( sentence );

        // Perform named entity (NE) classificiation.
        neclass.analyze( sentence );

        VectorWord words = sentence.getWords();

        //We only have single word always
        Word word0 = words.get(0);

        System.out.println(word0.getLemma());


    }


}
*/
