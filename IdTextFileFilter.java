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

import java.nio.file.Path;
import java.util.ArrayList;

/**
 *
 * @author VB
 */
public class IdTextFileFilter {
    private ArrayList<Path> listImagesFiles;
    private ArrayList<Path> listTextFiles;
    private ArrayList<String> currentFileStrings;

    public IdTextFileFilter(ArrayList<Path> forImagesFiles, ArrayList<Path> forTextFiles) {
        currentFileStrings = new ArrayList<String>();
        listImagesFiles = new ArrayList<Path>();
        listTextFiles = new ArrayList<Path>();
        listImagesFiles.addAll(forImagesFiles);
        listTextFiles.addAll(forTextFiles);
    }
    protected void createReportFromFiles(){
        
        for (int i = 0; i < listTextFiles.size(); i++) {
            Path getTextFilesPath = listTextFiles.get(i);
            Path getImagesFilesPath = listImagesFiles.get(i);
            currentFileStrings.addAll(IdFileManager.readLinesFromFile(getTextFilesPath));
            detectFileContent();
            currentFileStrings.clear();
        }
        
    }
    protected void detectFileContent(){
        for (String currentFileString : currentFileStrings) {
            String[] splitContent = currentFileString.split(" ");
        }
    }
    
}
