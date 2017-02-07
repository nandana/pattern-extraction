package es.upm.oeg.tools.rdfshapes.minube;


import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

public class SKOS {

    /** <p>The RDF model that holds the vocabulary terms</p> */
    protected static Model m_model = ModelFactory.createDefaultModel();

    /** <p>The namespace of the vocabulary as a string</p> */
    public static final String NS = "http://www.w3.org/2004/02/skos/core#";


    public static final Resource Concept = createClass(NS, "Concept");

    public static final Resource ConceptScheme = createClass(NS, "ConceptScheme");

    public static final Property inScheme = createProperty(NS, "inScheme");

    static Resource createClass(String ns, String clazz) {
        return m_model.createResource(ns + clazz );
    }

    static Property createProperty(String ns, String property) {
        return m_model.createProperty( ns + property);
    }



}
