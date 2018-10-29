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
        Double detectFileTypeAndWriteRecordToJournal = Double.MIN_NORMAL;
        String detectedType = docTypeCreatorInner.getNameForDefaultFileType();
        for(Path fileEnemy : dictonariesBlank){
            ArrayList<String> linesFromBlank = new ArrayList<String>();
            linesFromBlank.addAll(getBlankWordFromFile(fileEnemy));
            detectFileTypeAndWriteRecordToJournal = detectFileTypeAndWriteRecordToJournal(outerLinesFromSrcFile, linesFromBlank);
            if(  detectFileTypeAndWriteRecordToJournal > 50 ){
                countDetectedPercents++;
                //@todo function whos do record to journal
                detectedType = fileEnemy.getFileName().toString().replaceAll("\\.dct", "");
                String recordAboutType = 
                        IdDocTypeFileDirCreator.getNewProcessId()
                        + "|||||" + detectedFileName.toAbsolutePath().toString()
                        + "|||||" + detectedType 
                        + "|||||" + detectFileTypeAndWriteRecordToJournal.toString();
                putRecordInToDocTypeJournal(detectedFileName, detectedType, recordAboutType);
           }
        }
        if( countDetectedPercents == 0 ){
            
            String recordAboutType = IdDocTypeFileDirCreator.getNewProcessId()
                + "|||||" + detectedFileName.toAbsolutePath().toString()
                + "|||||" + detectedType
                + "|||||" + detectFileTypeAndWriteRecordToJournal.toString();
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
