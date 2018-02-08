package es.upm.oeg.tools.rdfshapes.ssn;

import com.hp.hpl.jena.query.ParameterizedSparqlString;
import es.upm.oeg.tools.rdfshapes.utils.IOUtils;
import es.upm.oeg.tools.rdfshapes.utils.SparqlUtils;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * @author Nandana Mihindukulasooriya
 * @since 8/02/18.
 */
public class LLODInstances {

    private static final String D1 = "http://loupe.linkeddata.es/d10/";

    private static final String SPARQL = "http://4v.dia.fi.upm.es:14014/sparql";

    private static final String LEMON_MODEL_CLASSES = "src/main/resources/ssn/lemon-model-classlist.txt";

    private static final String ONTOLEX_CLASSES = "src/main/resources/ssn/ontolex-classlist.txt";



    public static void main(String[] args) throws Exception {

        String classQuery  = IOUtils.readFile("src/main/resources/ssn/class.rq", Charset.defaultCharset());

        List<String> classList = Files.
                readAllLines(Paths.get(ONTOLEX_CLASSES),
                        Charset.defaultCharset());

        for (String clazz : classList) {

            ParameterizedSparqlString pss = new ParameterizedSparqlString();
            pss.setCommandText(classQuery);
            pss.setIri("class", clazz);
            pss.setIri("graph", D1);

            String queryString = pss.toString();
            //System.out.println(queryString);

            long count = SparqlUtils.executeQueryForLong(queryString, SPARQL, "c");

            //System.out.println(clazz + " , " + count);
            System.out.println(count);
        }

        System.out.println("Done!");

    }



}
