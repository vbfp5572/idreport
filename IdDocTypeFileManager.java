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
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 *
 * @author СДО
 */
public class IdDocTypeFileManager {
    
    private IdDocTypeFileDirCreator docTypeCreatorInner;
    private IdFileManager idFmReportInner;
    private IdDictFileManager dictonariesFM;
    
    private static final Double DETECT_LIMIT_PERCENT = 50D;

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
            detectFileType(lines, fileEnemy);
        }
    }
    private void detectFileType(ArrayList<String> outerLinesFromSrcFile,Path detectedFileName){
        ArrayList<Path> dictonariesBlank = new ArrayList<Path>();
        dictonariesBlank.addAll(idFmReportInner.getDctFilesFromDictonariesBlankDir());
        int countDetectedPercents = 0;
        Double detectFileTypeAndWriteRecordToJournal = 0D;
        String detectedType = docTypeCreatorInner.getNameForDefaultFileType();
        for(Path fileEnemy : dictonariesBlank){
            detectFileTypeAndWriteRecordToJournal = 0D;
            ArrayList<String> linesFromBlank = new ArrayList<String>();
            
            linesFromBlank.addAll(getBlankWordFromFile(fileEnemy));
            if( linesFromBlank.size() == 0 ){
                continue;
            }
            detectFileTypeAndWriteRecordToJournal = detectFileTypeAndWriteRecordToJournal(outerLinesFromSrcFile, linesFromBlank);
            System.out.println("[DETECTPERCENT]" + detectFileTypeAndWriteRecordToJournal.toString() + "[DETECTTYPE] detected file " 
                    + detectedFileName.toAbsolutePath().toString()
                    + " chek for type "
                    + fileEnemy.toAbsolutePath().toString());
            if(  detectFileTypeAndWriteRecordToJournal >= DETECT_LIMIT_PERCENT ){
                countDetectedPercents++;
                //@todo function whos do record to journal
                detectedType = fileEnemy.getFileName().toString().replaceAll("\\.dct", "");
                String recordAboutType = 
                        IdDocTypeFileDirCreator.getNewProcessId()
                        + "|||||" + detectedFileName.toAbsolutePath().toString()
                        + "|||||" + detectedType 
                        + "|||||" + detectFileTypeAndWriteRecordToJournal.toString();
                detectFileTypeAndWriteRecordToJournal = 0D;
                putRecordInToDocTypeJournal(detectedFileName, detectedType, recordAboutType);
                
           }
        }
        if( detectFileTypeAndWriteRecordToJournal < DETECT_LIMIT_PERCENT ){
            
            String recordAboutType = IdDocTypeFileDirCreator.getNewProcessId()
                + "|||||" + detectedFileName.toAbsolutePath().toString()
                + "|||||" + detectedType
                + "|||||" + detectFileTypeAndWriteRecordToJournal.toString();
            detectFileTypeAndWriteRecordToJournal = 0D;
            putRecordInToDocTypeJournal(detectedFileName, detectedType, recordAboutType);
        }
    }
    private void putRecordInToDocTypeJournal(Path fileEnemy, String typeFile, String recordAboutThat){
        Path checkedSubCurrentDir = docTypeCreatorInner.getCheckedSubCurrentDir(typeFile);
        Path storageDir = idFmReportInner.getCurrentStorage();
        Path currentDir = docTypeCreatorInner.getCurrentDir();
        Path nameStorageDir = storageDir.getName(storageDir.getNameCount() - 1);
        String journalFileExtention = docTypeCreatorInner.getJournalFileExtention();
        
        Path creationJournalFileName = Paths.get(checkedSubCurrentDir.toString(), nameStorageDir.toString() + journalFileExtention);
        if( Files.notExists(creationJournalFileName) ){
            try {
                Files.createFile(creationJournalFileName);
            } catch (IOException ex) {
                ex.getMessage();
                ex.printStackTrace();
                System.out.println("[ERROR] Can`t create lock file " + creationJournalFileName.toAbsolutePath().toString());
            }
        }
        
        ArrayList<String> readJournal = new ArrayList<String>();
                
        readJournal.addAll(IdDocTypeFileDirCreator.readJournal(creationJournalFileName.toString()));
        readJournal.add(recordAboutThat);
        IdDocTypeFileDirCreator.writeJournal(creationJournalFileName.toString(), readJournal);
    }
    protected void setLockForStorages(Path storageDir){
        //@todo put record about storage processing
        String definedFileLockExtention = dictonariesFM.getDefinedFileLockExtention();
        Path currentDir = docTypeCreatorInner.getCurrentDir();
        Path nameStorageDir = storageDir.getName(storageDir.getNameCount() - 1);
        Path creationLockFileName = Paths.get(currentDir.toString(), nameStorageDir.toString() + definedFileLockExtention);
        if( Files.notExists(creationLockFileName) ){
            try {
                Files.createFile(creationLockFileName);
            } catch (IOException ex) {
                ex.getMessage();
                ex.printStackTrace();
                System.out.println("[ERROR] Can`t create lock file " + creationLockFileName.toAbsolutePath().toString());
            }
        }
    }
    private Double detectFileTypeAndWriteRecordToJournal(ArrayList<String> outerLinesFromSrcFile,
            ArrayList<String> linesFromBlank){
        ArrayList<Integer> countMatchesSrc = new ArrayList<Integer>();
        ArrayList<Integer> countMatchesBlank = new ArrayList<Integer>();
        Integer countContains = 0;
        double calculatedPercent = 0D;
        for(String fileSrcEnemy : outerLinesFromSrcFile){
            //@todo compare algoritm here
            countContains = 0;
            for(String blankEnemy : linesFromBlank){
                Integer indexMatchesBlank = 0;
                if( fileSrcEnemy.toLowerCase().matches(blankEnemy.toLowerCase()) ){
                    indexMatchesBlank++;
                    countContains++;
                }
                countMatchesBlank.add(indexMatchesBlank);
            }
            countMatchesSrc.add(countContains);
        }
        Integer sumSrcMatches = 0;
        for(Integer countContainsEnemy : countMatchesSrc){
            sumSrcMatches = sumSrcMatches + countContainsEnemy;
        }
        Integer sizeLinesSrcFile = outerLinesFromSrcFile.size();
        Integer sumBlankMatches = 0;
        for(Integer countContainsBlankEnemy : countMatchesBlank){
            sumBlankMatches = sumBlankMatches + countContainsBlankEnemy;
        }
        Integer sizeLinesBlank = linesFromBlank.size();
        calculatedPercent = (( sumSrcMatches  / sizeLinesSrcFile )
                + (sumBlankMatches / sizeLinesBlank))* 100D;
        return calculatedPercent;
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
