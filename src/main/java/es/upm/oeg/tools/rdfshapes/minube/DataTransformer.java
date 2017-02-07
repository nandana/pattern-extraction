package es.upm.oeg.tools.rdfshapes.minube;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

import java.io.File;
import java.util.List;

/**
 * @author Nandana Mihindukulasooriya
 * @since 23/01/17.
 */
public class DataTransformer {

    public static void main(String[] args) throws Exception {

        String categoriesURI = "https://w3id.org/vocab/tourism/pos/categories";
        String categoryPrefix =  "https://w3id.org/vocab/tourism/pos/category/";


        Model m_model = ModelFactory.createDefaultModel();
        Resource categories = m_model.createResource(categoriesURI, SKOS.ConceptScheme);

        String path = "/home/nandana/data/sri_lanka/subcategories-en.json";

        ObjectMapper mapper = new ObjectMapper();;
        TypeFactory typeFactory = mapper.getTypeFactory();

        List<SubCategory> subCategoryList =
                mapper.readValue(new File(path), typeFactory.constructCollectionType(List.class, SubCategory.class));

//        subCategoryList.forEach(
//                s -> {
//                    int id = s.getId();
//
//
//                    m_model.createResource()
//
//
//
//                    System.out.println(s.getId());
//            System.out.println(s.getName());
//                });


    }



}
