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
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

/**
 *
 * @author VB
 */
public class IdDictFileManager {

    private IdFileManager idInnerFmReport;
    private Path dirDictonaries;
    private Path dirDictonariesBlank;
    private Path dirDictonariesUnfiltered;
    private Path dirDictonariesWord;
    private Path currentReportFolder;
    
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
    
    private static final String DECLINE = "decline";
    private static final String ACCEPT = "accept";
    private static final String FILE_EXTENTION= ".dct";
    private static final String FILE_FULL_EXTENTION= ".lck";
    
    private Integer FILE_LINES_LIMIT = 1000;
    
    public IdDictFileManager(IdFileManager idOuterFmReport) {
        idInnerFmReport = idOuterFmReport;
        dirDictonaries = idInnerFmReport.getDirDictonaries();
        dirDictonariesBlank = idInnerFmReport.getDirDictonariesBlank();
        dirDictonariesUnfiltered = idInnerFmReport.getDirDictonariesUnfiltered();
        dirDictonariesWord = idInnerFmReport.getDirDictonariesWord();
        currentReportFolder = idInnerFmReport.getCurrentReportDir();
        createDictBlankFileStorages();
        checkOrCreateSubDictonariesDir(DECLINE);
        checkOrCreateSubDictonariesDir(ACCEPT);
        
        checkOrCreateSubDictonariesWordDir(DECLINE);
        checkOrCreateSubDictonariesWordDir(ACCEPT);
        
        checkOrCreateSubDictonariesUnfilteredDir(DECLINE);
        checkOrCreateSubDictonariesUnfilteredDir(ACCEPT);
    }
    protected Integer getLinesLimit(){
        return FILE_LINES_LIMIT;
    }
    protected String getDefinedFileExtention(){
        return FILE_EXTENTION;
    }
    protected String getDefinedFileLockExtention(){
        return FILE_FULL_EXTENTION;
    }
    protected Path getCheckDirForFileName(){
        Path lookPath = checkOrCreateSubDictonariesUnfilteredDir(DECLINE);
        int count = 0;
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(lookPath,"*.{dct}")) {
            for (Path entry : stream) {
                pathIsNotReadWriteLink(entry);
                pathIsNotFile(entry);
                String replacedPath = entry.toString().replace(FILE_EXTENTION, FILE_FULL_EXTENTION);
                Path lockedFilePath = Paths.get(replacedPath);
                if( Files.notExists(lockedFilePath) ){
                    return entry;
                }
                count++;
            }
        if( count == 0 ){
            System.out.println("Directory is Empty " + lookPath.toString());
        }
        } catch (IOException | DirectoryIteratorException e) {
            e.printStackTrace();
            System.out.println("[ERROR] Can`t read count files in work directory " + lookPath.toString());
        }
        Path returnFileName = getDictonariesUnfilteredDirDeclineNewFile();
        return returnFileName;
    }
    private void createDictBlankFileStorages(){
        getDictonariesBlankFileForWord(DIR_DOC_TYPE_AOCP);
        getDictonariesBlankFileForWord(DIR_DOC_TYPE_AOOK);
        getDictonariesBlankFileForWord(DIR_DOC_TYPE_ABK);
        getDictonariesBlankFileForWord(DIR_DOC_TYPE_AKTDONEAKZ);
        getDictonariesBlankFileForWord(DIR_DOC_TYPE_AKTRASTKOMPENS);
        getDictonariesBlankFileForWord(DIR_DOC_TYPE_AKTISPTRUB);
        getDictonariesBlankFileForWord(DIR_DOC_TYPE_SERT);
        getDictonariesBlankFileForWord(DIR_DOC_TYPE_PASP);
        getDictonariesBlankFileForWord(DIR_DOC_TYPE_BETONPROTOK);
        getDictonariesBlankFileForWord(DIR_DOC_TYPE_BETONRESULTISP);
        getDictonariesBlankFileForWord(DIR_DOC_TYPE_BETONRECEPT);
        getDictonariesBlankFileForWord(DIR_DOC_TYPE_ISPSHEMA);
        getDictonariesBlankFileForWord(DIR_DOC_TYPE_VIK);
        getDictonariesBlankFileForWord(DIR_DOC_TYPE_UZK);
        getDictonariesBlankFileForWord(DIR_DOC_TYPE_KS2);
        getDictonariesBlankFileForWord(DIR_DOC_TYPE_KS6);
        getDictonariesBlankFileForWord(DIR_DOC_TYPE_JOURNAL);
        getDictonariesBlankFileForWord(DIR_DOC_TYPE_JOURNALAKZ);
        getDictonariesBlankFileForWord(DIR_DOC_TYPE_JOURNALBETON);
        getDictonariesBlankFileForWord(DIR_DOC_TYPE_JOURNALBUR);
        getDictonariesBlankFileForWord(DIR_DOC_TYPE_JOURNALPOGR);
        getDictonariesBlankFileForWord(DIR_DOC_TYPE_JOURNALSVARKI);
        getDictonariesBlankFileForWord(DIR_DOC_TYPE_JOURNALMONTAJKONSTR);
        getDictonariesBlankFileForWord(DIR_DOC_TYPE_JOURNALSVARKITRUB);
        getDictonariesBlankFileForWord(DIR_DOC_TYPE_JOURNALSOBSCHII);
        getDictonariesBlankFileForWord(DIR_DOC_TYPE_UNDEF);
    }
    
    protected Path getDictonariesBlankFileForWord(String creationFileName){
        Path toReturn = Paths.get(dirDictonariesBlank.toString(),creationFileName + FILE_EXTENTION);
        if( Files.exists(toReturn, LinkOption.NOFOLLOW_LINKS) ){
            try {
                pathIsNotFile(toReturn);
                pathIsNotReadWriteLink(toReturn);
                return toReturn;
            } catch (IOException ex) {
                ex.printStackTrace();
                System.out.println("[ERROR] Not readable, writeable or link " + toReturn.toString());
            }
        }
        try {
            Files.createFile(toReturn);
            try {
                pathIsNotFile(toReturn);
                pathIsNotReadWriteLink(toReturn);
                return toReturn;
            } catch (IOException ex) {
                ex.printStackTrace();
                System.out.println("[ERROR] Not readable, writeable or link " + toReturn.toString());
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println("[ERROR] Can`t createFile " + toReturn.toString());
        }
        return toReturn;
    }
    
    protected Path getDictonariesUnfilteredDirDeclineNewFile(){
        Path checkOrCreateSubDictonariesUnfilteredDir = checkOrCreateSubDictonariesUnfilteredDir(DECLINE);
        //@todo get new name or use founded
        Path toReturn = Paths.get(checkOrCreateSubDictonariesUnfilteredDir.toString(),UUID.randomUUID().toString() + FILE_EXTENTION);
        if( Files.exists(toReturn, LinkOption.NOFOLLOW_LINKS) ){
            try {
                pathIsNotFile(toReturn);
                pathIsNotReadWriteLink(toReturn);
            } catch (IOException ex) {
                ex.printStackTrace();
                System.out.println("[ERROR] Not readable, writeable or link " + toReturn.toString());
            }
        }
        try {
            Files.createFile(toReturn);
            try {
                pathIsNotFile(toReturn);
                pathIsNotReadWriteLink(toReturn);
            } catch (IOException ex) {
                ex.printStackTrace();
                System.out.println("[ERROR] Not readable, writeable or link " + toReturn.toString());
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println("[ERROR] Can`t createFile " + toReturn.toString());
        }
        return toReturn;
    }
    private Path checkOrCreateSubDictonariesDir(String subDirName){
         Path forCheckOrCreateDir = Paths.get(dirDictonaries.toString(),subDirName);
        if( Files.exists(forCheckOrCreateDir, LinkOption.NOFOLLOW_LINKS) ){
            try {
                pathIsNotDirectory(forCheckOrCreateDir);
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
    private Path checkOrCreateSubDictonariesUnfilteredDir(String subDirName){
         Path forCheckOrCreateDir = Paths.get(dirDictonariesUnfiltered.toString(),subDirName);
        if( Files.exists(forCheckOrCreateDir, LinkOption.NOFOLLOW_LINKS) ){
            try {
                pathIsNotDirectory(forCheckOrCreateDir);
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
    private Path checkOrCreateSubDictonariesWordDir(String subDirName){
         Path forCheckOrCreateDir = Paths.get(dirDictonariesWord.toString(),subDirName);
        if( Files.exists(forCheckOrCreateDir, LinkOption.NOFOLLOW_LINKS) ){
            try {
                pathIsNotDirectory(forCheckOrCreateDir);
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
                pathIsNotDirectory(forCheckOrCreateDir);
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
}
