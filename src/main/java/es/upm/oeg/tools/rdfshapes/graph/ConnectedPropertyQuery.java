package es.upm.oeg.tools.rdfshapes.graph;

import com.hp.hpl.jena.query.ParameterizedSparqlString;
import es.upm.oeg.tools.rdfshapes.utils.IOUtils;
import es.upm.oeg.tools.rdfshapes.utils.SparqlUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.time.Duration;
import java.time.Instant;

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
public class ConnectedPropertyQuery implements Runnable{

    private String propertyA;

    private String propertyB;

    private static String connectedPropsQuery;

    private static String endpoint = "http://4v.dia.fi.upm.es:10043/sparql";

    static {
        try {
            connectedPropsQuery = IOUtils.readFile("graphs/conct-props.rq", Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public ConnectedPropertyQuery(String propertyA, String propertyB) {
        this.propertyA = propertyA;
        this.propertyB = propertyB;
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {

        ParameterizedSparqlString pss = new ParameterizedSparqlString();
        pss.setCommandText(connectedPropsQuery);
        pss.setIri("p1", propertyA);
        pss.setIri("p2", propertyB);

        String queryString = pss.toString();

        Instant start = Instant.now();
        long c1 = SparqlUtils.executeQueryForLong(queryString, endpoint, "c");
        Instant end = Instant.now();

        Duration timeElapsed = Duration.between(start, end);
        long t1 = timeElapsed.toMillis();

        long c2;
        long t2;

        if (propertyA.equals(propertyB)) {

            c2 = c1;
            t2 = t1;

        } else {

            pss = new ParameterizedSparqlString();
            pss.setCommandText(connectedPropsQuery);
            pss.setIri("p1", propertyB);
            pss.setIri("p2", propertyA);

            queryString = pss.toString();

            start = Instant.now();
            c2 = SparqlUtils.executeQueryForLong(queryString, endpoint, "c");
            end = Instant.now();

            timeElapsed = Duration.between(start, end);
            t2 = timeElapsed.toMillis();

        }

        if (c1 != 0 || c2 != 0) {
            System.out.println(propertyA + " |" +  propertyB + " |" + c1 + " |" + c2 + " |" + t1 + " |" + t2);
            //writer.newLine();
        }

    }

}
