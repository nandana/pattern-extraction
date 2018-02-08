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
public class LLODProperties {

    private static final String DG = "http://loupe.linkeddata.es/d10/";

    private static final String SPARQL = "http://4v.dia.fi.upm.es:14014/sparql";

    private static final String LEMON_MODEL_PROPERTIES = "src/main/resources/ssn/lemon-model-propertylist.txt";
    private static final String ONTOLEX_PROPERTIES = "src/main/resources/ssn/ontolex-propertylist.txt";

    public static void main(String[] args) throws Exception {

        String classQuery  = IOUtils.readFile("src/main/resources/ssn/prop.rq", Charset.defaultCharset());

        List<String> propertyList = Files.
                readAllLines(Paths.get(ONTOLEX_PROPERTIES),
                        Charset.defaultCharset());

        for (String property :  propertyList) {

            ParameterizedSparqlString pss = new ParameterizedSparqlString();
            pss.setCommandText(classQuery);
            pss.setIri("p", property);
            pss.setIri("graph", DG);

            String queryString = pss.toString();
            //System.out.println(queryString);

            long count = SparqlUtils.executeQueryForLong(queryString, SPARQL, "c");

            //System.out.println(property + " , " + count);
            System.out.println(count);
        }

        System.out.println("Done!");



    }



}
