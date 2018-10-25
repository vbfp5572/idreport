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

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 *
 * @author VB
 */
public class IdReport {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        runProcessForCreateXlsReestr();
        //runProcessForCreateXlsReestrOnlyTwoStorages();
        
    }
    private static void oldCodeFor(){
    /*ArrayList<Path> dirReportXls = new ArrayList<Path> ();
        dirReportXls.addAll(idFmReport.getXlsxFilesFromReportDir());
        System.out.println("Files *.xlsx count: " + dirReportXls.size());
        IdXlsxReportFilesSummator reportSummator = new IdXlsxReportFilesSummator(dirReportXls);
        reportSummator.processReportSum();*/
    }
    
    
    private static void runProcessForCreateXlsReestrOnlyTwoStorages(){
        // TODO code application logic here
        IdFileManager idFmReport = new IdFileManager();
        //idFmReport.printStoragesList();
        
        Integer sizeStoragesList = idFmReport.getSizeStoragesList();
        
        
        IdXlsGlobReport xlsSummaryReport = new IdXlsGlobReport(idFmReport);
        for (int i = 0; i < 2; i++) {
        //for (int i = 0; i < sizeStoragesList; i++) {
            System.out.println("Current storage:");
            System.out.println(idFmReport.getCurrentStorage().toString());
            
            ArrayList<String> processStLines = new ArrayList<String>();
            processStLines.addAll(idFmReport.getProcessStContent());
            String readedSrcString = processStLines.get(0);
            
            ArrayList<Path> forTextFiles = new ArrayList<Path>();
            forTextFiles.addAll(idFmReport.getTextFilesFromCurrentStorage());


            ArrayList<Path> forImagesFiles = new ArrayList<Path>();
            forImagesFiles.addAll(idFmReport.getImagesFilesFromCurrentStorage());
            if( forImagesFiles.size() == forTextFiles.size() ){
                System.out.println("Files count in text and images storage: " + forTextFiles.size());
            }
            else{
                System.out.println("Files count in text: " + forTextFiles.size()
                        + ", images: " + forImagesFiles.size()
                        + ", count of files is wrong, choise next storage");
                idFmReport.setNextCurrentStorage();
                //@todo in iterations use here continue;
            }
            IdTextFileFilter filesFilter = new IdTextFileFilter(forImagesFiles,forTextFiles,idFmReport);
            IdReporter reporterToXls = new IdReporter(forTextFiles,idFmReport,xlsSummaryReport);
            reporterToXls.setSrcFileName(readedSrcString);
            reporterToXls.processFileFromList();
            forImagesFiles.clear();
            forTextFiles.clear();
            idFmReport.setNextCurrentStorage();
        }
        xlsSummaryReport.saveXlsFile();
    }
    private static void runProcessForCreateXlsReestr(){
        // TODO code application logic here
        IdFileManager idFmReport = new IdFileManager();
        //idFmReport.printStoragesList();
        
        Integer sizeStoragesList = idFmReport.getSizeStoragesList();
        
        
        IdXlsGlobReport xlsSummaryReport = new IdXlsGlobReport(idFmReport);
        
        for (int i = 0; i < sizeStoragesList; i++) {
            System.out.println("Current storage:");
            System.out.println(idFmReport.getCurrentStorage().toString());
            
            ArrayList<String> processStLines = new ArrayList<String>();
            processStLines.addAll(idFmReport.getProcessStContent());
            String readedSrcString = processStLines.get(0);
            
            ArrayList<Path> forTextFiles = new ArrayList<Path>();
            forTextFiles.addAll(idFmReport.getTextFilesFromCurrentStorage());


            ArrayList<Path> forImagesFiles = new ArrayList<Path>();
            forImagesFiles.addAll(idFmReport.getImagesFilesFromCurrentStorage());
            if( forImagesFiles.size() == forTextFiles.size() ){
                System.out.println("Files count in text and images storage: " + forTextFiles.size());
            }
            else{
                System.out.println("Files count in text: " + forTextFiles.size()
                        + ", images: " + forImagesFiles.size()
                        + ", count of files is wrong, choise next storage");
                idFmReport.setNextCurrentStorage();
                //@todo in iterations use here continue;
            }
            IdTextFileFilter filesFilter = new IdTextFileFilter(forImagesFiles,forTextFiles,idFmReport);
            IdReporter reporterToXls = new IdReporter(forTextFiles,idFmReport,xlsSummaryReport);
            reporterToXls.setSrcFileName(readedSrcString);
            reporterToXls.processFileFromList();
            forImagesFiles.clear();
            forTextFiles.clear();
            idFmReport.setNextCurrentStorage();
        }
        xlsSummaryReport.saveXlsFile();
    }
    
}
