/*******************************************************************************
 * Copyright 2022 Thomas Cashman
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
package org.mini2Dx.gettext.extractor;

import org.mini2Dx.gettext.PoFile;
import org.mini2Dx.gettext.TranslationEntry;
import org.mini2Dx.gettext.extractor.file.SourceFile;
import org.mini2Dx.gettext.extractor.file.SourceFileParser;
import java.util.List;
import java.util.ArrayList;
import java.util.Locale;
import java.io.IOException;
import java.io.File;

public class Main {
    public static String commentFormat = "#.";
    public static String forceExtractFormat = "#!extract";
    public static String ignoreFormat = "#!ignore";

    public static void main(String[] args) throws IOException {
        final File outputFile = new File(args[0]);
        final PoFile poFile = new PoFile(Locale.ENGLISH);
        final List<TranslationEntry> entries = new ArrayList<TranslationEntry>();
        for (int i = 1; i < args.length; i++) {
            final File file = new File(args[i]);
            generateTranslationEntries(file, args[i], entries);
            poFile.getEntries().addAll(entries);
            entries.clear();
        }
        if(outputFile.getParentFile() != null && !outputFile.getParentFile().exists()) {
            outputFile.getParentFile().mkdirs();
        }
        poFile.saveTo(outputFile);
    }

    private static void generateTranslationEntries(File file, String relativePath, List<TranslationEntry> results) throws IOException {
        final SourceFile sourceFile = SourceFileParser.parse(file, relativePath, commentFormat, forceExtractFormat, ignoreFormat);
        sourceFile.getTranslationEntries(results);
        sourceFile.dispose();
    }
}
