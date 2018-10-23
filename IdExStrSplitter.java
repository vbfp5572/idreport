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
public class IdExStrSplitter extends Thread  {
    private CopyOnWriteArrayList<String> linesInnerFromSrc;
    private IdDictFileManager dictInnerFileManager;
    private IdFileManager idInnerFmReport;

    public IdExStrSplitter(CopyOnWriteArrayList<String> linesReadedFromTextFiles,
            IdFileManager idOuterFmReport) {
        idInnerFmReport = idOuterFmReport;
        linesInnerFromSrc = new CopyOnWriteArrayList<String>();
        linesInnerFromSrc.addAll(linesReadedFromTextFiles);
        linesReadedFromTextFiles.clear();
        //@todo create new dict file manager for this instance
        dictInnerFileManager = new IdDictFileManager(idOuterFmReport);
    }
    
    @Override
    public void run() {
        for (String linesReadedFromTextFile : linesInnerFromSrc) {
            String[] wordFromFileReadedLine = linesReadedFromTextFile.split(" ");
            Path dictonariesUnfilteredDirDeclineNewFile = dictInnerFileManager.getCheckDirForFileName();
            
            ArrayList<String> fileLines = new ArrayList<String>();
            fileLines.addAll(getFileLines(dictonariesUnfilteredDirDeclineNewFile));
            System.out.println("In file " + dictonariesUnfilteredDirDeclineNewFile.toString()
                    + " lines readed " + fileLines.size());
            for (String string : wordFromFileReadedLine) {
                if( (fileLines.size() + 1) < dictInnerFileManager.getLinesLimit() ){
                    fileLines.add(string);
                }
                else{
                    putLinesToFile(dictonariesUnfilteredDirDeclineNewFile,fileLines);
                    String replacedPath = dictonariesUnfilteredDirDeclineNewFile.toString().replace(
                            dictInnerFileManager.getDefinedFileExtention(), dictInnerFileManager.getDefinedFileLockExtention());
                    Path lockedFilePath = Paths.get(replacedPath);
                    try{
                        Files.createFile(lockedFilePath);
                    } catch (IOException ex) {
                        System.out.println(ex.getMessage());
                        ex.printStackTrace();
                    }
                    fileLines.clear();
                    dictonariesUnfilteredDirDeclineNewFile = dictInnerFileManager.getDictonariesUnfilteredDirDeclineNewFile();
                    fileLines.addAll(getFileLines(dictonariesUnfilteredDirDeclineNewFile));
                    fileLines.add(string);
                }
            }
            System.out.println("[NORMAL]In file " + dictonariesUnfilteredDirDeclineNewFile.toString()
                    + " lines readed " + fileLines.size());
            putLinesToFile(dictonariesUnfilteredDirDeclineNewFile,fileLines);
        }
        
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
    private ArrayList<String> getFileLines(Path forReadPath){
        ArrayList<String> lines = new ArrayList<String>();
        try {
            lines.addAll(Files.readAllLines(forReadPath, Charset.forName("UTF-8")));
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
        return lines;
    }
}
