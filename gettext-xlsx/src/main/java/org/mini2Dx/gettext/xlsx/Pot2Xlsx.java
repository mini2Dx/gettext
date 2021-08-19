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

import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
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

	public static void convertMultipleFiles(final Locale sourceLocale, final File xlsxFile, final File... poFiles) throws IOException {
		final XSSFWorkbook workbook = new XSSFWorkbook();
		final Sheet sheet = workbook.createSheet("strings");

		final XSSFFont font = workbook.createFont();
		font.setColor(IndexedColors.WHITE.getIndex());
		font.setBold(true);

		final XSSFCellStyle style = workbook.createCellStyle();
		style.setFillForegroundColor(IndexedColors.BLACK.getIndex());
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style.setFont(font);

		final Row headerRow = sheet.createRow(0);
		setCell(headerRow, 0, "Reference", style);
		setCell(headerRow, 1, "Context", style);
		setCell(headerRow, 2, "Extracted Comments", style);
		setCell(headerRow, 3, sourceLocale.getDisplayName() + " (singular)", style);
		setCell(headerRow, 4, sourceLocale.getDisplayName() + " (plural)", style);
		sheet.createFreezePane( 0, 1, 0, 1 );

		int rowIndex = 1;
		for(File poFile : poFiles) {
			final PoFile poFile1 = new PoFile(sourceLocale, poFile);
			rowIndex = writeToSheet(sourceLocale, workbook, poFile1, sheet, rowIndex);
		}

		try (OutputStream outputStream = new FileOutputStream(xlsxFile)) {
			workbook.write(outputStream);
		}
	}

	private static void convertFile(final Locale sourceLocale, final XSSFWorkbook workbook, final String relativeFilePath, final PoFile poFile) throws IOException {
		final Sheet sheet = workbook.createSheet(relativeFilePath);

		final XSSFFont font = workbook.createFont();
		font.setColor(IndexedColors.WHITE.getIndex());
		font.setBold(true);

		final XSSFCellStyle style = workbook.createCellStyle();
		style.setFillForegroundColor(IndexedColors.BLACK.getIndex());
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style.setFont(font);

		final Row headerRow = sheet.createRow(0);
		setCell(headerRow, 0, "Reference", style);
		setCell(headerRow, 1, "Context", style);
		setCell(headerRow, 2, "Extracted Comments", style);
		setCell(headerRow, 3, sourceLocale.getDisplayName() + " (singular)", style);
		setCell(headerRow, 4, sourceLocale.getDisplayName() + " (plural)", style);
		sheet.createFreezePane( 0, 1, 0, 1 );

		writeToSheet(sourceLocale, workbook, poFile, sheet, 1);
	}

	private static int writeToSheet(Locale sourceLocale, XSSFWorkbook workbook, PoFile poFile, Sheet sheet, int rowIndex) {
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
		return rowIndex;
	}

	private static void setCell(Row row, int cellIndex, String value, XSSFCellStyle style) {
		setCell(row, cellIndex, value);
		setCellStyle(row, cellIndex, style);
	}

	private static void setCell(Row row, int cellIndex, String value) {
		row.createCell(cellIndex).setCellValue(value);
	}

	private static void setCellStyle(Row row, int cellIndex, XSSFCellStyle style) {
		row.getCell(cellIndex).setCellStyle(style);
	}
}
