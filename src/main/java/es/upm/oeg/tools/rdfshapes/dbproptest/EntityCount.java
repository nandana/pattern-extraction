package es.upm.oeg.tools.rdfshapes.dbproptest;

import com.google.common.collect.ImmutableList;
import com.hp.hpl.jena.query.ParameterizedSparqlString;
import es.upm.oeg.tools.rdfshapes.utils.IOUtils;
import es.upm.oeg.tools.rdfshapes.utils.SparqlUtils;

import java.nio.charset.Charset;
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
public class EntityCount {

    public static void main(String[] args) throws Exception {

        List<String[]> classes = ImmutableList.of(
                new String[]{"http://xmlns.com/foaf/0.1/Person", "foaf-person", "person"});
                //new String[]{"http://dbpedia.org/ontology/Place", "dbo-place", "place"});
                //new String[]{"http://dbpedia.org/ontology/Work", "dbo-work", "work"},
                //new String[]{"http://dbpedia.org/ontology/Species", "dbo-species", "species"},
                //new String[]{"http://dbpedia.org/ontology/Organisation", "dbo-organisation", "org"},
                //new String[]{"http://dbpedia.org/ontology/Animal", "dbo-animal", "animal"},
                //new String[]{"http://dbpedia.org/ontology/Film", "dbo-film", "film"},
                //new String[]{"http://dbpedia.org/ontology/Artist", "dbo-artist", "artist"},
                //new String[]{"http://dbpedia.org/ontology/Athlete", "dbo-athlete", "athlete"},
                //new String[]{"http://dbpedia.org/ontology/MusicalWork", "dbo-musicalWork", "musicalWork"});

        String endpoint = "http://4v.dia.fi.upm.es:10034/sparql";

        String entityCountQuery  = IOUtils.readFile("dbproptest/entity-count.rq", Charset.defaultCharset());

        for (String[] classData : classes) {

            ParameterizedSparqlString pss = new ParameterizedSparqlString();
            pss.setCommandText(entityCountQuery);
            pss.setIri("class", classData[0]);

            String queryString = pss.toString();

            long count = SparqlUtils.executeQueryForLong(queryString, endpoint, "c");

            System.out.println( classData[0] + " , " + count);

        }

    }
}
