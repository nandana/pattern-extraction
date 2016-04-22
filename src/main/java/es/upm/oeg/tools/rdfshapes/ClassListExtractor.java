package es.upm.oeg.tools.rdfshapes;

import com.hp.hpl.jena.rdf.model.RDFNode;
import es.upm.oeg.tools.rdfshapes.extractors.QueryBase;
import org.elasticsearch.common.xcontent.XContentBuilder;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class ClassListExtractor extends QueryBase {

    private static String allClassesQueryPath = "src/main/resources/common/all-classes-ordered.sparql";

    public static void main(String[] args) throws Exception {


        Path path = FileSystems.getDefault().getPath("src/main/resources/bne/classlist.txt");
        BufferedWriter writer =
                Files.newBufferedWriter( path, Charset.defaultCharset(),
                        StandardOpenOption.CREATE);

        String endpoint = "http://infra2.dia.fi.upm.es:8899/sparql";

        String allClassesQuery = readFile(allClassesQueryPath, Charset.defaultCharset());

        List<RDFNode> nodeList = executeQueryForList(allClassesQuery, endpoint, "class");
        for (RDFNode clazzNode : nodeList) {
            if (clazzNode.isURIResource()) {
                writer.write(clazzNode.asResource().getURI());
                writer.newLine();
            }
        }

        writer.flush();
        writer.close();
    }


}
