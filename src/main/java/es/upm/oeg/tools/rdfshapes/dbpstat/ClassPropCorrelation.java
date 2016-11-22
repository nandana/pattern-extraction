package es.upm.oeg.tools.rdfshapes.dbpstat;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.io.CharStreams;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
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
public class ClassPropCorrelation {

    public static final String OBJECTS = "objects";

    public static final String PROPERTIES = "properties";

    public static void main(String[] args) throws Exception {


        List<String> classList = Files.
                readAllLines(Paths.get("src/main/resources/dbpstat/classes.txt"),
                        Charset.defaultCharset());
        List<String> propertyList = Files.
                readAllLines(Paths.get("src/main/resources/dbpstat/props.txt"),
                        Charset.defaultCharset());
        List<String> langAllList = ImmutableList.of("ar", "az", "be", "bg", "bn", "ca", "cs", "cy", "da", "de", "el",
                "en", "eo", "es", "eu", "fr", "ga", "hr", "hu", "hy", "id", "it", "ja", "ko", "nl", "pl", "pt", "ru", "sk", "sl", "sr", "sv", "tr", "uk");

        System.out.println(langAllList.size());


        List<List<String>> langCombinations = getAllCombinationsTwo(langAllList);

        Gson gson = new Gson();
        String jsonString = readFile("dbpstat/stats-mappingbased.json");
        JsonObject obj = new JsonParser().parse(jsonString).getAsJsonObject();

        String jsonClassString = readFile("dbpstat/stats-instances.json");
        JsonObject objClass = new JsonParser().parse(jsonClassString).getAsJsonObject();


        //Create the Excel workbook and sheet
        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet = wb.createSheet("PropClassCorrelation");

        int currentExcelRow = 0;
        for (List<String> combination : langCombinations) {
            XSSFRow row = sheet.createRow(currentExcelRow);
            XSSFCell cell0 = row.createCell(0);
            cell0.setCellValue(combination.get(0) + "-" + combination.get(1));
            XSSFCell cell1 = row.createCell(1);
            cell1.setCellValue(combination.get(0));
            XSSFCell cell2 = row.createCell(2);
            cell2.setCellValue(combination.get(1));
            currentExcelRow++;
        }

        currentExcelRow = 0;
        for (List<String> combination : langCombinations) {

            String langA = combination.get(0);
            String langB = combination.get(1);

            //Calculate common class count
            int commonClassCount = 0;
            for (String clazz : classList) {
                if (hasInstances(objClass, OBJECTS, langA, clazz) && hasInstances(objClass, OBJECTS, langB, clazz)) {
                    commonClassCount++;
                }
            }

            int commonPropertyCount = 0;
            for (String prop : propertyList) {
                if (hasInstances(obj, PROPERTIES, langA, prop) && hasInstances(obj, PROPERTIES, langB, prop)) {
                    commonPropertyCount++;
                }
            }

            XSSFRow row = sheet.getRow(currentExcelRow);
            if (row == null) {
                throw new IllegalStateException("Row should exist for combination! - (" + langA + "," + langB + ") - " + currentExcelRow);
            }
            XSSFCell cell3 = row.createCell(3);
            cell3.setCellValue(String.valueOf(commonClassCount));

            XSSFCell cell4 = row.createCell(4);
            cell4.setCellValue(String.valueOf(commonPropertyCount));

            currentExcelRow++;
        }

        String filename = "corelation.xls" ;
        FileOutputStream fileOut = new FileOutputStream(filename);
        wb.write(fileOut);
        fileOut.close();


    }

    public static boolean hasInstances(JsonObject obj, String type, String lang, String clazz) {

        JsonElement ele = obj.get(lang).getAsJsonObject().get(type).getAsJsonObject().get(clazz);
        if (ele != null) {
            BigInteger value = ele.getAsBigInteger();
            if (value.intValue() > 0) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public static List<List<String>> getAllCombinationsTwo(List<String> itemList) {


        List<List<String>> results = new ArrayList();

        for (int i = 0; i < itemList.size(); i++)  {
            for (int j = i+1; j < itemList.size() ; j++) {
                results.add(ImmutableList.of(itemList.get(i), itemList.get(j)));
            }
        }

        return results;
    }

    public static String readFile(String path)
            throws IOException {

        try (InputStream inputStream = PropertyStats.class.getClassLoader().getResourceAsStream(path))
        {
            return CharStreams.toString(new InputStreamReader(inputStream, Charsets.UTF_8));
        }
    }
}
