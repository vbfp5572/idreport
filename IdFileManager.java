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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import static java.nio.file.StandardCopyOption.COPY_ATTRIBUTES;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author VB
 */
public class IdFileManager {
    private static final String PATH_ROOT = "D:/";
    private static final String REPORT_POSFIX = "-id-sum-rep";
    private static final String HTML = "html";
    private static final String HTML_IMAGES_OCR_TEXT_REPORT = "html-ocr-img-txt-report";
    private static final String XLS_REPORTS = "xlsx";
    private static final String DIR_DICTONARIES = "dict";
    private static final String DIR_DICTONARIES_UNFILTERED = "dict-unf";
    private static final String DIR_DICTONARIES_WORD = "dict-word";
    private static final String FILE_ALL_UNFILTERED_WORD = "dict-unf.txt";
    private static final String FILE_ALL_UNFILTERED_STATS = "stat.txt";
    private static final String FIELD_SEPARATOR = "|_|_|_|_|";
    private static final String IS_COMPILED_REPORT = "-compiled.st";
    private static final String XLSX_REPORT_NAME = "-id-reestr.xlsx";
    private static final String HTML_FILES_REPORT_NAME = "-id-sum-file-report.html";
    private static final String HTML_FILES_REPORT_JS = "js";
    private static final String HTML_FILES_REPORT_CSS = "css";
    private static final String CSS_EXT = ".css";
    private static final String JS_EXT = ".js";
    
    private static final String DIR_DOC_OBJ_TYPE = "doc-obj-type";
    
    private static final String DIR_DOC_TYPE_AOCP = "AOCP";
    private static final String DIR_DOC_TYPE_AOOK = "AOOK";
    private static final String DIR_DOC_TYPE_ABK = "ABK";
    private static final String DIR_DOC_TYPE_AKTDONEAKZ = "AKTDONEAKZ";
    private static final String DIR_DOC_TYPE_AKTRASTKOMPENS = "AKTRASTKOMPENS";
    private static final String DIR_DOC_TYPE_AKTISPTRUB = "AKTISPTRUB";
    private static final String DIR_DOC_TYPE_SERT = "SERT";
    private static final String DIR_DOC_TYPE_PASP = "PASP";
    private static final String DIR_DOC_TYPE_BETONPROTOK = "BETONPROTOK";
    private static final String DIR_DOC_TYPE_BETONRESULTISP = "BETONRESULTISP";
    private static final String DIR_DOC_TYPE_BETONRECEPT = "BETONRECEPT";
    private static final String DIR_DOC_TYPE_ISPSHEMA = "ISPSHEMA";
    private static final String DIR_DOC_TYPE_VIK = "VIK";
    private static final String DIR_DOC_TYPE_UZK = "UZK";
    private static final String DIR_DOC_TYPE_KS2 = "KS2";
    private static final String DIR_DOC_TYPE_KS6 = "KS6";
    
    private static final String DIR_DOC_TYPE_JOURNAL = "JOURNAL";
    private static final String DIR_DOC_TYPE_JOURNALAKZ = "JOURNALAKZ";
    private static final String DIR_DOC_TYPE_JOURNALBETON = "JOURNALBETON";
    private static final String DIR_DOC_TYPE_JOURNALBUR = "JOURNALBUR";
    private static final String DIR_DOC_TYPE_JOURNALPOGR = "JOURNALPOGR";
    private static final String DIR_DOC_TYPE_JOURNALSVARKI = "JOURNALSVARKI";
    private static final String DIR_DOC_TYPE_JOURNALMONTAJKONSTR = "JOURNALMONTAJKONSTR";
    private static final String DIR_DOC_TYPE_JOURNALSVARKITRUB = "JOURNALSVARKITRUB";
    private static final String DIR_DOC_TYPE_JOURNALSOBSCHII = "JOURNALOBSCHII";
    
    private static final String DIR_DOC_TYPE_UNDEF = "UNDEF";
    
    
    
    private Path currentReportFolder;
    private Path currentReportHtmlFolder;
    
    private static final String PDF_DIR = "pdf";
    private static final String PDF_RENAMED_DIR = "pdf-renamed";
    private static final String JPG_DIR = "jpg";
    private static final String TXT_TESS_DIR = "txt-tess";
    private static final String TXT_LINGVO_DIR = "txt-lingvo";
    private static final String XLS_DIR = "xls-tess";
    private static final String XLS_LINGVO_DIR = "xls-lingvo";
    private static final String XLS_VSN_DIR = "xls-vsn";
    private static final String XLS_REPORT_DIR = "report-xls";
    private static final String PDF_REPORT_DIR = "report-pdf";
    private static final String IS_PROCESSED = "01process.st";
    private static final String IS_PDF_RENAMED = "02pdfrenamed.st";
    private static final String IS_PDF_IMAGES_EXP = "03pdfimagesexp.st";
    private static final String IS_OCR_IMAGES = "04ocrimages.st";
    private static final String IS_TXT_TO_XLS = "05txttoxls.st";
    
    private ArrayList<Path> workFolders;
    private ArrayList<Path> storagesList;
    private Integer storageIndex;
    private Integer countStorageIteration;

    public IdFileManager() {
        countStorageIteration = 0;
        storageIndex = -1;
        workFolders = new ArrayList<Path>();
        storagesList = new ArrayList<Path>();
        listDirInRoot();
        getStoragesFromWorkFolders();
        if( !storagesList.isEmpty() ){
            storageIndex = 0;
        }
        listReportDirInRoot();
        checkOrCreateSubWorkDir(HTML);
        checkOrCreateSubWorkDir(XLS_REPORTS);
        checkOrCreateSubWorkDir(DIR_DICTONARIES);
        checkOrCreateSubWorkDir(DIR_DICTONARIES_UNFILTERED);
        checkOrCreateSubWorkDir(DIR_DICTONARIES_WORD);
        
    }
    protected List<String> getProcessStContent(){
        Path currentStorage = getCurrentStorage();
        Path processSt = Paths.get(currentStorage.toString(),IS_PROCESSED);
        ArrayList<String> readLinesFromFile = new ArrayList<String>();
        readLinesFromFile.addAll(readLinesFromFile(processSt));
        return readLinesFromFile;
    }
    protected Path getDirDictonariesHtml(){
        return checkOrCreateSubWorkDir(HTML);
    }
    protected Path getDirDictonariesHtmlImgTextReport(){
        return checkOrCreateSubWorkDir(HTML_IMAGES_OCR_TEXT_REPORT);
    }
    protected Path getDirDictonariesHtmlSubImgTextReport(){
        String newProcessId = getNewProcessId();
        currentReportHtmlFolder = checkOrCreateSubHtmlReportDir(newProcessId);
        return currentReportHtmlFolder;
    }
    protected static void writeLinesToFile(String strCfgPath, ArrayList<String> strTextRemark){
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(strCfgPath)))
        {
            for(String itemStr : strTextRemark){
                String text = itemStr.toString();
                
                bw.write(text);
                bw.newLine();
            }
        }
        catch(IOException ex){
             ex.printStackTrace();
            System.out.println("[ERROR] Can`t read count files in work directory " + strCfgPath
            + " " + ex.getMessage());
        }
    }
    /**
     * //@todo make function for create sub folder for report folder for build this part of report
     * @return 
     */
    protected Path getInDirDictonariesHtmlFile(String processIdForNow){
        try{
            pathIsNotDirectory(currentReportHtmlFolder);
            pathIsNotReadWriteLink(currentReportHtmlFolder);
            System.out.println("[DIR_EXIST] html report directory path " + currentReportHtmlFolder.toString());
        } catch (IOException ex) {
            System.out.println("[ERROR]Can`t create new file, directory not exist or not have permissions " + currentReportHtmlFolder.toString()
                    + ex.getMessage());
            ex.printStackTrace();
        }
        Path dirDictonariesHtml = currentReportHtmlFolder;
        
        Path currentHtmlReportFile = Paths.get(dirDictonariesHtml.toString(), processIdForNow + HTML_FILES_REPORT_NAME);
        currentHtmlReportFile = createFile(currentHtmlReportFile);
        return currentHtmlReportFile;
    }
    protected Path getInDirDictonariesHtmlJsDirFile(String processIdForNow){
        Path cssFile = Paths.get(PATH_ROOT,  processIdForNow + "not-true-create.js");;
        try{
            Path subDirPath = Paths.get(currentReportHtmlFolder.toString());
            pathIsNotDirectory(subDirPath);
            pathIsNotReadWriteLink(subDirPath);
            subDirPath = Paths.get(subDirPath.toString(), HTML_FILES_REPORT_JS);
            if( Files.notExists(subDirPath) ){
                Files.createDirectory(subDirPath);
            }
            pathIsNotDirectory(subDirPath);
            pathIsNotReadWriteLink(subDirPath);
            cssFile = Paths.get(subDirPath.toString(), processIdForNow + JS_EXT);
            
            System.out.println("[DIR_EXIST] html report css file path " + currentReportHtmlFolder.toString());
        } catch (IOException ex) {
            System.out.println("[ERROR]Can`t create new file, directory not exist or not have permissions " + currentReportHtmlFolder.toString()
                    + ex.getMessage());
            ex.printStackTrace();
        }
        
        return createFile(cssFile);
    }
    protected Path getInDirDictonariesHtmlCssDirFile(String processIdForNow){
        Path cssFile = Paths.get(PATH_ROOT, processIdForNow + "not-true-create.css");;
        try{
            Path subDirPath = Paths.get(currentReportHtmlFolder.toString());
            pathIsNotDirectory(subDirPath);
            pathIsNotReadWriteLink(subDirPath);
            subDirPath = Paths.get(subDirPath.toString(), HTML_FILES_REPORT_CSS);
            if( Files.notExists(subDirPath) ){
                Files.createDirectory(subDirPath);
            }
            pathIsNotDirectory(subDirPath);
            pathIsNotReadWriteLink(subDirPath);
            cssFile = Paths.get(subDirPath.toString(), processIdForNow + CSS_EXT);
            
            System.out.println("[DIR_EXIST] html report css file path " + currentReportHtmlFolder.toString());
        } catch (IOException ex) {
            System.out.println("[ERROR]Can`t create new file, directory not exist or not have permissions " + currentReportHtmlFolder.toString()
                    + ex.getMessage());
            ex.printStackTrace();
        }
        
        return createFile(cssFile);
    }
    protected Path createFile(Path creationFilePath){
        try{
            if( Files.notExists(creationFilePath) ){
                Files.createFile(creationFilePath);
            }
            pathIsNotFile(creationFilePath);
            pathIsNotReadWriteLink(creationFilePath);
            System.out.println("[CREATE_NEW_FILE] file path " + creationFilePath.toString());
        } catch (IOException ex) {
            System.out.println("[ERROR]Can`t create new file " + creationFilePath.toString()
                    + ex.getMessage());
            ex.printStackTrace();
        }
        return creationFilePath;
    }
    protected Path getDirDictonariesWord(){
       return checkOrCreateSubWorkDir(DIR_DICTONARIES_WORD);
    }
    
    protected Path getDirDictonaries(){
       return checkOrCreateSubWorkDir(DIR_DICTONARIES);
    }
    
    protected Path getDirDictonariesUnfiltered(){
       return checkOrCreateSubWorkDir(DIR_DICTONARIES_UNFILTERED);
    }
    
    protected Path getDirReportXls(){
       return checkOrCreateSubWorkDir(XLS_REPORTS);
    }
    
    protected Path getCurrentStorage(){
        if( storageIndex < 0 ){
            throw new IllegalArgumentException("Work Storages not found");
        }
        return storagesList.get(storageIndex);
    }
    protected Path setNextCurrentStorage(){
        storageIndex++;
        if( storageIndex > (storagesList.size() - 1) ){
            storageIndex = 0;
            countStorageIteration++;
        }
        return storagesList.get(storageIndex);
    }
    protected Integer getCountStorageIteration(){
        return countStorageIteration;
    }
    protected Integer getCurrentStorageIndex(){
        return storageIndex;
    }
    protected Path getCurrentStorageForText(){
        Path nowTextFolder = Paths.get(getCurrentStorage().toString(),TXT_TESS_DIR);
        try{
            pathIsNotDirectory(nowTextFolder);
        }catch(IOException ex){
            System.out.println("Not directory " + nowTextFolder.toString());
        }
        try{
            pathIsNotReadWriteLink(nowTextFolder);
        }catch(IOException ex){
            System.out.println("Not readable, writeable or it is a link " + nowTextFolder.toString());
        }
        return nowTextFolder;
    }
    protected Path getCurrentStorageForImages(){
        Path nowImagesFolder = Paths.get(getCurrentStorage().toString(),JPG_DIR);
        try{
            pathIsNotDirectory(nowImagesFolder);
        }catch(IOException ex){
            System.out.println("Not directory " + nowImagesFolder.toString());
        }
        try{
            pathIsNotReadWriteLink(nowImagesFolder);
        }catch(IOException ex){
            System.out.println("Not readable, writeable or it is a link " + nowImagesFolder.toString());
        }
        return nowImagesFolder;
    }
    private static void pathIsNotFile(Path innerWorkPath) throws IOException{
        if ( !Files.exists(innerWorkPath, LinkOption.NOFOLLOW_LINKS) ){
            System.out.println("[ERROR] File or Directory not exist: " + innerWorkPath.toString());
            throw new IOException("[ERROR] File or Directory not exist: " + innerWorkPath.toString());
        }
        if ( Files.isDirectory(innerWorkPath, LinkOption.NOFOLLOW_LINKS) ){
            System.out.println("[ERROR] Directory exist and it is not a File: " + innerWorkPath.toString());
            throw new IOException("[ERROR] Directory exist and it is not a File: " + innerWorkPath.toString());
        }
    }
    private static void pathIsNotDirectory(Path innerWorkPath) throws IOException{
        if ( !Files.exists(innerWorkPath, LinkOption.NOFOLLOW_LINKS) ){
            System.out.println("[ERROR] File or Directory exist and it is not a Directory: " + innerWorkPath.toString());
            throw new IOException("[ERROR] File or Directory exist and it is not a Directory: " + innerWorkPath.toString());
        }
        if ( !Files.isDirectory(innerWorkPath, LinkOption.NOFOLLOW_LINKS) ){
            System.out.println("[ERROR] File exist and it is not a Directory: " + innerWorkPath.toString());
            throw new IOException("[ERROR] File exist and it is not a Directory: " + innerWorkPath.toString());
        }
    }
    private static void pathIsNotReadWriteLink(Path innerWorkPath) throws IOException{
        if ( !Files.isReadable(innerWorkPath) ){
            System.out.println("[ERROR] File or Directory exist and it is not a Readable: " + innerWorkPath.toString());
            throw new IOException("[ERROR] File or Directory exist and it is not a Readable: " + innerWorkPath.toString());
        }
        if ( !Files.isWritable(innerWorkPath) ){
            System.out.println("[ERROR] File or Directory exist and it is not a Writable: " + innerWorkPath.toString());
            throw new IOException("[ERROR] File or Directory exist and it is not a Writable: " + innerWorkPath.toString());
        }
        if ( Files.isSymbolicLink(innerWorkPath) ){
            System.out.println("[ERROR] File or Directory exist and it is not a SymbolicLink: " + innerWorkPath.toString());
            throw new IOException("[ERROR] File or Directory exist and it is a SymbolicLink: " + innerWorkPath.toString());
        }
    }
    protected void printStoragesList(){
        for (Path lookPath : storagesList) {
            System.out.println(lookPath.toString());
        }
    }
    protected Integer getSizeStoragesList(){
        return storagesList.size();
    }
    protected void getStoragesFromWorkFolders(){
        for (Path lookPath : workFolders) {
            int count = 0;
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(lookPath,"*{-id-}*")) {
                for (Path entry : stream) {
                    pathIsNotReadWriteLink(entry);
                    pathIsNotDirectory(entry);
                    storagesList.add(entry);
                    count++;
                }
                if( count == 0 ){
                    System.out.println("Directory is Empty " + lookPath.toString());
                }
                } catch (IOException | DirectoryIteratorException e) {
                    e.printStackTrace();
                    System.out.println("[ERROR] Can`t read count files in work directory " + lookPath.toString());
                }
        }
    }
    protected ArrayList<Path> getXlsxFilesFromReportDir(){
        ArrayList<Path> forReturn = new ArrayList<Path>();
        Path lookPath = getDirReportXls();
        int count = 0;
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(lookPath,"*.{xlsx}")) {
            for (Path entry : stream) {
                pathIsNotReadWriteLink(entry);
                pathIsNotFile(entry);
                forReturn.add(entry);
                count++;
            }
        if( count == 0 ){
            System.out.println("Directory is Empty " + lookPath.toString());
        }
        } catch (IOException | DirectoryIteratorException e) {
            e.printStackTrace();
            System.out.println("[ERROR] Can`t read count files in work directory " + lookPath.toString());
        }
        return forReturn;
            
    }
    protected ArrayList<Path> getTextFilesFromCurrentStorage(){
        ArrayList<Path> forReturn = new ArrayList<Path>();
        Path lookPath = getCurrentStorageForText();
        int count = 0;
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(lookPath,"*.{txt}")) {
            for (Path entry : stream) {
                pathIsNotReadWriteLink(entry);
                pathIsNotFile(entry);
                forReturn.add(entry);
                count++;
            }
        if( count == 0 ){
            System.out.println("Directory is Empty " + lookPath.toString());
        }
        } catch (IOException | DirectoryIteratorException e) {
            e.printStackTrace();
            System.out.println("[ERROR] Can`t read count files in work directory " + lookPath.toString());
        }
        return forReturn;
    }
    protected ArrayList<Path> getImagesFilesFromCurrentStorage(){
        ArrayList<Path> forReturn = new ArrayList<Path>();
        Path lookPath = getCurrentStorageForImages();
        int count = 0;
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(lookPath,"*.{png}")) {
            for (Path entry : stream) {
                try {
                    pathIsNotFile(entry);
                    pathIsNotReadWriteLink(entry);
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                    ex.printStackTrace();
                    continue;
                }
                forReturn.add(entry);
                count++;
            }
        if( count == 0 ){
            System.out.println("Directory is Empty " + lookPath.toString());
        }
        } catch (IOException | DirectoryIteratorException e) {
            e.printStackTrace();
            System.out.println("[ERROR] Can`t read count files in work directory " + lookPath.toString());
        }
        return forReturn;
    }
    
    protected void listDirInRoot() {
        Path workPath = Paths.get(PATH_ROOT);
        System.out.println("Storage contained in PDF directory " + workPath.toString());
        System.out.println("files: ");
        int count = 0;
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(workPath,"*{-id-ocr}")) {
        for (Path entry : stream) {
            workFolders.add(entry);
            count++;
        }
        if( count == 0 ){
            System.out.println("Directory is Empty " + workPath.toString());
        }
        } catch (IOException | DirectoryIteratorException e) {
            e.printStackTrace();
            System.out.println("[ERROR] Can`t read count files in work directory " + workPath.toString());
        }
    }
    protected static List<String> readLinesFromFile(Path forReadPath){
        try {
            pathIsNotFile(forReadPath);
            pathIsNotReadWriteLink(forReadPath);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
            return new ArrayList<String>();
        }
        ArrayList<String> lines = new ArrayList<String>();
        try {
            lines.addAll(Files.readAllLines(forReadPath, Charset.forName("UTF-8")));
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
        return lines;
        
    }
    private void copyFileFromSrcToDest(Path srcPath, Path destPath){
        try {
            Files.copy(srcPath, destPath, REPLACE_EXISTING, COPY_ATTRIBUTES);
        } catch (UnsupportedOperationException ex) {
            ex.printStackTrace();
            System.out.println("[ERROR] Can`t copy files from "
            + srcPath.toString()
            + " to " + destPath.toString());
        } catch (FileAlreadyExistsException ex) {
            ex.printStackTrace();
            System.out.println("[ERROR] Can`t copy files from "
            + srcPath.toString()
            + " to " + destPath.toString());
        } catch (DirectoryNotEmptyException ex) {
            ex.printStackTrace();
            System.out.println("[ERROR] Can`t copy files from "
            + srcPath.toString()
            + " to " + destPath.toString());
        } catch (SecurityException ex) {
            ex.printStackTrace();
            System.out.println("[ERROR] Can`t copy files from "
            + srcPath.toString()
            + " to " + destPath.toString());
        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println("[ERROR] Can`t copy files from "
            + srcPath.toString()
            + " to " + destPath.toString());
        }
    }
    protected static String getNewProcessId(){
        long currentDateTime = System.currentTimeMillis();
       //creating Date from millisecond
       Date currentDate = new Date(currentDateTime);
       //printing value of Date
       //System.out.println("current Date: " + currentDate);
       DateFormat df = new SimpleDateFormat("yyyyMMdd-HHmmss");
       //formatted value of current Date
       return df.format(currentDate);
    }
    private Path checkOrCreateSubHtmlReportDir(String subDirName){
        Path subDirForHtmlReport = getDirDictonariesHtmlImgTextReport();
        Path forCheckOrCreateDir = Paths.get(subDirForHtmlReport.toString(),subDirName);
        if( Files.exists(forCheckOrCreateDir, LinkOption.NOFOLLOW_LINKS) ){
            try {
                pathIsNotReadWriteLink(forCheckOrCreateDir);
            } catch (IOException ex) {
                ex.printStackTrace();
                System.out.println("[ERROR] Not readable, writeable or link " + forCheckOrCreateDir.toString());
            }
            if( Files.isDirectory(forCheckOrCreateDir, LinkOption.NOFOLLOW_LINKS) ){
                return forCheckOrCreateDir;
            }
        }
        try {
            Files.createDirectory(forCheckOrCreateDir);
            pathIsNotReadWriteLink(forCheckOrCreateDir);
        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println("[ERROR] Can`t createDirectory " + forCheckOrCreateDir.toString());
        }
        return forCheckOrCreateDir;
    }
    private Path checkOrCreateSubWorkDir(String subDirName){
         Path forCheckOrCreateDir = Paths.get(currentReportFolder.toString(),subDirName);
        if( Files.exists(forCheckOrCreateDir, LinkOption.NOFOLLOW_LINKS) ){
            try {
                pathIsNotReadWriteLink(forCheckOrCreateDir);
            } catch (IOException ex) {
                ex.printStackTrace();
                System.out.println("[ERROR] Not readable, writeable or link " + forCheckOrCreateDir.toString());
            }
            if( Files.isDirectory(forCheckOrCreateDir, LinkOption.NOFOLLOW_LINKS) ){
                return forCheckOrCreateDir;
            }
        }
        try {
            Files.createDirectory(forCheckOrCreateDir);
            pathIsNotReadWriteLink(forCheckOrCreateDir);
        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println("[ERROR] Can`t createDirectory " + forCheckOrCreateDir.toString());
        }
        return forCheckOrCreateDir;
    }
    private Path createReportFolder(){
        String newStoragePath = getNewProcessId();
        Path workPath = Paths.get(PATH_ROOT,newStoragePath + REPORT_POSFIX);
        if( !Files.exists(workPath, LinkOption.NOFOLLOW_LINKS)){
            try {
                Files.createDirectory(workPath);
            } catch (IOException ex) {
                ex.printStackTrace();
                System.out.println("[ERROR] Can`t create work directory for Storage: " + workPath.toString());
            }
        }
        return workPath;
    }
    protected void listReportDirInRoot() {
        Path workPath = Paths.get(PATH_ROOT);
        System.out.println("Storage contained in PDF directory " + workPath.toString());
        System.out.println("files: ");
        int count = 0;
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(workPath,"*{" + REPORT_POSFIX + "}")) {
        for (Path entry : stream) {
            try {
                pathIsNotReadWriteLink(entry);
                pathIsNotDirectory(entry);
                Path forCheckCompiled = Paths.get(entry.toString(),IS_COMPILED_REPORT);
                if( !Files.exists(forCheckCompiled) ){
                    currentReportFolder = entry;
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                System.out.println("[ERROR] Not readable, writeable or link " + entry.toString());
            }
            count++;
        }
        if( count == 0 ){
            System.out.println("Directory is Empty " + workPath.toString());
            currentReportFolder = createReportFolder();
        }
        } catch (IOException | DirectoryIteratorException e) {
            e.printStackTrace();
            System.out.println("[ERROR] Can`t read count files in work directory " + workPath.toString());
        }
    }
    protected Path getCurrentReportDir(){
        return currentReportFolder;
    }
    protected Path getXlsReportFileName(){
        Path dirReportXls = getDirReportXls();
        System.out.println(dirReportXls.toString());
        Path currentXlsReportFile = Paths.get(dirReportXls.toString(), getNewProcessId() + XLSX_REPORT_NAME);
        System.out.println(getDirReportXls().toString());
        System.out.println(currentXlsReportFile.toString());
        return currentXlsReportFile;
    }
}
