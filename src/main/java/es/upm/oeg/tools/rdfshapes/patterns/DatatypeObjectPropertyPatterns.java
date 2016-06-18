package es.upm.oeg.tools.rdfshapes.patterns;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.hp.hpl.jena.rdf.model.RDFNode;
import es.upm.oeg.tools.rdfshapes.extractors.QueryBase;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Copyright 2014-2016 Ontology Engineering Group, Universidad Politécnica de Madrid, Spain
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @author Nandana Mihindukulasooriya
 * @since 1.0.0
 */
public class DatatypeObjectPropertyPatterns extends QueryBase {

    private static String classPropertyQueryPath = "src/main/resources/3cixty/class-properties.sparql";
    private static String propertyCardinalityQueryPath = "src/main/resources/3cixty/cardinality.sparql";
    private static String individualCountQueryPath = "src/main/resources/common/instance-count.sparql";
    private static String objectCountQueryPath = "src/main/resources/query/countObjectsWithProperty.rq";
    private static String tripleCountQueryPath = "src/main/resources/query/countTriplesWithProperty.rq";
    private static String literalCountQueryPath = "src/main/resources/query/literalCount.rq";
    private static String blankCountQueryPath = "src/main/resources/query/blankCount.rq";
    private static String iriCountQueryPath = "src/main/resources/query/iriCount.rq";
    private static String classListPath = "src/main/resources/3cixty/classlist.txt";
    private static String datatypeCountsPath = "src/main/resources/common/literal/datatype-count.sparql";

    public static void main(String[] args) throws Exception {

        String endpoint = "http://3cixty.eurecom.fr/sparql";

        List<String> classList = Files.
                readAllLines(Paths.get(classListPath),
                        Charset.defaultCharset());

        String classPropertyQueryString = readFile(classPropertyQueryPath, Charset.defaultCharset());
        String propertyCardinalityQueryString = readFile(propertyCardinalityQueryPath, Charset.defaultCharset());
        String individualCountQueryString = readFile(individualCountQueryPath, Charset.defaultCharset());
        String objectCountQueryString = readFile(objectCountQueryPath, Charset.defaultCharset());
        String tripleCountQueryString = readFile(tripleCountQueryPath, Charset.defaultCharset());
        String literalCountQueryString = readFile(literalCountQueryPath, Charset.defaultCharset());
        String blankCountQueryString = readFile(blankCountQueryPath, Charset.defaultCharset());
        String iriCountQueryString = readFile(iriCountQueryPath, Charset.defaultCharset());
        String datatypeCountQueryString = readFile(datatypeCountsPath, Charset.defaultCharset());

        DecimalFormat df = new DecimalFormat("0.0000");

        //Create the Excel workbook and sheet
        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet = wb.createSheet("Cardinality");

        int currentExcelRow = 0;
        int classStartRow = 0;

        for (String clazz : classList) {

            System.out.println("Class: " + clazz );

            Map<String, String> litMap = new HashMap<>();
            Map<String, String> iriMap = ImmutableMap.of("class", clazz);

            String queryString = bindQueryString(individualCountQueryString, ImmutableMap.of(IRI_BINDINGS, iriMap, LITERAL_BINDINGS, litMap));

            int individualCount;
            List<RDFNode> c = executeQueryForList(queryString, endpoint, "c");
            if (c.size() == 1) {
                individualCount = c.get(0).asLiteral().getInt();
            } else {
                continue;
            }

            // If there are zero individuals, continue
            if (individualCount == 0) {
                throw new IllegalStateException("Check whether " + classListPath + " and " + endpoint + " match.");
            }


            classStartRow = currentExcelRow;
            XSSFRow row = sheet.createRow(currentExcelRow);
            XSSFCell cell = row.createCell(0);
            cell.setCellValue(clazz);

            litMap = new HashMap<>();
            iriMap = ImmutableMap.of("class", clazz);
            queryString = bindQueryString(classPropertyQueryString, ImmutableMap.of(IRI_BINDINGS, iriMap, LITERAL_BINDINGS, litMap));
            List<RDFNode> nodeList = executeQueryForList(queryString, endpoint, "p");

//            System.out.println("***");
//            System.out.println("### **" + clazz + "**");
//            System.out.println("***");
//            System.out.println();



            cell.getCellStyle().setAlignment(CellStyle.ALIGN_CENTER);

            for (RDFNode property : nodeList) {
                if (property.isURIResource()) {

                    System.out.println("          " + property );

                    int tripleCount;
                    int objectCount;
                    int literalCount;
                    int blankCount;
                    int iriCount;

                    String propertyURI = property.asResource().getURI();

                    XSSFRow propertyRow = sheet.getRow(currentExcelRow);
                    if (propertyRow == null) {
                        propertyRow = sheet.createRow(currentExcelRow);
                    }
                    currentExcelRow++;
                    XSSFCell propertyCell = propertyRow.createCell(1);
                    propertyCell.setCellValue(propertyURI);

                    litMap = new HashMap<>();
                    iriMap = ImmutableMap.of("class", clazz, "p", propertyURI);

                    queryString = bindQueryString(tripleCountQueryString, ImmutableMap.of(IRI_BINDINGS, iriMap, LITERAL_BINDINGS, litMap));

                    c = executeQueryForList(queryString, endpoint, "c");
                    if (c.size() > 0) {
                        tripleCount = c.get(0).asLiteral().getInt();
                    } else {
                        tripleCount = 0;
                    }

                    queryString = bindQueryString(objectCountQueryString, ImmutableMap.of(IRI_BINDINGS, iriMap, LITERAL_BINDINGS, litMap));

                    c = executeQueryForList(queryString, endpoint, "c");
                    if (c.size() > 0) {
                        objectCount = c.get(0).asLiteral().getInt();
                    } else {
                        objectCount = 0;
                    }

                    queryString = bindQueryString(literalCountQueryString, ImmutableMap.of(IRI_BINDINGS, iriMap, LITERAL_BINDINGS, litMap));
                    c = executeQueryForList(queryString, endpoint, "c");
                    if (c.size() > 0) {
                        literalCount = c.get(0).asLiteral().getInt();
                    } else {
                        literalCount = 0;
                    }

                    queryString = bindQueryString(blankCountQueryString, ImmutableMap.of(IRI_BINDINGS, iriMap, LITERAL_BINDINGS, litMap));
                    c = executeQueryForList(queryString, endpoint, "c");
                    if (c.size() > 0) {
                        blankCount = c.get(0).asLiteral().getInt();
                    } else {
                        blankCount = 0;
                    }

                    queryString = bindQueryString(iriCountQueryString, ImmutableMap.of(IRI_BINDINGS, iriMap, LITERAL_BINDINGS, litMap));
                    c = executeQueryForList(queryString, endpoint, "c");
                    if (c.size() > 0) {
                        iriCount = c.get(0).asLiteral().getInt();
                    } else {
                        iriCount = 0;
                    }


                    XSSFCell objectCountCell = propertyRow.createCell(2);
                    objectCountCell.setCellValue(objectCount);

                    XSSFCell uniqueObjectsCell = propertyRow.createCell(3);
                    uniqueObjectsCell.setCellValue(df.format(((double) objectCount)/tripleCount));

                    XSSFCell literalCell = propertyRow.createCell(4);
                    literalCell.setCellValue(df.format((((double) literalCount)/objectCount)));

                    XSSFCell iriCell = propertyRow.createCell(5);
                    iriCell.setCellValue(df.format((((double) iriCount)/objectCount)));

                    XSSFCell blankCell = propertyRow.createCell(6);
                    blankCell.setCellValue(df.format((((double) blankCount)/objectCount)));


                    if (literalCount > 0) {

                        litMap = new HashMap<>();
                        iriMap = ImmutableMap.of("class", clazz, "p", propertyURI);

                        queryString = bindQueryString(datatypeCountQueryString, ImmutableMap.of(IRI_BINDINGS, iriMap, LITERAL_BINDINGS, litMap));
                        List<Map<String, RDFNode>> solnMaps = executeQueryForList(queryString, endpoint, ImmutableSet.of("datatype", "c"));

                        int i = 1;
                        for (Map<String, RDFNode> soln : solnMaps) {
                            String datatype = soln.get("datatype").asResource().getURI();
                            int count = soln.get("c").asLiteral().getInt();

                            XSSFCell dataCell = propertyRow.createCell(6 + i++);
                            dataCell.setCellValue(datatype);

                            dataCell = propertyRow.createCell(6 + i++);
                            dataCell.setCellValue(df.format((((double) count)/objectCount)));

                        }

                    }


//                    System.out.println("* " + propertyURI);
//                    System.out.println();
//
//                    System.out.println("| Object Count | Unique Objects | Literals | IRIs | Blank Nodes | ");
//                    System.out.println("|---|---|---|---|---|");
//                    System.out.println(String.format("|%d|%d (%.2f%%) |%d (%.2f%%)|%d (%.2f%%)|%d (%.2f%%)|",
//                            tripleCount,
//                            objectCount, ((((double) objectCount)/tripleCount)*100),
//                            literalCount, ((((double) literalCount)/objectCount)*100),
//                            iriCount, ((((double) iriCount)/objectCount)*100),
//                            blankCount, ((((double) blankCount)/objectCount)*100)));
//                    System.out.println();
                }
            }
        }

        String filename = "literals.xls" ;
        FileOutputStream fileOut = new FileOutputStream(filename);
        wb.write(fileOut);
        fileOut.close();

    }

}
