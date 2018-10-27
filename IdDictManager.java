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

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 *
 * @author VB
 */
public class IdDictManager {
    
    private IdDictFileManager dictFileManager;
    private IdFileManager idInnerFmReport;
    private Path currentReportHtmlDir;
    
    


    public IdDictManager(IdFileManager idOuterFmReport) {
        idInnerFmReport = idOuterFmReport;
        dictFileManager = new IdDictFileManager(idOuterFmReport);
        currentReportHtmlDir = idInnerFmReport.getDirDictonariesHtml();
    }
    protected void buildHtmlImagesTextFromOCRFolders(){
        storageWalker();
    }
    
    protected void storageWalker(){
        Integer sizeStoragesList = idInnerFmReport.getSizeStoragesList();
        //idInnerFmReport.
        //for (int i = 0; i < 2; i++) {
        for (int i = 0; i < sizeStoragesList; i++) {
            System.out.println("Dictonaries Manager Current storage:");
            System.out.println(idInnerFmReport.getCurrentStorage().toString());
            String currentStorageString = idInnerFmReport.getCurrentStorage().toString();
        
            ArrayList<String> processStLines = new ArrayList<String>();
            processStLines.addAll(idInnerFmReport.getProcessStContent());
            String readedSrcString = processStLines.get(0);
            
            ArrayList<Path> forTextFiles = new ArrayList<Path>();
            forTextFiles.addAll(idInnerFmReport.getTextFilesFromCurrentStorage());

            ArrayList<Path> forImagesFiles = new ArrayList<Path>();
            forImagesFiles.addAll(idInnerFmReport.getImagesFilesFromCurrentStorage());
            buildSumFileReport(currentStorageString,
                    readedSrcString,
                    forTextFiles,
                    forImagesFiles);
            idInnerFmReport.setNextCurrentStorage();
        }
    }
    protected void buildSumFileReport(String currentStorageStringOuter,
            String readedSrcStringOuter,
            ArrayList<Path> forTextFilesOuter, 
            ArrayList<Path> forImagesFilesOuter){
        String newProcessId = idInnerFmReport.getNewProcessId();
        Path inDirDictonariesHtmlFile = idInnerFmReport.getInDirDictonariesHtmlFile(newProcessId);
        Path inDirDictonariesHtmlJsFile = idInnerFmReport.getInDirDictonariesHtmlJsDirFile(newProcessId);
        Path inDirDictonariesHtmlCssFile = idInnerFmReport.getInDirDictonariesHtmlCssDirFile(newProcessId);
        ArrayList<String> linesToReportFile = new ArrayList<String>();
        ArrayList<String> linesToCssFile = new ArrayList<String>();
        ArrayList<String> linesToJsFile = new ArrayList<String>();
        
        String forBuildHead = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\"  \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">";
        linesToReportFile.add(forBuildHead);
        forBuildHead = "<html xmlns=\"http://www.w3.org/1999/xhtml\">";
        linesToReportFile.add(forBuildHead);
        forBuildHead = "<head>";
        linesToReportFile.add(forBuildHead);
        forBuildHead = "<title>HTML report OCR files</title>";
        linesToReportFile.add(forBuildHead);
        forBuildHead = "<script type=\"text/javascript\" src=\"../../../forconvert/js-lib/jquery-3.3.1.js\"></script>";
        linesToReportFile.add(forBuildHead);
        forBuildHead = "<link rel=\"stylesheet\" type=\"text/css\" href=\"" + inDirDictonariesHtmlCssFile.toString() + "\">";
        linesToReportFile.add(forBuildHead);
        forBuildHead = "<script type=\"text/javascript\" src=\"" + inDirDictonariesHtmlJsFile.toString() + "\"></script>";
        linesToReportFile.add(forBuildHead);
        forBuildHead = "</head>";
        linesToReportFile.add(forBuildHead);
        forBuildHead = "<body>";
        linesToReportFile.add(forBuildHead);
        forBuildHead = "<table>";
        linesToReportFile.add(forBuildHead);
        forBuildHead = "<tr><th>Images</th><th>OCR-Text</th></tr>";
        linesToReportFile.add(forBuildHead);
        forBuildHead = "<tr><td>" 
                + currentStorageStringOuter 
                + "</td><td>" 
                + readedSrcStringOuter 
                + "</td></tr>";
        linesToReportFile.add(forBuildHead);
        String imgPrefix = "img_el";
        String textPreFix = "txt_ocr_el";
        if( forImagesFilesOuter.size() == forTextFilesOuter.size() ){
            int idxFile = 0;
            for (Path imagesFile : forImagesFilesOuter) {
                String toStringImagesFile = imagesFile.toString();
                Path fileNameImagesFile = imagesFile.getFileName();
                String toStringTextFiles = forTextFilesOuter.get(idxFile).toString();
                Path fileNameTextFiles = forTextFilesOuter.get(idxFile).getFileName();
                String forBuild = "";
                forBuild = "<tr><td>"
                        + "<input type=\"button\" id=\"but" + imgPrefix + idxFile + "\" value=\"" + fileNameImagesFile.toString() + "\" onclick=\"hideShowDiv" + imgPrefix + idxFile + "();\">"
                        + "</td>"
                        + "<td><div id=\"id-src-" + imgPrefix + "_num" + idxFile + "\">" 
                        + "<img src=\"" + toStringImagesFile + "\" width=\"70%\" height=\"70%\" style=\"padding:0px 2px;border:1px solid black\">" 
                        + "</div></td><td>" 
                        + "<div id=\"id-src-" + textPreFix + "_num" + idxFile + "\"><iframe src=\"" + toStringTextFiles + "\" width=\"100%\" height=\"100%\">" + forTextFilesOuter.get(idxFile).toString() + "</iframe>" 
                        + "</div></td><td>"
                        + "<input type=\"button\" id=\"but" + textPreFix + idxFile + "\" value=\"" + fileNameTextFiles.toString() + "\" onclick=\"hideShowDiv" + textPreFix + idxFile + "();\">"
                        + "</td></tr>";
                
                linesToReportFile.add(forBuild);
                linesToCssFile.add(getCssStructText(imgPrefix,idxFile));
                linesToJsFile.add(getJsText(imgPrefix,idxFile));
                linesToJsFile.add(getJsText(textPreFix,idxFile));
                idxFile++;
            }
        }
        forBuildHead = "</table>";
        linesToReportFile.add(forBuildHead);
        forBuildHead = "</body>";
        linesToReportFile.add(forBuildHead);
        forBuildHead = "</html>";
        linesToReportFile.add(forBuildHead);
        
        IdFileManager.writeLinesToFile(inDirDictonariesHtmlFile.toString(), linesToReportFile);
        IdFileManager.writeLinesToFile(inDirDictonariesHtmlJsFile.toString(), linesToJsFile);
        IdFileManager.writeLinesToFile(inDirDictonariesHtmlCssFile.toString(), linesToCssFile);
        //idInnerFmReport.
        
    }
    private String getCssStructText(String preFix, Integer idDivContainer){
        //In generation function append before this string #div_id
        return "#id-src-" + preFix + "_num" + idDivContainer + "{\n" +
            "    width:70%;\n" +
            "    height:70%;\n" +
            "    background:#666699;\n" +
            "    border:1px solid #333366;\n" +
            "    margin-bottom:20px;\n" +
            "    display:none;\n" +
            "}";
    }
    private String getJsText(String preFix, Integer idDivContainer){
        return "function hideShowDiv" + preFix + idDivContainer + "(){\n" +
            "      $(\"#id-src-" + preFix + "_num" + idDivContainer + "\").toggle(\"slow\");\n" +
            "}";
    }
    
    protected void buildDictonariesByRunnable(){
        
    }
    private void putSplitedLinesIntoFoldersByRunnable(ArrayList<String> linesFromFile, Boolean lastFileOuterFlag){
        CopyOnWriteArrayList<String> linesReadedFromTextFiles = new CopyOnWriteArrayList<String>();
        linesReadedFromTextFiles.addAll(linesFromFile);
        if( (linesReadedFromTextFiles.size() > 1000) || lastFileOuterFlag ){
            IdExStrSplitter spliterExec = new IdExStrSplitter(linesReadedFromTextFiles,
            idInnerFmReport);
            spliterExec.start();
        }
    }
    protected void putSplitLineAndPutToDictonaries(ArrayList<String> linesFromFile, Boolean lastFileOuterFlag){
        Path checkDirForFileName = dictFileManager.getCheckDirForFileName();
        ArrayList<String> fileLines = new ArrayList<String>();
        for (String linesReadedFromTextFile : linesFromFile) {
            String[] wordFromFileReadedLine = linesReadedFromTextFile.split(" ");
            for (String stringToAdd : wordFromFileReadedLine) {
                if( !stringToAdd.isEmpty() ){
                    fileLines.add(stringToAdd);
                }
            }
        }
        putLinesToFile(checkDirForFileName, fileLines);
        checkDirForFileName = setLockAndGetNewName(checkDirForFileName);
    }
    private void putLinesToFile(Path writedFile, ArrayList<String> lines){
            try {
                Files.write(writedFile, lines, Charset.forName("UTF-8"));
                System.out.println("Dictonaries writer in file " + writedFile.toString() + " put lines count " + lines.size());
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
                ex.printStackTrace();
            }
    }
    private Path setLockAndGetNewName(Path inputFileName){
        String replacedPath = inputFileName.toString().replace(
                        dictFileManager.getDefinedFileExtention(), dictFileManager.getDefinedFileLockExtention());
        Path lockedFilePath = Paths.get(replacedPath);
        System.out.println("[GETFORLOCK]In file " + lockedFilePath.toString());
        try{
            if( Files.notExists(lockedFilePath) ){
                Files.createFile(lockedFilePath);
            }
            System.out.println("[CREATELOCK]In file " + lockedFilePath.toString());
        } catch (IOException ex) {
            System.out.println("[ERROR]Cant create lock file " + lockedFilePath.toString()
                    + ex.getMessage());
            ex.printStackTrace();
        }
        return dictFileManager.getDictonariesUnfilteredDirDeclineNewFile();
    }
}
