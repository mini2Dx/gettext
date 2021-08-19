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
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.mini2Dx.gettext.PoFile
import org.mini2Dx.gettext.TranslationEntry
import org.mini2Dx.gettext.plugin.GetTextSource
import org.mini2Dx.gettext.plugin.file.SourceFile
import org.mini2Dx.gettext.plugin.file.SourceFileParser

class GeneratePotTask extends DefaultTask {
    public static final String DEFAULT_COMMENT_FORMAT = "#.";
    public static final String DEFAULT_FORCE_EXTRACT_FORMAT = "#!extract";

    @InputDirectory
    public String srcDir;
    @Input
    @Optional
    public String include;
    @Input
    @Optional
    public String[] includes;
    @Input
    @Optional
    public String exclude;
    @Input
    @Optional
    public String[] excludes;
    @Input
    @Optional
    public String commentFormat = "#.";
    @Input
    @Optional
    public String forceExtractFormat = "#!extract";
    @Input
    @Optional
    public String ignoreFormat = "#!ignore";
    @OutputFile
    public File outputFile;

    @TaskAction
    public void run() throws IOException {
        final FileTree sourceFiles = project.fileTree(this.srcDir) {
            if(this.includes != null) {
                for (String includePath : this.includes) {
                    include includePath;
                }
            }

            if(this.include != null) {
                include this.include;
            }

            if(this.excludes != null) {
                for (String excludePath : this.excludes) {
                    exclude excludePath;
                }
            }

            if(this.exclude != null) {
                exclude this.exclude;
            }
        };

        final PoFile poFile = new PoFile(Locale.ENGLISH);

        final List<TranslationEntry> entries = new ArrayList<TranslationEntry>();
        for(File file : sourceFiles.getFiles()) {
            generateTranslationEntries(file, project.relativePath(file), entries);
            poFile.getEntries().addAll(entries);
            entries.clear();
        }
        if(!outputFile.getParentFile().exists()) {
            outputFile.getParentFile().mkdirs();
        }
        poFile.saveTo(outputFile);
    }

    private void generateTranslationEntries(File file, String relativePath, List<TranslationEntry> results) {
        final SourceFile sourceFile = SourceFileParser.parse(file, relativePath, this.commentFormat, this.forceExtractFormat, this.ignoreFormat);
        sourceFile.getTranslationEntries(results);
        sourceFile.dispose();
    }
}
