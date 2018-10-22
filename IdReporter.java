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
    private Integer rowCount;
    private Integer colCount;
    private Path excelFile;
    private static final int FILE_ROW_LIMIT = 100000;
    private String sheetName = "Reestr";
    private XSSFWorkbook wb;
    private XSSFSheet sheet;
    private IdFileManager idinnerFmReport;
    private Path currentReportFolder;
    
    
    private ArrayList<Path> filesForReport;
    public IdReporter(ArrayList<Path> filesInWorkTxtTesseractDir, IdFileManager idOuterFmReport) {
        idinnerFmReport = idOuterFmReport;
        filesForReport = filesInWorkTxtTesseractDir;
        rowCount = 0;
        colCount = 0;
        excelFile = idinnerFmReport.getXlsReportFileName();
        wb = new XSSFWorkbook();
        sheet = wb.createSheet(sheetName);
        
        
        currentReportFolder = idinnerFmReport.getCurrentReportDir();
    }
    
    protected void processFileFromList(){
        for (Path elementFile : this.filesForReport) {
            String strFileName = elementFile.toString();
            
            List<String> lines = new ArrayList<>();
            lines.add("filename" + strFileName);
            try {
                lines.addAll(Files.readAllLines(elementFile, Charset.forName("UTF-8")));
            } catch (IOException ex) {
                ex.getMessage();
                ex.printStackTrace();
            }
            lines.add("filename" + strFileName);
            List<String> linesFiltered = new ArrayList<>();
            
            linesFiltered.addAll(rowFilter(lines));
            reportWriterXlsx(linesFiltered);
            System.out.println(strFileName + " row count " + linesFiltered.size());
            
        }
        saveXlsFile();
    }
    private List<String> rowFilter(List<String> linesOuter){
        List<String> strFiltered = new ArrayList<>();
        for (String strForAdd : linesOuter) {
            String stringForFilter = new String(strForAdd.toLowerCase().getBytes());
            if( !stringForFilter.isEmpty() ){
                if( stringForFilter.contains("фамилия") ){
                    continue;
                }
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
        for (String stringToXlsxCell : linesOuter) {
            
            rowCount++;
            XSSFRow row = sheet.createRow(rowCount);
            XSSFCell cell = row.createCell(3);
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
                rowCount = 1;
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
