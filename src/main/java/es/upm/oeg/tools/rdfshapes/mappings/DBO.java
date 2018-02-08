package es.upm.oeg.tools.rdfshapes.mappings;

import com.google.common.base.Joiner;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import es.upm.oeg.tools.rdfshapes.utils.IOUtils;
import es.upm.oeg.tools.rdfshapes.utils.SparqlUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.hp.hpl.jena.ontology.OntModelSpec.OWL_MEM;
import static com.hp.hpl.jena.ontology.OntModelSpec.OWL_MEM_MICRO_RULE_INF;

/**
 * @author Nandana Mihindukulasooriya
 * @since 7/02/17.
 */
public class DBO {

    private static final String SPARQL_Endpoint = "http://4v.dia.fi.upm.es:8890/sparql";
    private static final String DBO_GRAGH = "http://dbpedia.org/o/201604";

//    OntModel inf;

    public DBO() {
//        OntModel base = ModelFactory.createOntologyModel( OWL_MEM );
//        base.read(DBO.class.getClassLoader().getResourceAsStream("mappings/dbpedia.owl"), "RDF/XML" );
//        inf = ModelFactory.createOntologyModel(OWL_MEM_MICRO_RULE_INF , base );
    }

    public static boolean isSubClass(String classA, String classB) {

        String query = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
                "PREFIX dbo: <http://dbpedia.org/ontology/> " +
                "PREFIX geo: <http://www.w3.org/2003/01/geo/wgs84_pos#> " +
                "PREFIX foaf: <http://xmlns.com/foaf/0.1/> " +
                "PREFIX owl: <http://www.w3.org/2002/07/owl#> " +
                "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> " +
                "ask { graph <http://dbpedia.org/ontology> " +
                "{ " + classA + " rdfs:subClassOf* " + classB + " } }";

        //System.out.println(query);

        return SparqlUtils.ask(query, SPARQL_Endpoint);

    }

    public static boolean isSubProperty(String propA, String propB) {

        String query = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
                "PREFIX dbo: <http://dbpedia.org/ontology/> " +
                "PREFIX geo: <http://www.w3.org/2003/01/geo/wgs84_pos#> " +
                "PREFIX foaf: <http://xmlns.com/foaf/0.1/> " +
                "PREFIX owl: <http://www.w3.org/2002/07/owl#> " +
                "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> " +
                "ask { graph <http://dbpedia.org/ontology> " +
                "{ " + propA + " rdfs:subPropertyOf* " + propB + " } }";

        //System.out.println(query);

        return SparqlUtils.ask(query, SPARQL_Endpoint);

    }

    public static String getDomain(String property) {

        String query = "select ?d { graph <http://dbpedia.org/ontology> { <" + property + "> <http://www.w3.org/2000/01/rdf-schema#domain> ?d } }";

        List<RDFNode> domains = SparqlUtils.executeQueryForList(query, SPARQL_Endpoint, "d");

        Set<String> domainList = domains.stream()
                .filter(d -> d.isURIResource())
                .map(s -> s.asResource().getURI())
                .collect(Collectors.toSet());

        return Joiner.on(",").join(domainList);

//        Property p = inf.getProperty(property);
//        OntProperty prop = null;
//        try {
//            prop = p.as(OntProperty.class);
//        } catch (Exception e) {
//            return "";
//        }
//
//        OntResource domain = prop.getDomain();
//        if (domain != null) {
//            return domain.getURI();
//        } else {
//            return "";
//        }
    }

    public static String getRange(String property) {

        String query = "select ?r { graph <http://dbpedia.org/ontology> { <" + property + "> <http://www.w3.org/2000/01/rdf-schema#range> ?r } }";

        List<RDFNode> ranges = SparqlUtils.executeQueryForList(query, SPARQL_Endpoint, "r");

        Set<String> rangeList = ranges.stream()
                .filter(d -> d.isURIResource())
                .map(s -> s.asResource().getURI())
                .collect(Collectors.toSet());

        return Joiner.on(",").join(rangeList);



//        Property p = inf.getProperty(property);
//        OntProperty prop = null;
//        try {
//            prop = p.as(OntProperty.class);
//        } catch (Exception e) {
//            return "";
//        }
//        OntResource range = prop.getRange();
//        if (range != null) {
//            return range.getURI();
//        } else {
//            return "";
//        }
    }

    public static void main(String[] args) {

        String property = "http://dbpedia.org/ontology/birthPlace";

        String classA = "dbo:Person";
        String classB = "dbo:AdultActor";

        DBO dbo = new DBO();
        System.out.println("Range:" + dbo.getRange(property));
        System.out.println("Domain:" + dbo.getDomain(property));
        System.out.println(dbo.isSubClass(classB, classA));
        System.out.println(dbo.isSubProperty( "dbo:isPartOfMilitaryConflict", "dbo:isPartOf"));

    }

}
