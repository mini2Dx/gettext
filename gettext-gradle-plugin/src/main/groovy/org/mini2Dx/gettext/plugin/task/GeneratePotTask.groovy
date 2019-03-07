/*******************************************************************************
 * Copyright 2019 Thomas Cashman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.mini2Dx.gettext.plugin.task

import org.gradle.api.DefaultTask
import org.gradle.api.file.FileTree
import org.gradle.api.tasks.TaskAction
import org.mini2Dx.gettext.PoFile
import org.mini2Dx.gettext.TranslationEntry
import org.mini2Dx.gettext.plugin.GetTextSource
import org.mini2Dx.gettext.plugin.file.SourceFile
import org.mini2Dx.gettext.plugin.file.SourceFileParser

class GeneratePotTask extends DefaultTask {
    def GetTextSource source;

    @TaskAction
    public void run() throws IOException {
        final FileTree sourceFiles = project.fileTree(source.srcDir) {
            include source.include;
        };

        final PoFile poFile = new PoFile(Locale.ENGLISH);

        final List<TranslationEntry> entries = new ArrayList<TranslationEntry>();
        for(File file : sourceFiles.getFiles()) {
            generateTranslationEntries(file, project.relativePath(file), entries);
            poFile.getEntries().addAll(entries);
            entries.clear();
        }

        final File outputDirectory = new File(project.getBuildDir(), 'gettext');
        if(!outputDirectory.exists()) {
            outputDirectory.mkdirs();
        }
        final File outputFile = new File(outputDirectory, source.outputFilename);
        poFile.saveTo(outputFile);
    }

    private void generateTranslationEntries(File file, String relativePath, List<TranslationEntry> results) {
        final SourceFile sourceFile = SourceFileParser.parse(file, relativePath);
        sourceFile.getTranslationEntries(results);
        sourceFile.dispose();
    }
}
