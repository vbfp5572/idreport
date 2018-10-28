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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author СДО
 */
public enum IdFileDirCreator {
    ROOT_FOLDER(Paths.get("D:/")),
    DOC_TYPES_FOLDER(Paths.get(ROOT_FOLDER.toString(),"docObjType")),
    DIR_DOC_TYPE_AOCP(Paths.get(DOC_TYPES_FOLDER.toString(),"AOCP")),
    DIR_DOC_TYPE_AOOK(Paths.get(DOC_TYPES_FOLDER.toString(),"AOOK")),
    DIR_DOC_TYPE_ABK(Paths.get(DOC_TYPES_FOLDER.toString(),"ABK")),
    DIR_DOC_TYPE_AKTDONEAKZ(Paths.get(DOC_TYPES_FOLDER.toString(),"AKTDONEAKZ")),
    DIR_DOC_TYPE_AKTRASTKOMPENS(Paths.get(DOC_TYPES_FOLDER.toString(),"AKTRASTKOMPENS")),
    DIR_DOC_TYPE_AKTISPTRUB(Paths.get(DOC_TYPES_FOLDER.toString(),"AKTISPTRUB")),
    DIR_DOC_TYPE_SERT(Paths.get(DOC_TYPES_FOLDER.toString(),"SERT")),
    DIR_DOC_TYPE_PASP(Paths.get(DOC_TYPES_FOLDER.toString(),"PASP")),
    DIR_DOC_TYPE_BETONPROTOK(Paths.get(DOC_TYPES_FOLDER.toString(),"BETONPROTOK")),
    DIR_DOC_TYPE_BETONRESULTISP(Paths.get(DOC_TYPES_FOLDER.toString(),"BETONRESULTISP")),
    DIR_DOC_TYPE_BETONRECEPT(Paths.get(DOC_TYPES_FOLDER.toString(),"BETONRECEPT")),
    DIR_DOC_TYPE_ISPSHEMA(Paths.get(DOC_TYPES_FOLDER.toString(),"ISPSHEMA")),
    DIR_DOC_TYPE_VIK(Paths.get(DOC_TYPES_FOLDER.toString(),"VIK")),
    DIR_DOC_TYPE_UZK(Paths.get(DOC_TYPES_FOLDER.toString(),"UZK")),
    DIR_DOC_TYPE_KS2(Paths.get(DOC_TYPES_FOLDER.toString(),"KS2")),
    DIR_DOC_TYPE_KS6(Paths.get(DOC_TYPES_FOLDER.toString(),"KS6")),
    DIR_DOC_TYPE_JOURNAL(Paths.get(DOC_TYPES_FOLDER.toString(),"JOURNAL")),
    DIR_DOC_TYPE_JOURNALAKZ(Paths.get(DOC_TYPES_FOLDER.toString(),"JOURNALAKZ")),
    DIR_DOC_TYPE_JOURNALBETON(Paths.get(DOC_TYPES_FOLDER.toString(),"JOURNALBETON")),
    DIR_DOC_TYPE_JOURNALBUR(Paths.get(DOC_TYPES_FOLDER.toString(),"JOURNALBUR")),
    DIR_DOC_TYPE_JOURNALPOGR(Paths.get(DOC_TYPES_FOLDER.toString(),"JOURNALPOGR")),
    DIR_DOC_TYPE_JOURNALSVARKI(Paths.get(DOC_TYPES_FOLDER.toString(),"JOURNALSVARKI")),
    DIR_DOC_TYPE_JOURNALMONTAJKONSTR(Paths.get(DOC_TYPES_FOLDER.toString(),"JOURNALMONTAJKONSTR")),
    DIR_DOC_TYPE_JOURNALSVARKITRUB(Paths.get(DOC_TYPES_FOLDER.toString(),"JOURNALSVARKITRUB")),
    DIR_DOC_TYPE_JOURNALSOBSCHII(Paths.get(DOC_TYPES_FOLDER.toString(),"JOURNALOBSCHII")),
    DIR_DOC_TYPE_UNDEF(Paths.get(DOC_TYPES_FOLDER.toString(),"UNDEF"));
    
    private static String LOCK_EXT = ".lck";
    
    private Path operationsFolder;
    IdFileDirCreator(Path operationsFolderInputed){
        this.operationsFolder = operationsFolderInputed;
    }
    protected Path getFolderForNewProcess(){
        String processIdForNow = getNewProcessId();
        Path doItForFolder = Paths.get(operationsFolder.toString(),  processIdForNow);
        return checkDirsExistOrCreate(doItForFolder);
    }
    protected Path getSubCurrentFolder(){
        Path doItForFolder = Paths.get(operationsFolder.toString());
        return checkDirsExistOrCreate(doItForFolder);
    }
    protected Path setLockForFolder(){
        String processIdForNow = getNewProcessId();
        Path lockedFilePath = Paths.get(operationsFolder.toString(),processIdForNow + LOCK_EXT);
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
            System.out.println("[DIR_EXIST] Readed, Writed and NotLink, path: " + foderForCheck.toString());
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
}
