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
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

/**
 *
 * @author СДО
 */
public class IdDocTypeFileManager {
    
    private IdDocTypeFileDirCreator docTypeCreatorInner;
    private IdFileManager idFmReportInner;
    private IdDictFileManager dictonariesFM;

    public IdDocTypeFileManager(IdDocTypeFileDirCreator docTypeCreatorOuter, 
            IdFileManager idFmReportOuter) {
        docTypeCreatorInner = docTypeCreatorOuter;
        idFmReportInner = idFmReportOuter;
        dictonariesFM = new IdDictFileManager(idFmReportOuter);
    }
    protected void processDetectFileTypes(ArrayList<Path> listTextFiles){
        ArrayList<String> lines = new ArrayList<String>();
        for(Path fileEnemy : listTextFiles){
            if( Files.notExists(fileEnemy) ){
                continue;
            }
            try {
                lines.addAll(Files.readAllLines(fileEnemy, Charset.forName("UTF-8")));
            } catch (IOException ex) {
                ex.getMessage();
                ex.printStackTrace();
                System.out.println("[ERROR] Can`t read lines from file " + fileEnemy.toAbsolutePath().toString());
            }
            String detectFileType = detectFileType(lines);
            //@todo function whos do record to journal
        }
        
    }
    private String detectFileType(ArrayList<String> outerLinesFromSrcFile){
        ArrayList<Path> dictonariesBlank = new ArrayList<Path>();
        dictonariesBlank.addAll(idFmReportInner.getDctFilesFromDictonariesBlankDir());
       
        for(Path fileEnemy : dictonariesBlank){
            ArrayList<String> linesFromBlank = new ArrayList<String>();
            linesFromBlank.addAll(getBlankWordFromFile(fileEnemy));
            Double detectFileTypeAndWriteRecordToJournal = detectFileTypeAndWriteRecordToJournal(outerLinesFromSrcFile, linesFromBlank);
            if(  detectFileTypeAndWriteRecordToJournal > 50 ){
                
                return fileEnemy.getFileName().toString().replaceAll("\\.dct", "")
                    + "|||||" + detectFileTypeAndWriteRecordToJournal.toString();
            }
            
        }
        return "UNDEFINED";
    }
    private Double detectFileTypeAndWriteRecordToJournal(ArrayList<String> outerLinesFromSrcFile,
            ArrayList<String> linesFromBlank){
        Integer countContains = 0;
        for(String fileEnemy : outerLinesFromSrcFile){
            //@todo compare algoritm here
            for(String blankEnemy : linesFromBlank){
                if( fileEnemy.toLowerCase().contains(blankEnemy.toLowerCase()) ){
                    countContains++;
                }
            }
        }
        Integer countRecoedsInBlank = linesFromBlank.size();
        return ( countContains.doubleValue()  / countRecoedsInBlank.doubleValue() );
    }
    private ArrayList<String> getBlankWordFromFile(Path fileEnemy){
        ArrayList<String> lines = new ArrayList<String>();
        if( Files.notExists(fileEnemy) ){
                return lines;
        }
        try {
            lines.addAll(Files.readAllLines(fileEnemy, Charset.forName("UTF-8")));
        } catch (IOException ex) {
            ex.getMessage();
            ex.printStackTrace();
            System.out.println("[ERROR] Can`t read lines from file " + fileEnemy.toAbsolutePath().toString());
        }
        return lines;
    }
}
