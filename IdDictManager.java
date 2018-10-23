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


    public IdDictManager(IdFileManager idOuterFmReport) {
        idInnerFmReport = idOuterFmReport;
        dictFileManager = new IdDictFileManager(idOuterFmReport);
    }
    protected void putSplitLineAndPutToDictonaries(ArrayList<String> linesFromFile, Boolean lastFileOuterFlag){
        /*CopyOnWriteArrayList<String> linesReadedFromTextFiles = new CopyOnWriteArrayList<String>();
        linesReadedFromTextFiles.addAll(linesFromFile);
        if( (linesReadedFromTextFiles.size() > 1000) || lastFileOuterFlag ){
        IdExStrSplitter spliterExec = new IdExStrSplitter(linesReadedFromTextFiles,
        idInnerFmReport);
        spliterExec.start();
        }*/
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
        putLinesToFile(checkDirForFileName, linesFromFile);
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
