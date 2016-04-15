package es.upm.oeg.tools.rdfshapes.patterns;

import com.google.common.collect.ImmutableSet;
import es.upm.oeg.tools.rdfshapes.Count;
import es.upm.oeg.tools.rdfshapes.extractors.ClassListExtractor;
import es.upm.oeg.tools.rdfshapes.extractors.PropertyInfoExtractor;
import es.upm.oeg.tools.rdfshapes.utils.RDFTermUtils;

import java.util.ArrayList;
import java.util.Set;

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
public class DomainRangePatterns {

    private static Set<String> exclusions = ImmutableSet.of("http://www.w3.org/1999/02/22-rdf-syntax-ns#type", "http://www.w3.org/2002/07/owl#sameAs");

    public static void main(String[] args) {

        String sparqlEndpoint = "http://3cixty.eurecom.fr/sparql";

        ArrayList<Count> propertyCounts = PropertyInfoExtractor.extractProperties(sparqlEndpoint);

        for (Count propertyCount : propertyCounts) {

            String property = propertyCount.getSubject();
            // We exclude some properties
            if(exclusions.contains(property)){
                continue;
            }

            System.out.println("### " + RDFTermUtils.getPrefixedTerm(property));

            ArrayList<Count> domainClassCounts = PropertyInfoExtractor.extractDomainClasses(property, sparqlEndpoint);

            //TODO this sucks! Find a better way to serialize Markdown better

            System.out.print("|Domain Class|");
            for (Count count : domainClassCounts) {
                System.out.print(RDFTermUtils.getPrefixedTerm(count.getSubject()) +"|");
                //System.out.print(count.getSubject() +"|");
            }
            System.out.println();

            for (int i = 0; i <= domainClassCounts.size() ; i++) {
                System.out.print("|---");
            }
            System.out.println("|");


            System.out.print("|Count|");
            for (Count count : domainClassCounts) {
                System.out.print(count.getCount() +"|");
            }
            System.out.println();

            break;

        }


    }


}
