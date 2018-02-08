package es.upm.oeg.tools.rdfshapes.mappings;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * @author Nandana Mihindukulasooriya
 * @since 22/03/17.
 */
public class HierarchicalFeatures {

    public static void main(String[] args) throws Exception {

        List<String> data = Files.
                readAllLines(Paths.get("src/main/resources/mappings/training/en-nl-lit.csv"),
                        Charset.defaultCharset());


        int count = 1;

        for (String dataLine : data) {
            String[] elements = dataLine.split(",");

            String propEn  = elements[0];
            String propEs  = elements[1];

//            System.out.println(propEn);
//            System.out.println(propEs);

            System.out.print( (DBO.isSubProperty(propEn, propEs) ? 1 : 0) + ",");
            System.out.print( (DBO.isSubProperty(propEs, propEn)  ? 1 : 0) + ",");

            String classEn  = elements[2];
            String classEs  = elements[3];

            if ("".equals(classEn.trim()) || "".equals(classEs.trim())) {
                System.out.print("0,0,0,");
            } else {
                System.out.print( (classEn.equals(classEs) ? 1 : 0) + "," );
                if (!classEn.equals(classEs)) {
                    System.out.print( (DBO.isSubClass(classEn, classEs) ? 1 : 0) + ",");
                    System.out.print( (DBO.isSubClass(classEs, classEn) ? 1 : 0) + ",");
                } else {
                    System.out.print("0,0,");
                }
            }

            String domainPropEn = "";
            if (elements.length > 4) {
                domainPropEn = elements[4];
            }

            String domainPropEs = "";
            if (elements.length > 5) {
                domainPropEs = elements[5];
            }

            if ("".equals(domainPropEn.trim()) || "".equals(domainPropEs.trim())) {
                System.out.print("0,0,0,");
            } else {
                System.out.print( (domainPropEn.equals(domainPropEs) ? 1 : 0) + "," );
                if (!domainPropEn.equals(domainPropEs)) {
                    System.out.print( (DBO.isSubClass(domainPropEn, domainPropEs) ? 1 : 0) + ",");
                    System.out.print( (DBO.isSubClass(domainPropEs, domainPropEn) ? 1 : 0) + ",");
                } else {
                    System.out.print("0,0,");
                }
            }

            String rangePropEn = "";
            if (elements.length > 6) {
                rangePropEn = elements[6];
            }

            String rangePropEs = "";
            if (elements.length > 7) {
                rangePropEs = elements[7];
            }

            if ("".equals(rangePropEn.trim()) || "".equals(rangePropEs.trim())) {
                System.out.print("0,0,0");
            } else {
                System.out.print( (rangePropEn.equals(rangePropEs) ? 1 : 0) + "," );
                if (!rangePropEn.equals(rangePropEs)) {
                    System.out.print( (DBO.isSubClass(rangePropEn, rangePropEs) ? 1 : 0) + ",");
                    System.out.print( (DBO.isSubClass(rangePropEs, rangePropEn) ? 1 : 0) + "");
                } else {
                    System.out.print("0,0");
                }
            }



            System.out.println();
        }



    }


}
