package es.upm.oeg.tools.rdfshapes.utils;

import org.aksw.rdfunit.utils.RDFUnitUtils;
import org.junit.Test;
import org.junit.Before; 
import org.junit.After;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** 
* VocabUtils Tester. 
* 
* @author Nandana Mihindukulasooriya
* @since <pre>Apr 25, 2016</pre> 
* @version 1.0 
*/ 
public class VocabUtilsTest {


    private List<String> threeSixtyProperties;

    @Before
    public void before() throws Exception {
        threeSixtyProperties = Files.
                readAllLines(Paths.get("src/main/resources/3cixty/properties.txt"),
                        Charset.defaultCharset());
    }

    @After
    public void after() throws Exception {
    }

    /**
    *
    * Method: analyzeProperty(String property)
    *
    */
    @Test
    public void testAnalyzeProperty() throws Exception {

        threeSixtyProperties = new ArrayList<>();
        threeSixtyProperties.add("http://xmlns.com/foaf/0.1/name");

        threeSixtyProperties.forEach(property -> VocabUtils.analyzeProperty(property));

    }


} 
