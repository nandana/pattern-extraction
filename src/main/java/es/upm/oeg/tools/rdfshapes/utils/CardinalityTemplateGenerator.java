package es.upm.oeg.tools.rdfshapes.utils;

import com.google.common.collect.ImmutableMap;
import com.hp.hpl.jena.rdf.model.RDFNode;
import es.upm.oeg.tools.rdfshapes.extractors.QueryBase;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
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
public class CardinalityTemplateGenerator extends QueryBase {

    private static String classPropertyQueryPath = "src/main/resources/3cixty/class-properties.sparql";
    private static String propertyCardinalityQueryPath = "src/main/resources/3cixty/cardinality.sparql";
    private static String individualCountQueryPath = "src/main/resources/3cixty/instance-count.sparql";
    private static String classListPath = "src/main/resources/bne/classlist.txt";

    public static void main(String[] args) throws Exception {

        String endpoint = "http://infra2.dia.fi.upm.es:8899/sparql";

        List<String> classList = Files.
                readAllLines(Paths.get(classListPath),
                        Charset.defaultCharset());

        String classPropertyQueryString = readFile(classPropertyQueryPath, Charset.defaultCharset());
        String propertyCardinalityQueryString = readFile(propertyCardinalityQueryPath, Charset.defaultCharset());
        String individualCountQueryString = readFile(individualCountQueryPath, Charset.defaultCharset());


        //Create the Excel workbook and sheet
        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet = wb.createSheet("Cardinality");

        int currentExcelRow = 0;
        int classStartRow = 0;

        for (String clazz : classList) {

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

//            System.out.println("***");
//            System.out.println("### **" + clazz + "** (" + individualCount + ")");
//            System.out.println("***");
//            System.out.println();

            classStartRow = currentExcelRow;
            XSSFRow row = sheet.createRow(currentExcelRow);
            XSSFCell cell = row.createCell(0);
            cell.setCellValue(clazz);
            cell.getCellStyle().setAlignment(CellStyle.ALIGN_CENTER);

            queryString = bindQueryString(classPropertyQueryString, ImmutableMap.of(IRI_BINDINGS, iriMap, LITERAL_BINDINGS, litMap));

            List<RDFNode> nodeList = executeQueryForList(queryString, endpoint, "p");

            for (RDFNode property : nodeList) {
                if (property.isURIResource()) {

                    String propertyURI = property.asResource().getURI();
//                    System.out.println("* " + propertyURI);
//                    System.out.println();


                    XSSFRow propertyRow = sheet.getRow(currentExcelRow);
                    if (propertyRow == null) {
                        propertyRow = sheet.createRow(currentExcelRow);
                    }
                    currentExcelRow++;

                    XSSFCell propertyCell = propertyRow.createCell(1);
                    propertyCell.setCellValue(propertyURI);

//                    System.out.println("| Min Card. |Max Card. |");
//                    System.out.println("|---|---|");
//                    System.out.println("| ? | ? |");
//                    System.out.println();

                }
            }

            //System.out.println("class start: " + classStartRow + ", class end: " + (currentExcelRow -1));
            //We have finished writting properties of one class, now it's time to merge the cells
            int classEndRow = currentExcelRow-1;
            if (classStartRow < classEndRow) {
                sheet.addMergedRegion(new CellRangeAddress(classStartRow, classEndRow, 0, 0));
            }

        }

        String filename = "test.xls" ;
        FileOutputStream fileOut = new FileOutputStream(filename);
        wb.write(fileOut);
        fileOut.close();

    }
}
