package org.mini2Dx.gettext.xlsx;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Assert;
import org.junit.Test;
import org.mini2Dx.gettext.PoFile;
import org.mini2Dx.gettext.TranslationEntry;

import java.io.*;
import java.nio.file.Files;
import java.util.Locale;

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

public class PoGenerationTest {

	@Test
	public void testConvertToXlsxThenToPo() throws IOException, InvalidFormatException {
		final File potFile = Files.createTempFile("sample.pot", "").toFile();
		final File xlsxFile = Files.createTempFile("sample.xlsx", "").toFile();
		final File poDirectory = Files.createTempDirectory("po").toFile();

		copySamplePotTo(potFile);
		final PoFile expectedPoFile = new PoFile(Locale.ENGLISH, potFile);

		Pot2Xlsx.convertFile(Locale.ENGLISH, potFile, xlsxFile);
		appendLanguageColumn(xlsxFile, Locale.SIMPLIFIED_CHINESE);
		appendLanguageColumn(xlsxFile, Locale.TRADITIONAL_CHINESE);

		Xlsx2Po.convertFile(xlsxFile, poDirectory);

		for(File localeDirectory : poDirectory.listFiles()) {
			if(!localeDirectory.isDirectory()) {
				continue;
			}
			final Locale locale = Locale.forLanguageTag(localeDirectory.getName());
			for(File poFile : localeDirectory.listFiles()) {
				final PoFile actualPoFile = new PoFile(locale, poFile);
				Assert.assertEquals(expectedPoFile.getEntries().size(), actualPoFile.getEntries().size());

				for(int i = 0; i < expectedPoFile.getEntries().size(); i++) {
					final TranslationEntry expectedEntry = expectedPoFile.getEntries().get(i);
					final TranslationEntry actualEntry = actualPoFile.getEntries().get(i);

					Assert.assertEquals(expectedEntry.getReference(), actualEntry.getReference());
					Assert.assertEquals(expectedEntry.getContext(), actualEntry.getContext());
					Assert.assertEquals(expectedEntry.getId(), actualEntry.getId());
					Assert.assertEquals(expectedEntry.getIdPlural(), actualEntry.getIdPlural());

					Assert.assertEquals(1, actualEntry.getStrings().size());
					Assert.assertEquals(expectedEntry.getId() + " (translated)", actualEntry.getStrings().get(0));
				}
			}
		}
	}

	private void appendLanguageColumn(File xlsxFile, Locale translatedLanguage) throws IOException, InvalidFormatException {
		final XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(xlsxFile));
		for(int sheetIndex = 0; sheetIndex < workbook.getNumberOfSheets(); sheetIndex++) {
			final Sheet sheet = workbook.getSheetAt(sheetIndex);

			final Row headerRow = sheet.getRow(0);
			final int translationColumn = headerRow.getLastCellNum();

			headerRow.createCell(translationColumn).setCellValue(translatedLanguage.getDisplayName(Locale.ENGLISH));

			for(int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
				final Row entryRow = sheet.getRow(rowIndex);
				entryRow.createCell(translationColumn).setCellValue(entryRow.getCell(3).getStringCellValue() + " (translated)");
			}
		}
		workbook.write(new FileOutputStream(xlsxFile));
	}

	private void copySamplePotTo(File outputFile) throws IOException {
		final InputStream inputStream = PoGenerationTest.class.getResourceAsStream("/sample.pot");
		final OutputStream outputStream = new FileOutputStream(outputFile);
		IOUtils.copy(inputStream, outputStream);
		inputStream.close();
		outputStream.close();
	}
}
