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
import java.util.concurrent.CopyOnWriteArrayList;

/**
 *
 * @author VB
 */
public class IdDictManager {
    
    private IdDictFileManager dictFileManager;
    private IdFileManager idInnerFmReport;
    
    private CopyOnWriteArrayList<String> linesReadedFromTextFiles;

    public IdDictManager(ArrayList<String> linesFromFile, IdFileManager idOuterFmReport) {
        idInnerFmReport = idOuterFmReport;
        dictFileManager = new IdDictFileManager(idOuterFmReport);
        linesReadedFromTextFiles = new CopyOnWriteArrayList<String>();
        linesReadedFromTextFiles.addAll(linesFromFile);
    }
    protected void putSplitLineAndPutToDictonaries(){
        IdExStrSplitter spliterExec = new IdExStrSplitter(linesReadedFromTextFiles,
            idInnerFmReport);
        spliterExec.start();
    }
    
}
