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
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 *
 * @author VB
 */
public class IdDictFileManager {

    private IdFileManager idInnerFmReport;
    private Path dirDictonaries;
    private Path dirDictonariesUnfiltered;
    private Path dirDictonariesWord;
    private Path currentReportFolder;
    
    private String DECLINE = "decline";
    private String ACCEPT = "accept";
    
    private Integer FILE_LINES_LIMIT = 1000;
    
    public IdDictFileManager(IdFileManager idOuterFmReport) {
        idInnerFmReport = idOuterFmReport;
        dirDictonaries = idInnerFmReport.getDirDictonaries();
        dirDictonariesUnfiltered = idInnerFmReport.getDirDictonariesUnfiltered();
        dirDictonariesWord = idInnerFmReport.getDirDictonariesWord();
        currentReportFolder = idInnerFmReport.getCurrentReportDir();
        
        checkOrCreateSubDictonariesDir(DECLINE);
        checkOrCreateSubDictonariesDir(ACCEPT);
        
        checkOrCreateSubDictonariesWordDir(DECLINE);
        checkOrCreateSubDictonariesWordDir(ACCEPT);
        
        checkOrCreateSubDictonariesUnfilteredDir(DECLINE);
        checkOrCreateSubDictonariesUnfilteredDir(ACCEPT);
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