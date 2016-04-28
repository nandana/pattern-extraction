package es.upm.oeg.tools.rdfshapes.utils;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.hp.hpl.jena.rdf.model.RDFNode;
import es.upm.oeg.tools.rdfshapes.extractors.QueryBase;
import es.upm.oeg.tools.rdfshapes.stats.CardinalityCount;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
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
import java.text.DecimalFormat;
import java.util.*;

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
public class CadinalityResultGenerator extends QueryBase {

    private static String classPropertyQueryPath = "src/main/resources/3cixty/class-properties.sparql";
    private static String propertyCardinalityQueryPath = "src/main/resources/3cixty/cardinality.sparql";
    private static String individualCountQueryPath = "src/main/resources/common/instance-count.sparql";
    private static String classListPath = "src/main/resources/3cixty/classlist.txt";

    public static void main(String[] args) throws Exception {

        String endpoint = "http://3cixty.eurecom.fr/sparql";

        List<String> classList = Files.
                readAllLines(Paths.get(classListPath),
                        Charset.defaultCharset());

        String classPropertyQueryString = readFile(classPropertyQueryPath, Charset.defaultCharset());
        String propertyCardinalityQueryString = readFile(propertyCardinalityQueryPath, Charset.defaultCharset());
        String individualCountQueryString = readFile(individualCountQueryPath, Charset.defaultCharset());

        DecimalFormat df = new DecimalFormat("0.0000");


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

                    DescriptiveStatistics stats = new DescriptiveStatistics();

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

                    Map<String, String> litMap2 = new HashMap<>();
                    Map<String, String> iriMap2 = ImmutableMap.of("class", clazz, "p", propertyURI);

                    queryString = bindQueryString(propertyCardinalityQueryString, ImmutableMap.of(IRI_BINDINGS, iriMap2, LITERAL_BINDINGS, litMap2));

                    List<Map<String, RDFNode>> solnMaps = executeQueryForList(queryString, endpoint, ImmutableSet.of("card", "count"));

                    int sum = 0;
                    List<CardinalityCount> cardinalityList = new ArrayList<>();
                    if (solnMaps.size() > 0) {

                        for (Map<String, RDFNode> soln : solnMaps) {
                            int count = soln.get("count").asLiteral().getInt();
                            int card = soln.get("card").asLiteral().getInt();

                            for (int i = 0 ; i < count; i++) {
                                stats.addValue(card);
                            }

                            CardinalityCount cardinalityCount = new CardinalityCount(card, count, (((double)count) / individualCount) * 100);
                            cardinalityList.add(cardinalityCount);
                            sum += count;
                        }

                        // Check for zero cardinality instances
                        int count = individualCount - sum;
                        if (count > 0) {
                            for (int i = 0; i < count; i++) {
                                stats.addValue(0);
                            }
                            CardinalityCount cardinalityCount = new CardinalityCount(0, count, (((double)count) / individualCount) * 100);
                            cardinalityList.add(cardinalityCount);
                        }
                    }

                    Map<Integer, Double> cardMap = new HashMap<>();
                    for (CardinalityCount count : cardinalityList) {
                            cardMap.put(count.getCardinality(), count.getPrecentage());
                    }


                    XSSFCell instanceCountCell = propertyRow.createCell(2);
                    instanceCountCell.setCellValue(individualCount);

                    XSSFCell minCell = propertyRow.createCell(3);
                    minCell.setCellValue(stats.getMin());

                    XSSFCell maxCell = propertyRow.createCell(4);
                    maxCell.setCellValue(stats.getMax());

                    XSSFCell p1 = propertyRow.createCell(5);
                    p1.setCellValue(stats.getPercentile(1));

                    XSSFCell p99 = propertyRow.createCell(6);
                    p99.setCellValue(stats.getPercentile(99));

                    XSSFCell mean = propertyRow.createCell(7);
                    mean.setCellValue(df.format(stats.getMean()));

                    for (int i = 0; i < 21 ; i++) {
                        XSSFCell dataCell = propertyRow.createCell(8+i);
                        Double percentage = cardMap.get(i);
                        if (percentage != null) {
                            dataCell.setCellValue(df.format(percentage));
                        } else {
                            dataCell.setCellValue(0);
                        }
                    }

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

        String filename = "3cixty.xls" ;
        FileOutputStream fileOut = new FileOutputStream(filename);
        wb.write(fileOut);
        fileOut.close();
    }

}
