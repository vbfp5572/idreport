/*
 * Copyright 2018 VB.
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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author vbfp
 */
public class IdReporter {
    private IdXlsGlobReport xlsSummaryReport;
    IdDictManager dictonariesManager;
    
    private String srcFileName;
    private Integer rowCount;
    private Integer colCount;
    private Path excelFile;
    private static final int FILE_ROW_LIMIT = 100000;
    private String sheetName = "Reestr";
    private XSSFWorkbook wb;
    private XSSFSheet sheet;
    private IdFileManager idinnerFmReport;
    private Path currentReportFolder;
    
    private String NOT_DETECTED = "|_|_|_|_|NotDetectedType|_|_|_|_|";
    private String ABK_DETECTED = "|_|_|_|_|ABK|_|_|_|_|";
    private String AOCP_DETECTED = "|_|_|_|_|AOCP|_|_|_|_|";
    private String AOCP_PAGE1_DETECTED = "|_|_|_|_|AOCP-P1|_|_|_|_|";
    private String AOCP_PAGE2_DETECTED = "|_|_|_|_|AOCP-P2|_|_|_|_|";
    private String AKT_OTHER = "|_|_|_|_|OTHER_AKT|_|_|_|_|";
    private String SCHEME = "|_|_|_|_|SCHEME|_|_|_|_|";
    private String PROTOKOL = "|_|_|_|_|PROTOKOL|_|_|_|_|";
    private String REZULT = "|_|_|_|_|RESULT|_|_|_|_|";
    private String SERTIFICAT = "|_|_|_|_|SERTIFICAT|_|_|_|_|";
    private String PASPORT = "|_|_|_|_|PASPORT|_|_|_|_|";
    
    private String ALL_LINES_NOT_FILTERED = "|_|_|_|_|allLinesNotFilteredInFile|_|_|_|_|";
    
    
    
    private ArrayList<Path> filesForReport;
    public IdReporter(ArrayList<Path> filesInWorkTxtTesseractDir, IdFileManager idOuterFmReport) {
        xlsSummaryReport = null;
        idinnerFmReport = idOuterFmReport;
        filesForReport = filesInWorkTxtTesseractDir;
        rowCount = 5;
        colCount = 7;
        excelFile = idinnerFmReport.getXlsReportFileName();
        wb = new XSSFWorkbook();
        sheet = wb.createSheet(sheetName);
        srcFileName = "";
        
        currentReportFolder = idinnerFmReport.getCurrentReportDir();
        dictonariesManager = new IdDictManager(idinnerFmReport);
    }
    public IdReporter(ArrayList<Path> filesInWorkTxtTesseractDir, IdFileManager idOuterFmReport, IdXlsGlobReport xlsOuterSummaryReport) {
        xlsSummaryReport = xlsOuterSummaryReport;
        idinnerFmReport = idOuterFmReport;
        filesForReport = filesInWorkTxtTesseractDir;
        rowCount = 5;
        colCount = 7;
        excelFile = idinnerFmReport.getXlsReportFileName();
        wb = new XSSFWorkbook();
        sheet = wb.createSheet(sheetName);
        srcFileName = "";
        
        currentReportFolder = idinnerFmReport.getCurrentReportDir();
        dictonariesManager = new IdDictManager(idinnerFmReport);
    }
    
    protected void setSrcFileName(String srcOuterFileName){
        srcFileName = srcOuterFileName.substring(0,srcOuterFileName.indexOf("|||||"));
        Path pathSrcFile = Paths.get(srcFileName);
        String pathSrcFiletoString = pathSrcFile.getFileName().toString();
        XSSFRow row = sheet.createRow(rowCount);
        XSSFCell cell = row.createCell(colCount + 1);
        cell.setCellValue(pathSrcFiletoString);
        
        if( xlsSummaryReport != null ){
            ArrayList<String> srcRows = new ArrayList<String>();
            srcRows.add(pathSrcFiletoString);
            xlsSummaryReport.addRow(srcRows);
        }
        rowCount++;
                
    }
    protected void processFileFromList(){
        int countFiles = this.filesForReport.size();
        int iterationCount = 0;
        for (Path elementFile : this.filesForReport) {
            iterationCount++;
            String strFileName = elementFile.toString();
            
            ArrayList<String> lines = new ArrayList<String>();
            lines.add(strFileName);
            lines.add(NOT_DETECTED);
            try {
                lines.addAll(Files.readAllLines(elementFile, Charset.forName("UTF-8")));
            } catch (IOException ex) {
                ex.getMessage();
                ex.printStackTrace();
            }
            lines.add("|_|_|_|_|allLinesNotFilteredInFile|_|_|_|_|");
            lines.add(strFileName);
            ArrayList<String> linesFiltered = new ArrayList<String>();
            //
            Boolean lastFileFlag = Boolean.FALSE;
            if( countFiles == iterationCount ){
                lastFileFlag = Boolean.TRUE;
            }
            //for create dictonaries uncomment this string
            //dictonariesManager.putSplitLineAndPutToDictonaries(lines, lastFileFlag);
            
            
            //filter and write in this part of code
            linesFiltered.addAll(rowFilterNotAdaptive(lines));
            
            reportWriterXlsx(linesFiltered);
            //reportWriterXlsx(lines);
            System.out.println(strFileName + " row count " + linesFiltered.size());
            
        }
        saveXlsFile();
    }
    private List<String> rowFilterNotAdaptive(List<String> linesOuter){
        
        int catchedAktOther = 0;
        int catchedCount = 0;
        int percentABK = 0;
        int percentAOCP = 0;
        int percentAOCP1 = 0;
        int percentAOCP2 = 0;
        int linesCount = 0;
        List<String> strFiltered = new ArrayList<>();
        for (String strForAdd : linesOuter) {
            linesCount++;
            String stringForFilter = new String(strForAdd.toLowerCase().getBytes());
            if( !stringForFilter.isEmpty() ){
                
                //AOCP - 1 str
                strFiltered.add(strForAdd);
                if( stringForFilter.contains("приложение") ){
                    percentAOCP++;
                    
                    catchedCount++;
                }
                if( stringForFilter.contains("акт") ){
                    percentAOCP++;
                    
                    catchedCount++;
                }
                if( stringForFilter.contains("освидетельствования") ){
                    percentAOCP++;
                    
                    catchedCount++;
                }
                if( stringForFilter.contains("скрытых") ){
                    percentAOCP++;
                    
                    catchedCount++;
                }
                if( stringForFilter.contains("работ") ){
                    percentAOCP++;
                    
                    catchedCount++;
                }
                if( stringForFilter.contains("представитель") ){
                    percentAOCP++;
                    
                    catchedCount++;
                }
                if( stringForFilter.contains("застройщика") ){
                    percentAOCP++;
                    
                    catchedCount++;
                }
                if( stringForFilter.contains("или") ){
                    percentAOCP++;
                    
                    catchedCount++;
                }
                if( stringForFilter.contains("заказчика") ){
                    percentAOCP++;
                    
                    catchedCount++;
                }
                if( stringForFilter.contains("вопросам") ){
                    percentAOCP++;
                    
                    catchedCount++;
                }
                if( stringForFilter.contains("строительного") ){
                    percentAOCP++;
                    
                    catchedCount++;
                }
                if( stringForFilter.contains("контроля") ){
                    percentAOCP++;
                    
                    catchedCount++;
                }
                //AOCP - 2 str
                if( stringForFilter.contains("составлен") ){
                    percentAOCP++;
                    
                    catchedCount++;
                }
                if( stringForFilter.contains("экземплярах") ){
                    percentAOCP++;
                    
                    catchedCount++;
                }
                if( stringForFilter.contains("сведения") ){
                    percentAOCP++;
                    
                    catchedCount++;
                }
                if( stringForFilter.contains("дополнительные") ){
                    percentAOCP++;
                    
                    catchedCount++;
                }
                if( stringForFilter.contains("разрешается") ){
                    percentAOCP++;
                    
                    catchedCount++;
                }
                if( stringForFilter.contains("предъявлены") ){
                    percentAOCP++;
                    
                    catchedCount++;
                }
                if( stringForFilter.contains("документы") ){
                    percentAOCP++;
                    
                    catchedCount++;
                }
                if( stringForFilter.contains("подтверждающие") ){
                    percentAOCP++;
                    
                    catchedCount++;
                }
                if( stringForFilter.contains("соответствие") ){
                    percentAOCP++;
                    
                    catchedCount++;
                }
                if( stringForFilter.contains("предъявляемым") ){
                    percentAOCP++;
                    
                    catchedCount++;
                }
                if( stringForFilter.contains("требованиям") ){
                    percentAOCP++;
                   
                    catchedCount++;
                }
                //ABK
                if( stringForFilter.contains("результатах") ){
                    percentABK++;
                    catchedCount++;
                }
                if( stringForFilter.contains("проверки") ){
                    percentABK++;
                    catchedCount++;
                }
                if( stringForFilter.contains("изделий") ){
                    percentABK++;
                    catchedCount++;
                }
                if( stringForFilter.contains("вид") ){
                    percentABK++;
                    catchedCount++;
                }
                if( stringForFilter.contains("соответствие") ){
                    percentABK++;
                    catchedCount++;
                }
                if( stringForFilter.contains("техдокументации") ){
                    percentABK++;
                    catchedCount++;
                }
                if( stringForFilter.contains("сплошной") ){
                    percentABK++;
                    catchedCount++;
                }
                if( stringForFilter.contains("выборочный") ){
                    percentABK++;
                    catchedCount++;
                }
                if( stringForFilter.contains("своим") ){
                    percentABK++;
                    catchedCount++;
                }
                if( stringForFilter.contains("геометрическим") ){
                    percentABK++;
                    catchedCount++;
                }
                if( stringForFilter.contains("размерам") ){
                    percentABK++;
                    catchedCount++;
                }
                if( stringForFilter.contains("данным") ){
                    percentABK++;
                    catchedCount++;
                }
                if( stringForFilter.contains("сопроводительной") ){
                    percentABK++;
                    catchedCount++;
                }
                if( stringForFilter.contains("документации") ){
                    percentABK++;
                    catchedCount++;
                }
                if( stringForFilter.contains("характеристики") ){
                    percentABK++;
                    catchedCount++;
                }
                if( stringForFilter.contains("механических") ){
                    percentABK++;
                    catchedCount++;
                }
                if( stringForFilter.contains("свойств") ){
                    percentABK++;
                    catchedCount++;
                }
                if( stringForFilter.contains("предназначенных") ){
                    percentABK++;
                    catchedCount++;
                }
                if( stringForFilter.contains("проектом") ){
                    percentABK++;
                    catchedCount++;
                }
                
                
            }
            if( catchedCount > 0 ){
                
                if( (percentABK/catchedCount) > (percentAOCP/catchedCount) ){
                    strFiltered.set(1, ABK_DETECTED);
                }
                if( (percentABK/catchedCount) < (percentAOCP/catchedCount) ){
                    strFiltered.set(1, AOCP_DETECTED);
                }
            }
            else{
                if( stringForFilter.contains("акт") ){
                    strFiltered.set(1, AKT_OTHER);
                }
                if( stringForFilter.contains("схема") ){
                    strFiltered.set(1, SCHEME);
                }
                if( stringForFilter.contains("протокол") ){
                    strFiltered.set(1, PROTOKOL);
                }
                if( stringForFilter.contains("результат") ){
                    strFiltered.set(1, REZULT);
                }
                if( stringForFilter.contains("сертификат") ){
                    strFiltered.set(1, SERTIFICAT);
                }
                if( stringForFilter.contains("паспорт") ){
                    strFiltered.set(1, PASPORT);
                }
                
            }
        }
        return strFiltered;
    }
    private List<String> rowFilter(List<String> linesOuter){
        List<String> strFiltered = new ArrayList<>();
        for (String strForAdd : linesOuter) {
            String stringForFilter = new String(strForAdd.toLowerCase().getBytes());
            if( !stringForFilter.isEmpty() ){
                //part rules for decline strings
                if( stringForFilter.contains("фамилия") ){
                    continue;
                }
                //part rules for accept strings
                if( stringForFilter.contains("акт") ){
                    strFiltered.add(strForAdd);
                    continue;
                }
                if( stringForFilter.contains("№") ){
                    strFiltered.add(strForAdd);
                    continue;
                }
                if( stringForFilter.contains("дата") ){
                    strFiltered.add(strForAdd);
                    continue;
                }
                if( stringForFilter.contains("работ") ){
                    strFiltered.add(strForAdd);
                    continue;
                }
                if( stringForFilter.contains("освидетельствования") ){
                    strFiltered.add(strForAdd);
                    continue;
                }
                if( stringForFilter.contains("монтаж") ){
                    strFiltered.add(strForAdd);
                    continue;
                }
                if( stringForFilter.contains("погружение") ){
                    strFiltered.add(strForAdd);
                    continue;
                }
                if( stringForFilter.contains("забивка") ){
                    strFiltered.add(strForAdd);
                    continue;
                }
                if( stringForFilter.contains("нанесение") ){
                    strFiltered.add(strForAdd);
                    continue;
                }
                if( stringForFilter.contains("грунтовка") ){
                    strFiltered.add(strForAdd);
                    continue;
                }
                if( stringForFilter.contains("скрытых") ){
                    strFiltered.add(strForAdd);
                    continue;
                }
                if( stringForFilter.contains("проекту") ){
                    strFiltered.add(strForAdd);
                    continue;
                }
                if( stringForFilter.contains("паспорт") ){
                    strFiltered.add(strForAdd);
                    continue;
                }
                if( stringForFilter.contains("сертификат") ){
                    strFiltered.add(strForAdd);
                    continue;
                }
                if( stringForFilter.contains("схема") ){
                    strFiltered.add(strForAdd);
                    continue;
                }
                if( stringForFilter.contains("труба") ){
                    strFiltered.add(strForAdd);
                    continue;
                }
                if( stringForFilter.contains("результат") ){
                    strFiltered.add(strForAdd);
                    continue;
                }
                if( stringForFilter.contains("свая") ){
                    strFiltered.add(strForAdd);
                    continue;
                }
                if( stringForFilter.contains("балка") ){
                    strFiltered.add(strForAdd);
                    continue;
                }
                if( stringForFilter.contains("траверса") ){
                    strFiltered.add(strForAdd);
                    continue;
                }
                if( stringForFilter.contains("отвод") ){
                    strFiltered.add(strForAdd);
                    continue;
                }
                if( stringForFilter.contains("переход") ){
                    strFiltered.add(strForAdd);
                    continue;
                }
                if( stringForFilter.contains("тройник") ){
                    strFiltered.add(strForAdd);
                    continue;
                }
                if( stringForFilter.contains("смесь") ){
                    strFiltered.add(strForAdd);
                    continue;
                }
                if( stringForFilter.contains("январ") ){
                    strFiltered.add(strForAdd);
                    continue;
                }
                if( stringForFilter.contains("феврал") ){
                    strFiltered.add(strForAdd);
                    continue;
                }
                if( stringForFilter.contains("март") ){
                    strFiltered.add(strForAdd);
                    continue;
                }
                if( stringForFilter.contains("апрел") ){
                    strFiltered.add(strForAdd);
                    continue;
                }
                if( stringForFilter.contains("май") ){
                    strFiltered.add(strForAdd);
                    continue;
                }
                if( stringForFilter.contains("июн") ){
                    strFiltered.add(strForAdd);
                    continue;
                }
                if( stringForFilter.contains("июл") ){
                    strFiltered.add(strForAdd);
                    continue;
                }
                if( stringForFilter.contains("август") ){
                    strFiltered.add(strForAdd);
                    continue;
                }
                if( stringForFilter.contains("сентябр") ){
                    strFiltered.add(strForAdd);
                    continue;
                }
                if( stringForFilter.contains("октябр") ){
                    strFiltered.add(strForAdd);
                    continue;
                }
                if( stringForFilter.contains("ноябр") ){
                    strFiltered.add(strForAdd);
                    continue;
                }
                if( stringForFilter.contains("декабр") ){
                    strFiltered.add(strForAdd);
                    continue;
                }
                if( stringForFilter.contains("номер") ){
                    strFiltered.add(strForAdd);
                    continue;
                }
                
                if( stringForFilter.contains("протокол") ){
                    strFiltered.add(strForAdd);
                    continue;
                }
                if( stringForFilter.contains("filename") ){
                    strFiltered.add(strForAdd);
                    continue;
                }
            
            }
        }
        return strFiltered;
    }

    protected void reportWriterXlsx(List<String> linesOuter){
        if( xlsSummaryReport != null ){
            xlsSummaryReport.addRow(linesOuter);
        }
        String fileName = "";
        rowCount++;
        XSSFRow row = sheet.createRow(rowCount);
        int colForWrite = colCount;
        
        for (String stringToXlsxCell : linesOuter) {
            colForWrite++;
            
            
            XSSFCell cell = row.createCell(colForWrite);
            cell.setCellValue(stringToXlsxCell);
            
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
                    
                }
                rowCount = 5;
                excelFile = idinnerFmReport.getXlsReportFileName();
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
                
                
            }
        }
    }
    
    private void saveXlsFile(){
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
