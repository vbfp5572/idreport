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
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author VB
 */
public class IdXlsxReportFilesSummator {
    
    private ArrayList<Path> filesReport;

    public IdXlsxReportFilesSummator(ArrayList<Path> dirReportXls) {
        filesReport = dirReportXls;
    }
    
    protected void processReportSum(){
        for (Path pathXlsx : filesReport) {
            readAndWriteXlsx(pathXlsx);
        }
        
        
    }
    protected void readAndWriteXlsx(Path fileForRead){
        try (FileInputStream fInputStream = new FileInputStream(fileForRead.toFile())){
            XSSFWorkbook book = new XSSFWorkbook(fInputStream);
            XSSFSheet sheet = book.getSheetAt(0);

            Iterator<Row> itr = sheet.iterator();

            // Iterating over Excel file in Java
            while (itr.hasNext()) {
                Row row = itr.next();

                // Iterating over each column of Excel file
                Iterator<Cell> cellIterator = row.cellIterator();
                while (cellIterator.hasNext()) {

                    Cell cell = cellIterator.next();

                    switch (cell.getCellType()) {
                    case Cell.CELL_TYPE_STRING:
                        System.out.print(cell.getStringCellValue() + "\t");
                        break;
                    case Cell.CELL_TYPE_NUMERIC:
                        System.out.print(cell.getNumericCellValue() + "\t");
                        break;
                    case Cell.CELL_TYPE_BOOLEAN:
                        System.out.print(cell.getBooleanCellValue() + "\t");
                        break;
                    default:

                    }
                }
                System.out.println("");
            }

            

        } catch (FileNotFoundException fe) {
            fe.printStackTrace();
        } catch (IOException ie) {
            ie.printStackTrace();
        }
    
    }
    /*private void saveXlsFile(Path excelFile){
        try (FileOutputStream fOutStream = new FileOutputStream(excelFile.toFile())){
            wb.write(fOutStream);
        } catch (FileNotFoundException ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
    }*/
}
