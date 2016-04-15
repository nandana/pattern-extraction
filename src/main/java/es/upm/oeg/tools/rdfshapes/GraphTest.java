package es.upm.oeg.tools.rdfshapes;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.stream.AttributeSink;
import org.graphstream.stream.ElementSink;
import org.graphstream.stream.Sink;
import org.graphstream.stream.file.FileSource;
import org.graphstream.stream.file.FileSourceDOT;
import org.graphstream.ui.view.Viewer;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;

/**
 * Copyright 2014-2016 Ontology Engineering Group, Universidad Politécnica de Madrid, Spain
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @author Nandana Mihindukulasooriya
 * @since 1.0.0
 */
public class GraphTest {

    public static void main(String[] args) throws Exception {

        Graph graph = new SingleGraph("Tutorial 1");

        FileSource fs = new FileSourceDOT();

        fs.addSink(graph);

        try {
            fs.readAll("graph/dbo:country");
        } catch( IOException e) {
            //TODO handle exception
             throw e;
        } finally {
            fs.removeSink(graph);
        }

        graph.display();


//        Node node = graph.addNode("A" );
//        node.setAttribute("ui.label", "A");
//        graph.addNode("B" );
//        graph.addNode("C" );
//        graph.addEdge("AB", "A", "B");
//        graph.addEdge("BC", "B", "C");
//        graph.addEdge("CA", "C", "A");
//
//        Viewer viewer = graph.display();

    }

}
