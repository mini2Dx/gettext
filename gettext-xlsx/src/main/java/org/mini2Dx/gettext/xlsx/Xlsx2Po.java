/*******************************************************************************
 * Copyright 2020 Thomas Cashman
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
package org.mini2Dx.gettext.xlsx;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.mini2Dx.gettext.PoFile;
import org.mini2Dx.gettext.TranslationEntry;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Converts Excel files into PO localisation files
 */
public class Xlsx2Po {
	private static final ConcurrentMap<String, Locale> LOCALE_BY_ENGLISH_NAME = new ConcurrentHashMap<String, Locale>() {
		{
			for(Locale locale : Locale.getAvailableLocales()) {
				put(locale.getDisplayName(Locale.ENGLISH).toUpperCase().replace(' ', '_'), locale);
			}
		}
	};

	public static void convertFile(final File xlsxFile, final File poDirectory) throws IOException, InvalidFormatException {
		final XSSFWorkbook workbook = new XSSFWorkbook(xlsxFile);

		for(int sheetIndex = 0; sheetIndex < workbook.getNumberOfSheets(); sheetIndex++) {
			final Sheet sheet = workbook.getSheetAt(sheetIndex);

			final Map<Integer, Locale> localeIndices = new HashMap<Integer, Locale>();
			final Map<Locale, PoFile> poFiles = new HashMap<Locale, PoFile>();
			final Map<Locale, File> outputFiles = new HashMap<Locale, File>();

			final Row headerRow = sheet.getRow(0);
			for(int columnIndex = 5; columnIndex < headerRow.getLastCellNum(); columnIndex++) {
				final String language = getCell(headerRow, columnIndex);
				if(language == null || language.isEmpty()) {
					continue;
				}
				final Locale locale = LOCALE_BY_ENGLISH_NAME.get(language.toUpperCase().replace(' ', '_'));
				final File directory = new File(poDirectory, locale.getLanguage());
				if(!directory.exists()) {
					directory.mkdirs();
				}
				outputFiles.put(locale, new File(directory, sheet.getSheetName().replace(".pot", ".po")));
				poFiles.put(locale, new PoFile(locale));
				localeIndices.put(columnIndex, locale);
			}

			for(int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
				final Row row = sheet.getRow(rowIndex);
				final String reference = getCell(row, 0);
				final String context = getCell(row, 1);

				final String [] extractedComments = getCell(row, 2).split("\n");
				final String id = getCell(row, 3);
				final String idPlural = getCell(row, 4);

				if(id.isEmpty() && idPlural.isEmpty()) {
					continue;
				}

				for(int columnIndex = 5; columnIndex < row.getLastCellNum(); columnIndex++) {
					final Locale locale = localeIndices.get(columnIndex);
					final PoFile poFile = poFiles.get(locale);

					final TranslationEntry translationEntry = new TranslationEntry();
					translationEntry.setReference(reference);
					translationEntry.setContext(context);
					translationEntry.setId(id);
					translationEntry.setIdPlural(idPlural);

					for(String comment : extractedComments) {
						translationEntry.getExtractedComments().add(comment);
					}
					translationEntry.getStrings().add(getCell(row, columnIndex));
					poFile.getEntries().add(translationEntry);
				}
			}

			for(Locale locale : poFiles.keySet()) {
				final PoFile poFile = poFiles.get(locale);
				poFile.saveTo(outputFiles.get(locale));
			}
		}
	}

	private static String getCell(final Row row, final int columnIndex) {
		if(columnIndex >= row.getLastCellNum()) {
			return "";
		}
		if(row.getCell(columnIndex) == null) {
			return "";
		}
		return row.getCell(columnIndex).getStringCellValue().trim();
	}
}
