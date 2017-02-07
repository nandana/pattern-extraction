package es.upm.oeg.tools.rdfshapes.minube;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * @author Nandana Mihindukulasooriya
 * @since 23/01/17.
 */
public class TourismVocab {

    /** <p>The RDF model that holds the vocabulary terms</p> */
    protected static Model m_model = ModelFactory.createDefaultModel();



















    static Resource createClass(String ns, String clazz) {
        return m_model.createResource(ns + clazz );
    }

    static Property createProperty(String ns, String property) {
        return m_model.createProperty( ns + property);
    }



}
