package es.upm.oeg.tools.rdfshapes.libdemo;

import org.languagetool.JLanguageTool;
import org.languagetool.language.BritishEnglish;
import org.languagetool.rules.RuleMatch;

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
public class LanguageToolDemo {

    public static void main(String[] args) throws Exception {

        JLanguageTool langTool = new JLanguageTool(new BritishEnglish());

        List<RuleMatch> matches = langTool.check("dat");

        for (RuleMatch match : matches) {
            System.out.println(match.getRule().getId());

            System.out.println("Potential typo at line " +
                    match.getLine() + ", column " +
                    match.getColumn() + ": " + match.getMessage());
            System.out.println("Suggested correction(s): " +
                    match.getSuggestedReplacements());
        }


    }

}
