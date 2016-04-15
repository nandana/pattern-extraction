package es.upm.oeg.tools.rdfshapes;

import es.upm.oeg.tools.rdfshapes.utils.RDFTermUtils;
import org.elasticsearch.common.base.Joiner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
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
public class AruleInputGenerator {

    private static final Logger logger = LoggerFactory.getLogger(AruleInputGenerator.class);

    public static void main(String[] args) throws Exception {

        String arulesPath = "src/main/resources/arules/SoccerPlayer-country.csv";
        BufferedWriter writer =
                Files.newBufferedWriter(
                        Paths.get(arulesPath),
                        Charset.forName("UTF-8"),
                        StandardOpenOption.CREATE);

        String sparqlService = "http://es.dbpedia.org/sparql";
        PropertyDomainExtractor domainExtractor = new PropertyDomainExtractor(sparqlService);

        String clazz = "http://dbpedia.org/ontology/SoccerPlayer";
        String property = "http://dbpedia.org/ontology/country";

        List<String> domainList = domainExtractor.extractDomainClassesList(clazz, property);
        domainList.remove("http://www.w3.org/2002/07/owl#Thing");

        List<Count> objectCount = domainExtractor.extractPropertyObjects(clazz, property);

        List<String> prefixedDomainList = new ArrayList<>();
        for (String domainClass : domainList) {
            prefixedDomainList.add(RDFTermUtils.getPrefixedTerm(domainClass));
        }

        StringBuffer csvHeader = new StringBuffer("objectUri,");
        csvHeader.append(Joiner.on(",").join(prefixedDomainList));

        logger.info("Header : " + csvHeader.toString());

        //Write the header of the CSV file
        writer.write(csvHeader.toString());
        writer.newLine();

        int i = 0;
        for (Count object : objectCount) {
            writer.write(""+ i++);
            for (String domainClass : domainList) {
                writer.write(",");
                if (domainExtractor.isTypeOf(object.getSubject(), domainClass))  {
                    writer.write("1");
                } else  {
                    writer.write("0");
                }
            }
            writer.newLine();
        }
        writer.flush();

    }

}
