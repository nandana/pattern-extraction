package es.upm.oeg.tools.rdfshapes.ssn;

import com.google.common.collect.ImmutableSet;
import com.hp.hpl.jena.query.ParameterizedSparqlString;
import com.hp.hpl.jena.rdf.model.RDFNode;
import es.upm.oeg.tools.rdfshapes.utils.IOUtils;
import es.upm.oeg.tools.rdfshapes.utils.SparqlUtils;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
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
public class LODStats {

    public static void main(String[] args) throws Exception {
        String lodStats = "http://stats.lod2.eu/sparql";

        List<String> propList = Files.
                readAllLines(Paths.get("src/main/resources/ssn/propertyList.txt"),
                        Charset.defaultCharset());
        String propertyQuery  = IOUtils.readFile("ssn/lodstat-prop.rq", Charset.defaultCharset());

        for (String prop : propList) {

            System.out.println(prop);

            ParameterizedSparqlString pss = new ParameterizedSparqlString();
            pss.setCommandText(propertyQuery);
            pss.setIri("p", prop);

            String queryString = pss.toString();
            //System.out.println(queryString);

            List<Map<String, RDFNode>> maps = SparqlUtils.executeQueryForList(queryString, lodStats, ImmutableSet.of("tcount"));

            for (Map<String, RDFNode> map : maps) {

                System.out.println(map.get("tcount").asLiteral());
            }

        }



    }
}
