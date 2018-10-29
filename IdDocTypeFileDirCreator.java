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
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author СДО
 */
public class IdDocTypeFileDirCreator {
    private static final String ROOT_DIR = "D:/";
    private static final String DOC_TYPES_DIR = "docObjType";
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

    private static final String DOC_TYPE_LOCK_FILE_NAME_EXT = "status.lck";
    private static final String DOC_TYPE_JOURNAL_FILE_NAME_EXT = ".jnl";
    
    private Path operationsDir;
    
    IdDocTypeFileDirCreator(){
        this.operationsDir = foundForFirstNotLockDir();
        createAllSubFodersInCurrent();
    }
    protected Path getCurrentDir(){
        return this.operationsDir;
    }
    protected String getNameForDefaultFileType(){
        return DIR_DOC_TYPE_UNDEF;
    }
    private void createAllSubFodersInCurrent(){
        getCheckedSubCurrentDir(DIR_DOC_TYPE_AOCP);
        getCheckedSubCurrentDir(DIR_DOC_TYPE_AOOK);
        getCheckedSubCurrentDir(DIR_DOC_TYPE_ABK);
        getCheckedSubCurrentDir(DIR_DOC_TYPE_AKTDONEAKZ);
        getCheckedSubCurrentDir(DIR_DOC_TYPE_AKTRASTKOMPENS);
        getCheckedSubCurrentDir(DIR_DOC_TYPE_AKTISPTRUB);
        getCheckedSubCurrentDir(DIR_DOC_TYPE_SERT);
        getCheckedSubCurrentDir(DIR_DOC_TYPE_PASP);
        getCheckedSubCurrentDir(DIR_DOC_TYPE_BETONPROTOK);
        getCheckedSubCurrentDir(DIR_DOC_TYPE_BETONRESULTISP);
        getCheckedSubCurrentDir(DIR_DOC_TYPE_BETONRECEPT);
        getCheckedSubCurrentDir(DIR_DOC_TYPE_ISPSHEMA);
        getCheckedSubCurrentDir(DIR_DOC_TYPE_VIK);
        getCheckedSubCurrentDir(DIR_DOC_TYPE_UZK);
        getCheckedSubCurrentDir(DIR_DOC_TYPE_KS2);
        getCheckedSubCurrentDir(DIR_DOC_TYPE_KS6);
        getCheckedSubCurrentDir(DIR_DOC_TYPE_JOURNAL);
        getCheckedSubCurrentDir(DIR_DOC_TYPE_JOURNALAKZ);
        getCheckedSubCurrentDir(DIR_DOC_TYPE_JOURNALBETON);
        getCheckedSubCurrentDir(DIR_DOC_TYPE_JOURNALBUR);
        getCheckedSubCurrentDir(DIR_DOC_TYPE_JOURNALPOGR);
        getCheckedSubCurrentDir(DIR_DOC_TYPE_JOURNALSVARKI);
        getCheckedSubCurrentDir(DIR_DOC_TYPE_JOURNALMONTAJKONSTR);
        getCheckedSubCurrentDir(DIR_DOC_TYPE_JOURNALSVARKITRUB);
        getCheckedSubCurrentDir(DIR_DOC_TYPE_JOURNALSOBSCHII);
        getCheckedSubCurrentDir(DIR_DOC_TYPE_UNDEF);
    }
    
    private static Path foundForFirstNotLockDir(){
        //@todo scan dirs for nor set *.lck file and return for current operations
        Path workPath = Paths.get(ROOT_DIR);
        System.out.println("[INFO]In Root folder, path " + workPath.toString());
        System.out.println("[INFO]found dirictories: ");
        int count = 0;
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(workPath,"*{" + DOC_TYPES_DIR + "}")) {
        for (Path entry : stream) {
            try {
                pathIsNotReadWriteLink(entry);
            } catch (IOException ex) {
                ex.printStackTrace();
                System.out.println("[ERROR] Not readable, writeable or link, path " + entry.toString());
            }
            try {
                pathIsNotDirectory(entry);
            } catch (IOException ex) {
                ex.printStackTrace();
                System.out.println("[ERROR] Not directory, path " + entry.toString());
            }

            Path forCheckCompiled = Paths.get(entry.toString(),DOC_TYPE_LOCK_FILE_NAME_EXT);
            if( Files.notExists(forCheckCompiled) ){
                count++;
                return entry;
            }
        }
        if( count == 0 ){
            Path forNewStorage = getDirForNewProcess();
            System.out.println("Directory is Empty, created new Dir for storage " + forNewStorage.toString());
            return forNewStorage;
        }
        } catch (NoSuchFileException e) {
            //e.printStackTrace();
            System.out.println("[INFO] Directories not founded in work directory " + workPath.toString());
            return getDirForNewProcess();
        } catch (IOException | DirectoryIteratorException e) {
            e.printStackTrace();
            System.out.println("[ERROR] Can`t read count files in work directory " + workPath.toString());
        } 
        return getDirForNewProcess();
    }
    protected static Path getDirForNewProcess(){
        String processIdForNow = getNewProcessId();
        return checkDirsExistOrCreate(Paths.get(ROOT_DIR, processIdForNow + DOC_TYPES_DIR));
    }
    protected Path getCheckedSubCurrentDir(String subDir){
        
        Path doItForDir = Paths.get(operationsDir.toString(), subDir);
        System.out.println("[INFO]DocType.getCheckedSubCurrentDir " + doItForDir.toString());
        return checkDirsExistOrCreate(doItForDir);
    }
    protected String getJournalFileExtention(){
        return DOC_TYPE_JOURNAL_FILE_NAME_EXT;
    }
    protected Path setLockForCurrentDir(){
        String processIdForNow = getNewProcessId();
        Path lockedFilePath = Paths.get(operationsDir.toString(),DOC_TYPE_LOCK_FILE_NAME_EXT);
        try{
            if( Files.notExists(lockedFilePath) ){
                Files.createFile(lockedFilePath);
                System.out.println("[CREATELOCK]In file " + lockedFilePath.toString());
            }
            pathIsNotFile(lockedFilePath);
            pathIsNotReadWriteLink(lockedFilePath);
        } catch (IOException ex) {
            System.out.println("[ERROR]Can`t create lock file " + lockedFilePath.toString()
                    + ex.getMessage());
            ex.printStackTrace();
        }
        return lockedFilePath;
    }
    
    protected static Path checkDirsExistOrCreate(Path foderForCheck){
        try{
            if( Files.notExists(foderForCheck) ){
                Files.createDirectories(foderForCheck);
                System.out.println("[DIR_CREATE] Create directories, path: " + foderForCheck.toString());
            }
            pathIsNotDirectory(foderForCheck);
            pathIsNotReadWriteLink(foderForCheck);
            System.out.println("[DIR_EXIST] Readed, Writed and NotLink, path: " + foderForCheck.toAbsolutePath().toString());
        } catch (IOException ex) {
            System.out.println("[ERROR]Can`t create new file, directory not exist or not have permissions " + foderForCheck.toString()
                    + ex.getMessage());
            ex.printStackTrace();
        }
        return foderForCheck;
    }
    protected static String getNewProcessId(){
       long currentDateTime = System.currentTimeMillis();
       //creating Date from millisecond
       Date currentDate = new Date(currentDateTime);
       //printing value of Date
       //System.out.println("current Date: " + currentDate);
       DateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSSSSS");
       //formatted value of current Date
       return df.format(currentDate);
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
    protected static ArrayList<String> readJournal(String ncStrCfgPath){
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
    protected static void writeJournal(String strCfgPath, ArrayList<String> strTextRemark){
        
        
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
}
