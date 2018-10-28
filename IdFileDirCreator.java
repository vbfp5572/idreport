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
    DOC_TYPES_FOLDER(Paths.get(ROOT_FOLDER.toString(),"docObjType"));
    private Path operationsFolder;
    IdFileDirCreator(Path operationsFolderInputed){
        this.operationsFolder = operationsFolderInputed;
    }
    protected Path getFolderForNewProcess(){
        String processIdForNow = getNewProcessId();
        Path cssFile = Paths.get(operationsFolder.toString(),  processIdForNow);
        
        return operationsFolder;
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
