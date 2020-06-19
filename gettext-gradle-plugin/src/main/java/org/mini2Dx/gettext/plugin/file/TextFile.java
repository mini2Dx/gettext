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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TextFile implements SourceFile {
	public static final String DEFAULT_COMMENT_FORMAT = "#.";

	private final List<TranslationEntry> translationEntries = new ArrayList<TranslationEntry>();
	private final String relativePath;

	/**
	 * Parses a text file from an input stream using {@link #DEFAULT_COMMENT_FORMAT} as the PO comment prefix.
	 * Any line starting with the comment prefix will be treated as a translation note for the generate PO file.
	 * @param file The input {@link File} to read from
	 * @param relativePath The relative asset path for the file to use as the line reference in the PO translation entries
	 * @throws IOException
	 */
	public TextFile(File file, String relativePath) throws IOException {
		this(new FileInputStream(file), relativePath, DEFAULT_COMMENT_FORMAT);
	}

	/**
	 * Parses a text file from an input stream using {@link #DEFAULT_COMMENT_FORMAT} as the PO comment prefix.
	 * Any line starting with the comment prefix will be treated as a translation note for the generate PO file.
	 * @param inputStream The input stream to read from
	 * @param relativePath The relative asset path for the file to use as the line reference in the PO translation entries
	 * @throws IOException
	 */
	public TextFile(InputStream inputStream, String relativePath) throws IOException {
		this(inputStream, relativePath, DEFAULT_COMMENT_FORMAT);
	}

	/**
	 * Parses a text file from an input stream using a custom PO comment prefix.
	 * Any line starting with the comment prefix will be treated as a translation note for the generate PO file.
	 * @param inputStream The input stream to read from
	 * @param relativePath The relative asset path for the file to use as the line reference in the PO translation entries
	 * @param commentFormatPrefix The custom comment prefix to parse
	 * @throws IOException
	 */
	public TextFile(InputStream inputStream, String relativePath, String commentFormatPrefix) throws IOException {
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
				if(line.startsWith(commentFormatPrefix)) {
					entry.getExtractedComments().add(line.substring(commentFormatPrefix.length()).trim());
				} else {
					entry.setReference(this.relativePath + ":" + lineNumber);
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

	public String getRelativePath() {
		return relativePath;
	}
}
