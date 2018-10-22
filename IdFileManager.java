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
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author VB
 */
public class IdFileManager {
    private static final String PATH_ROOT = "D:/";
    
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
    private void writeStady(String strSatdy){
        Path checkProcessStadyPath = Paths.get(PATH_ROOT);
        List<String> lines = new ArrayList<>();
        try {
            lines.addAll(Files.readAllLines(checkProcessStadyPath, Charset.forName("UTF-8")));
        } catch (IOException ex) {
            ex.getMessage();
            ex.printStackTrace();
        }
        lines.add(strSatdy);
        try {
            Files.write(checkProcessStadyPath, lines, Charset.forName("UTF-8"));
        } catch (IOException ex) {
            ex.getMessage();
            ex.printStackTrace();
        }
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
}
