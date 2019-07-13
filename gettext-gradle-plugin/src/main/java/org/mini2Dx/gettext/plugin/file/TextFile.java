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
package org.mini2Dx.gettext.plugin.file;

import org.mini2Dx.gettext.TranslationEntry;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TextFile implements SourceFile {
	private final List<TranslationEntry> translationEntries = new ArrayList<TranslationEntry>();
	private final String relativePath;

	public TextFile(InputStream inputStream, String relativePath, String commentFormat) throws IOException {
		super();
		this.relativePath = relativePath;

		final Scanner scanner = new Scanner(inputStream);

		TranslationEntry entry = null;
		int lineNumber = 1;
		while(scanner.hasNextLine()) {
			final String line = scanner.nextLine();
			if(entry == null) {
				entry = new TranslationEntry();
			}
			if(!line.trim().isEmpty()) {
				if(line.startsWith(commentFormat)) {
					entry.getExtractedComments().add(line.substring(commentFormat.length()).trim());
				} else {
					entry.setReference(relativePath + ":" + lineNumber);
					entry.setId(line);
					translationEntries.add(entry);
					entry = null;
				}
			}
			lineNumber++;
		}
		scanner.close();
		inputStream.close();
	}

	@Override
	public void getTranslationEntries(List<TranslationEntry> result) {
		result.addAll(translationEntries);
	}

	@Override
	public void dispose() {
		translationEntries.clear();
	}
}
