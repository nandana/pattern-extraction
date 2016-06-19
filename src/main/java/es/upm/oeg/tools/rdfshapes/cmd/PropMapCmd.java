package es.upm.oeg.tools.rdfshapes.cmd;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

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
public class PropMapCmd {

    private static final Options OPTIONS = new Options();

    static {

        OPTIONS.addOption(Option.builder("g1")
                .longOpt("graph1")
                .desc("The iri of the first named graph")
                .hasArg()
                .argName("NAMED-GRAPH-1")
                .required()
                .build());

        OPTIONS.addOption(Option.builder("g2")
                .longOpt("graph2")
                .desc("The iri of the second named graph")
                .hasArg()
                .argName("NAMED-GRAPH-2")
                .required()
                .build());

        OPTIONS.addOption(Option.builder("tg1")
                .longOpt("templateGraph1")
                .desc("The iri of the first template named graph")
                .hasArg()
                .argName("TEMPLATE-NAMED-GRAPH-1")
                .required()
                .build());

        OPTIONS.addOption(Option.builder("tg2")
                .longOpt("templateGraph2")
                .desc("The iri of the second template named graph")
                .hasArg()
                .argName("TEMPLATE-NAMED-GRAPH-2")
                .required()
                .build());

        OPTIONS.addOption(Option.builder("o")
                .longOpt("output")
                .desc("Output file path")
                .hasArg()
                .argName("OUTPUT-FILE-PATH")
                .required()
                .build());

        OPTIONS.addOption(Option.builder("nt")
                .longOpt("numberOfThreads")
                .desc("Number of threads to be used")
                .hasArg()
                .argName("N-THREADS")
                .build());


    }

    public static void main(String[] args) {





    }


}
