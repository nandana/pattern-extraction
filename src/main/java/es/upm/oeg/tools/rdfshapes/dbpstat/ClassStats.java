package es.upm.oeg.tools.rdfshapes.dbpstat;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.io.CharStreams;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.jena.atlas.RuntimeIOException;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.elasticsearch.common.base.Joiner;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

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
public class ClassStats {


    public static void main(String[] args) throws Exception {

        List<String> propertyList = Files.
                readAllLines(Paths.get("src/main/resources/dbpstat/classes.txt"),
                        Charset.defaultCharset());
        //List<String> langList = ImmutableList.of("en", "es", "de", "fr", "nl");

        List<String> langList = ImmutableList.of("ar", "az", "be", "bg", "bn", "ca", "cs", "cy", "de", "el",
                "en", "eo", "es", "eu", "fr", "ga", "hr", "hu", "hy", "id", "it", "ja", "ko", "nl", "pl", "pt", "ru", "sk", "sl", "sr", "sv", "tr", "uk");

        int[] countArray = new int[langList.size()+1];
        int[] propertyUsedCount = new int[propertyList.size()];

        String jsonString = readFile("dbpstat/stats-instances.json");
        JsonObject obj = new JsonParser().parse(jsonString).getAsJsonObject();

        //Create the Excel workbook and sheet
        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet = wb.createSheet("ClassCount");

        int currentExcelRow = 0;
        for (String property : propertyList) {
            XSSFRow row = sheet.createRow(currentExcelRow);
            XSSFCell cell1 = row.createCell(0);
            cell1.setCellValue(property);
            currentExcelRow++;
        }

        currentExcelRow = 0;
        int currentColumn = 1;

        for (String lang : langList) {

            int langClassUsageCount = 0;
            for (String property : propertyList) {

                JsonElement ele = obj.get(lang).getAsJsonObject().get("objects").getAsJsonObject().get(property);

                XSSFRow row = sheet.getRow(currentExcelRow);
                if (row == null) {
                    throw new IllegalStateException("Row should exist for each property! - " + property + " - " + currentExcelRow);
                }
                XSSFCell cell = row.createCell(currentColumn);


                if (ele != null) {
                    cell.setCellValue(String.valueOf(ele.getAsBigInteger()));
                    langClassUsageCount++;
                } else {
                    cell.setCellValue("0");
                }
                currentExcelRow++;
            }

            //Print lang total property usage
            System.out.println( langClassUsageCount);

            currentExcelRow = 0;
            currentColumn++;
        }

        currentExcelRow = 0;
        int langSize = langList.size();

        for (String property : propertyList) {

            XSSFRow row = sheet.getRow(currentExcelRow);
            if (row == null) {
                throw new IllegalStateException("Row should exist for each property! - " + property + " - " + currentExcelRow);
            }

            int usedCount = 0;
            List<String> langs = new ArrayList<>();

            for (int j = 0; j < langSize; j++) {
                if ( ! "0".equals(row.getCell(j+1).getStringCellValue())) {
                    usedCount++;
                    langs.add(langList.get(j));
                }
            }

            // collect the counts
            countArray[usedCount] = countArray[usedCount] + 1;
            propertyUsedCount[currentExcelRow] = usedCount;

            //System.out.println("<" + property + "> " + "<http://www.w3.org/ns/odrl/2/count> " + usedCount + " .");

            System.out.println("<" + property + "> " + "<http://www.w3.org/ns/odrl/2/list> \"" + Joiner.on(",").join(langs) + "\" .");

            XSSFCell cellCount = row.createCell(langSize+1);
            cellCount.setCellValue(usedCount);

            XSSFCell cellLangs = row.createCell(langSize+2);
            cellLangs.setCellValue(Joiner.on(",").join(langs));

            currentExcelRow++;
        }

        for (String lang : langList) {

            int[] matrixRaw = new int[langList.size()];

            int propertyCount = 0;
            for (String property : propertyList) {

                JsonElement ele = obj.get(lang).getAsJsonObject().get("objects").getAsJsonObject().get(property);
                if (ele != null) {
                    int usedCount = propertyUsedCount[propertyCount];
                    if (usedCount == 0) {
                        throw new RuntimeException("Used count can not be 0 for a property used by a lang");
                    }
                    matrixRaw[usedCount-1] = matrixRaw[usedCount-1] + 1;

                } else {

                }
                propertyCount++;
            }

//            System.out.println(lang);
//            for (int cell: matrixRaw) {
//                System.out.print(cell + ", ");
//            }
//            System.out.println();
//            System.out.println();

        }




        String filename = "class2.xls" ;
        FileOutputStream fileOut = new FileOutputStream(filename);
        wb.write(fileOut);
        fileOut.close();

    }

    public static String readFile(String path)
            throws IOException {

        try (InputStream inputStream = PropertyStats.class.getClassLoader().getResourceAsStream(path))
        {
            return CharStreams.toString(new InputStreamReader(inputStream, Charsets.UTF_8));
        }
    }




}
