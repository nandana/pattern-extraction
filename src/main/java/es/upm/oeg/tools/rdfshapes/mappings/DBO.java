package es.upm.oeg.tools.rdfshapes.mappings;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import es.upm.oeg.tools.rdfshapes.utils.IOUtils;

import static com.hp.hpl.jena.ontology.OntModelSpec.OWL_MEM;
import static com.hp.hpl.jena.ontology.OntModelSpec.OWL_MEM_MICRO_RULE_INF;

/**
 * @author Nandana Mihindukulasooriya
 * @since 7/02/17.
 */
public class DBO {

    OntModel inf;

    public DBO() {
        OntModel base = ModelFactory.createOntologyModel( OWL_MEM );
        base.read(DBO.class.getClassLoader().getResourceAsStream("mappings/dbpedia.owl"), "RDF/XML" );
        inf = ModelFactory.createOntologyModel(OWL_MEM_MICRO_RULE_INF , base );
    }

    public String getDomain(String property) {
        Property p = inf.getProperty(property);
        OntProperty prop = (OntProperty) p.as( OntProperty.class);
        OntResource domain = prop.getDomain();
        if (domain != null) {
            return domain.getURI();
        } else {
            return "";
        }
    }

    public String getRange(String property) {
        Property p = inf.getProperty(property);
        OntProperty prop = (OntProperty) p.as( OntProperty.class);
        OntResource range = prop.getRange();
        if (range != null) {
            return range.getURI();
        } else {
            return "";
        }
    }

}
