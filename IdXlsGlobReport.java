/*
 * Copyright 2018 СДО.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ru.vbfp.idreport;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author СДО
 */
public class IdXlsGlobReport {
    IdFileManager idFmInnerReport;
    private Path fileXlsToWrite;
    private Path excelFile;
    private static final int FILE_ROW_LIMIT = 1000;
    private String sheetName = "Reestr";
    private XSSFWorkbook wb;
    private XSSFSheet sheet;
    private Integer rowCount;
    private Integer colCount;
    
    private String NOT_DETECTED = "|_|_|_|_|NotDetectedType|_|_|_|_|";
    private String AOCP_DETECTED = "|_|_|_|_|AOCP|_|_|_|_|";
    private String AKT_OTHER = "|_|_|_|_|OTHER_AKT|_|_|_|_|";
    private String SCHEME = "|_|_|_|_|SCHEME|_|_|_|_|";
    private String PROTOKOL = "|_|_|_|_|PROTOKOL|_|_|_|_|";
    private String REZULT = "|_|_|_|_|RESULT|_|_|_|_|";
    private String SERTIFICAT = "|_|_|_|_|SERTIFICAT|_|_|_|_|";
    private String PASPORT = "|_|_|_|_|PASPORT|_|_|_|_|";
    private String AOCP_PAGE1_DETECTED = "|_|_|_|_|AOCP-P1|_|_|_|_|";
    private String AOCP_PAGE2_DETECTED = "|_|_|_|_|AOCP-P2|_|_|_|_|";
    private String ABK_DETECTED = "|_|_|_|_|ABK|_|_|_|_|";
    
    public IdXlsGlobReport(IdFileManager idFmReport) {
        idFmInnerReport = idFmReport;
        Path currentReportXlsSumFile = getIterationXlsFileName();
        fileXlsToWrite = currentReportXlsSumFile;
        excelFile = currentReportXlsSumFile;
        wb = new XSSFWorkbook();
        sheet = wb.createSheet(sheetName);
        rowCount = 5;
        colCount = 7;
    }
    protected Path getIterationXlsFileName(){
        return Paths.get(idFmInnerReport.getCurrentReportDir().toString(),
                idFmInnerReport.getNewProcessId() + "-sum-idr.xlsx");
    }
    /*protected List<String> filterStringNew(List<String> linesOuter){
        //@todo filter here
        
        
        ArrayList<String> forFilter = new ArrayList<String>();
        int colCount = 0;
        String forConcatination = "";
        for (String stringForFilter : linesOuter) {
            if( colCount < 2 ){
                if( stringForFilter.equalsIgnoreCase(NOT_DETECTED) ){
                    return new ArrayList<String>();
                }
                forFilter.add(stringForFilter);
            }else{
               String str1 = new String(stringForFilter.trim().getBytes());
               //String str2 = new String(str1.);
               forConcatination = forConcatination + str1;
            }
            colCount++;
        }
        
        
            forFilter.add(forConcatination);
        
        return forFilter;
    }*/
    protected String getWordInLine(String forDeleteWords){
        String[] deletedWords = {"атомному",
            "ведения",
            "документации",
            "инженерно","технического",
            "исполнительной",
            "и-требования",
            "конструкций",
            "надзору",
            "обеспечения",
            "объектов","капитального",
            "порядку",
            "предъявляемые",
            "ПРИЛОЖЕНИЕ",
            "реконструкции","капитальном",
            "ремонте",
            "сетей",
            "службы",
            "составу",
            "строительства",
            "строительстве",
            "технологическому",
            "Требованиям",
            "утвержденнымприказом",
            "участков",
            "Федеральной",
            "экологическому",
            };
        String[] splitedStrs = forDeleteWords.split(" ");
        String forReturnStr = "";
        for (String splitedStrElement : splitedStrs) {
            String strLowerCase = new String(splitedStrElement.toLowerCase().getBytes());
            for (String deletedStrElement : deletedWords) {
                String delLowerCase = new String(deletedStrElement.toLowerCase().getBytes());
                if( delLowerCase.contains(strLowerCase) ){
                    continue;
                }
                forReturnStr = forReturnStr + splitedStrElement;
            }
        }
        return forReturnStr;
    }
    protected void addRow(List<String> linesOuter){
        ArrayList<String> forFilter = new ArrayList<String>();
        forFilter.addAll(filterFormBlackListFile(linesOuter));
        //@todo filter set there
        
        if( !forFilter.isEmpty() ){
            
            
            addRowAfterFilter(forFilter);
        }
        
    }
    private ArrayList<String> filterFormBlackListFile(List<String> filteredLines){
        ArrayList<String> linesDest = new ArrayList<String>();
        Path fileBlackListWord = Paths.get("D:/20181024-0511-vsn-reestr/003/word-black-list.txt");
        if( Files.exists(fileBlackListWord, LinkOption.NOFOLLOW_LINKS) ){
            System.out.println("Word black list found and exist " + fileBlackListWord.toString());
        }
        ArrayList<String> wordsFromBlackList = readCfg(fileBlackListWord.toString());
        System.out.println("Word count in black list " + wordsFromBlackList.size());
        int countOutOfFilter = 0;
        for(String elStr : filteredLines){
            countOutOfFilter++;
            if( countOutOfFilter < 2 ){
                continue;
            }
          
            String strReplaced = new String(elStr.getBytes());
            //String strReplaced = new String(strWithOutDot.replaceAll("\\s*(\\w|\\s|\t|\\.|:|,|\")\\s*", "").getBytes());
            
            for(String strSubRepl : wordsFromBlackList){
                strReplaced = new String(strReplaced.replaceAll("\\s*" + strSubRepl + "\\s*", "").getBytes());
            }
            String strForOutPut = new String(strReplaced.replaceAll("\\s*(\\w|\\s|\t|\\.|:|,|!|\"|\\||$|~|@|#|\\*|%|\\^|&|\\(|\\)|\\{|\\}|\\[|\\]|<|>|\\?)\\s*", "").getBytes());
            //String str3 = new String(str2.replaceAll("\\s*[работ по строитель]-[для физических типу]\\s*", "").toString());
            linesDest.add(strForOutPut);
        }
        return linesDest;
    }
    private static ArrayList<String> readCfg(String ncStrCfgPath){
        ArrayList<String> strForReturn;
        strForReturn = new ArrayList<String>();
        try(BufferedReader br = new BufferedReader(new FileReader(ncStrCfgPath)))
        {
            String s;
            while((s=br.readLine())!=null){
                
                    strForReturn.add(s.trim());
                    //System.out.println(s);
            }
        }
         catch(IOException ex){
            ex.printStackTrace();
            System.out.println("[ERROR] Can`t read count files in work directory " + ncStrCfgPath
            + " " + ex.getMessage());
        }   
        return strForReturn;
    }
    /*protected TreeMap<String, String> getKeysForSubString(){
        TreeMap<String, String> keysForSearch = new TreeMap<String, String>();
        keysForSearch.put("предъявлены следующие работы:", "работы выполнены по проектной");
        
        return keysForSearch;
        
    }
    protected String getSubStringFromKeys(String innerStr){
        TreeMap<String, String> keysForSearch = getKeysForSubString();
        for (Map.Entry<String, String> en : keysForSearch.entrySet()) {
            String key = en.getKey();
            String value = en.getValue();
            innerStr.contains(key);
            innerStr.contains(value);
        }
        return innerStr;
    }*/
    /*protected String replaseFromString(String innerStr){
        
        String lowCaseStr = innerStr.toLowerCase();
        
        if( !lowCaseStr.contains("предъявлены следующие работы:") ){
            return innerStr;
        }
        if( !lowCaseStr.contains("2. Работы выполнены по проектной") ){
            return innerStr;
        }
        
        int startPos = lowCaseStr.indexOf("предъявлены следующие работы:") + "предъявлены следующие работы:".length();
        int stopPos = lowCaseStr.indexOf("2. Работы выполнены по проектной");
        String workStr = innerStr.substring(startPos, stopPos);
        
        int startPosPrjct = lowCaseStr.indexOf("наименование проект") + "наименование проект".length() + 3;
        int stopPosPrjct = lowCaseStr.indexOf("и/ или рабочей документации");
        String prjctStr = innerStr.substring(startPosPrjct, stopPosPrjct);
        
        int startPosMatli = lowCaseStr.indexOf("другие документы, подтверждающие качество") + "другие документы, подтверждающие качество".length() + 1;
        int stopPosMatli = lowCaseStr.indexOf("и/ или рабочей документации");
        String matliStr = innerStr.substring(startPosMatli, stopPosMatli);
        
        String outerStr = workStr + prjctStr + matliStr;
        return outerStr;
    }*/
    //@todo compare with etalon array and percent of compare
    protected void addRowAfterFilter(List<String> linesOuter){
        String fileName = "";
        rowCount++;
        XSSFRow row = sheet.createRow(rowCount);
        int colForWrite = colCount;
        String summaryStr = "";
        for (String stringToXlsxCell : linesOuter) {
            colForWrite++;
            XSSFCell cell = row.createCell(colForWrite);
            cell.setCellValue(stringToXlsxCell);
            summaryStr = summaryStr + stringToXlsxCell;
        }
        XSSFCell cell = row.createCell(3);
        cell.setCellValue(summaryStr);
        saveOldAndCreateNewXlsBook();
    }
    protected void saveOldAndCreateNewXlsBook(){
        if( (rowCount > FILE_ROW_LIMIT) || (rowCount == 0) ){
                if ( rowCount != 0){
                    saveXlsFile();
                    try {
                        wb.close();
                    } catch (IOException ex) {
                        System.out.println(ex.getMessage());
                        ex.printStackTrace();
                    }
                    sheet = null;
                    wb = new XSSFWorkbook();
                    sheet = wb.createSheet(sheetName);
                    excelFile = getIterationXlsFileName();
                }
                rowCount = 5;
                
                try {
                    if( !Files.exists(excelFile, LinkOption.NOFOLLOW_LINKS ) ){
                        Files.createFile(excelFile);
                    }
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                    ex.printStackTrace();
                }

                sheetName = "Reestr";
                wb = new XSSFWorkbook();
                sheet = wb.createSheet(sheetName) ;
                excelFile = getIterationXlsFileName();
                
            }
    }
    protected void saveXlsFile(){
        
        try (FileOutputStream fOutStream = new FileOutputStream(excelFile.toFile())){
            wb.write(fOutStream);
        } catch (FileNotFoundException ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
    }
    
}
