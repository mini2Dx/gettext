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

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.mini2Dx.gettext.PoFile;
import org.mini2Dx.gettext.TranslationEntry;

import java.io.*;
import java.util.Locale;

/**
 * Converts POT files into an Excel workbook for localisation
 */
public class Pot2Xlsx {

	public static void convertDirectory(Locale sourceLocale, File poDirectory, File xlsxFile) throws IOException {
		final XSSFWorkbook workbook = new XSSFWorkbook();
		convertDirectory(sourceLocale, workbook, poDirectory, poDirectory);

		try (OutputStream outputStream = new FileOutputStream(xlsxFile)) {
			workbook.write(outputStream);
		}
	}

	private static void convertDirectory(final Locale sourceLocale, final XSSFWorkbook workbook, final File rootDirectory, final File poDirectory) throws IOException {
		for(File file : poDirectory.listFiles()) {
			if(file.isDirectory()) {
				convertDirectory(sourceLocale, workbook, rootDirectory, file);
				continue;
			}

			final String relativePath = file.getAbsolutePath().replace(rootDirectory.getAbsolutePath(), "");
			final PoFile poFile = new PoFile(sourceLocale, file);
			convertFile(sourceLocale, workbook, relativePath, poFile);
		}
	}

	public static void convertFile(final Locale sourceLocale, final File poFile, final File xlsxFile) throws IOException {
		final PoFile poFile1 = new PoFile(sourceLocale, poFile);
		final XSSFWorkbook workbook = new XSSFWorkbook();

		convertFile(sourceLocale, workbook, poFile.getName(), poFile1);

		try (OutputStream outputStream = new FileOutputStream(xlsxFile)) {
			workbook.write(outputStream);
		}
	}

	private static void convertFile(final Locale sourceLocale, final XSSFWorkbook workbook, final String relativeFilePath, final PoFile poFile) throws IOException {
		final Sheet sheet = workbook.createSheet(relativeFilePath);

		final Row headerRow = sheet.createRow(0);
		setCell(headerRow, 0, "Reference");
		setCell(headerRow, 1, "Context");
		setCell(headerRow, 2, "Extracted Comments");
		setCell(headerRow, 3, sourceLocale.getDisplayName() + " (singular)");
		setCell(headerRow, 4, sourceLocale.getDisplayName() + " (plural)");
		sheet.createFreezePane( 0, 1, 0, 1 );

		int rowIndex = 1;
		for(TranslationEntry translationEntry : poFile.getEntries()) {
			final Row row = sheet.createRow(rowIndex);
			setCell(row, 0, translationEntry.getReference());
			setCell(row, 1, translationEntry.getContext());

			final StringBuilder extractedComments = new StringBuilder();
			for(String comment : translationEntry.getExtractedComments()) {
				extractedComments.append(comment);
				extractedComments.append('\n');
			}
			setCell(row, 2, extractedComments.toString());

			setCell(row, 3, translationEntry.getId());
			setCell(row, 4, translationEntry.getIdPlural());
			rowIndex++;
		}
	}

	private static void setCell(Row row, int cellIndex, String value) {
		row.createCell(cellIndex).setCellValue(value);
	}
}
