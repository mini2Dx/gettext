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
package org.mini2Dx.gettext;

import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GetTextTest {
	private static final Locale CATALAN = Locale.forLanguageTag("ca-ES");

	private static PoFile EN_FILE, CA_FILE, JP_FILE, EMPTY_FILE, HEADER_FILE;

	@BeforeClass
	public static void setUp() throws IOException  {
		EN_FILE = new PoFile(Locale.ENGLISH, GetTextTest.class.getResourceAsStream("/sample_en.po"));
		CA_FILE = new PoFile(CATALAN, GetTextTest.class.getResourceAsStream("/sample_ca.po"));
		JP_FILE = new PoFile(Locale.JAPAN, GetTextTest.class.getResourceAsStream("/sample_jp.po"));
		EMPTY_FILE = new PoFile(Locale.ROOT, GetTextTest.class.getResourceAsStream("/sample_empty.po"));
		HEADER_FILE = new PoFile(Locale.KOREA, GetTextTest.class.getResourceAsStream("/sample_header.po"));

		GetText.add(EN_FILE);
		GetText.add(CA_FILE);
		GetText.add(JP_FILE);
		GetText.add(EMPTY_FILE);
	}

	@Test(expected = ParseCancellationException.class)
	public void testExceptionOnParseFailure() throws IOException {
		new PoFile(Locale.ENGLISH, GetTextTest.class.getResourceAsStream("/sample_error.po"));
	}

	@Test
	public void testGetLocale() {
		GetText.setLocale(Locale.ROOT);
		Assert.assertEquals(Locale.ROOT, GetText.getLocale());
		GetText.setLocale(Locale.KOREA);
		Assert.assertEquals(Locale.KOREA, GetText.getLocale());
	}

	@Test
	public void testEmptyTranslation() {
		GetText.setLocale(Locale.ROOT);
		final String id1 = "Translation is empty";
		Assert.assertEquals(id1, GetText.tr(id1));
	}

	@Test
	public void testHeader() {
		GetText.setLocale(Locale.KOREA);
		final String id1 = "Translation in header file";
		Assert.assertEquals(id1, GetText.tr(id1));
	}

	@Test
	public void testEntries() {
		Assert.assertEquals(4, CA_FILE.getEntries().size());
		Assert.assertEquals("Unknown \"system\" error", CA_FILE.getEntries().get(0).getId());
	}

	@Test
	public void testTr() throws IOException {
		GetText.setLocale(CATALAN);

		for(int i = 0; i < EN.size(); i++) {
			Assert.assertEquals(CA.get(i), GetText.tr(EN.get(i)));
		}
	}

	@Test
	public void testTrWithNestedQuotes() throws IOException {
		GetText.setLocale(CATALAN);

		final String id1 = "Unknown \"system\" error";
		final String result1 = "Error desconegut del \"sistema\"";
		Assert.assertEquals(result1, GetText.tr(id1));

		final String id2 = "Unknown \"systems\" error";
		final String result2 = "Error desconegut del \"sistemas\"";
		Assert.assertEquals(result2, GetText.tr(id2));
	}

	@Test
	public void testTrc() throws IOException {
		GetText.setLocale(Locale.JAPAN);

		final String context = "system context";
		final String id = "Unknown \"system\" error";
		final String result = "不明なシステムエラー";
		Assert.assertEquals(result, GetText.trc(context, id));
	}

	@Test
	public void testTrn() throws IOException {
		GetText.setLocale(CATALAN);

		final String id = "found {0} fatal error";
		final String idPlural = "found {0} fatal errors";

		Assert.assertEquals("s'ha trobat {0} error fatal", GetText.trn(id, idPlural, 0));
		Assert.assertEquals("s'han trobat {0} errors fatals", GetText.trn(id, idPlural, 1));
	}

	@Test
	public void testTrnc() throws IOException {
		GetText.setLocale(Locale.JAPAN);

		final String context = "system context";
		final String id = "found {0} fatal error";
		final String idPlural = "found {0} fatal errors";

		Assert.assertEquals("{0}致命的なエラーを発見", GetText.trnc(context, id, idPlural, 0));
		Assert.assertEquals("{0}致命的なエラーを発見", GetText.trnc(context, id, idPlural, 1));
	}

	@Test
	public void testTrWithValues() throws IOException {
		GetText.setLocale(CATALAN);

		Assert.assertEquals("s'ha trobat 7 error fatal", GetText.tr("found {0} fatal error", 7));
		Assert.assertEquals("s'ha trobat 7 error fatal", GetText.tr("found {0} fatal error", 7));
	}

	@Test
	public void testTrWithValuesNoLocale() throws IOException {
		GetText.setLocale(Locale.ENGLISH);

		Assert.assertEquals("found 7 fatal error", GetText.tr("found {0} fatal error", 7));
		Assert.assertEquals("found 7 fatal error", GetText.tr("found {0} fatal error", new Object[] { 7 }));
	}

	@Test
	public void testTrWithValuesNoStringMatchOrLocale() throws IOException {
		GetText.setLocale(Locale.CANADA_FRENCH);

		Assert.assertEquals("example's 7 fatal error", GetText.tr("example's {0} fatal error", 7));
	}

	@Test
	public void testTrcWithValues() throws IOException {
		GetText.setLocale(Locale.JAPAN);

		final String context = "system context";
		final String id = "found {0} fatal error";
		final String result = "{0}致命的なエラーを発見";
		Assert.assertEquals(result, GetText.trc(context, id));
	}

	@Test
	public void testTrcWithValuesNoStringMatchOrLocale() throws IOException {
		GetText.setLocale(Locale.CANADA_FRENCH);

		Assert.assertEquals("example's 7 fatal error", GetText.trc("context", "example's {0} fatal error", 7));
	}

	@Test
	public void testTrnWithValues() throws IOException {
		GetText.setLocale(CATALAN);

		final String id = "found {0} fatal error";
		final String idPlural = "found {0} fatal errors";

		Assert.assertEquals("s'ha trobat 7 error fatal", GetText.trn(id, idPlural, 0, 7));
		Assert.assertEquals("s'han trobat 8 errors fatals", GetText.trn(id, idPlural, 1, 8));
	}

	@Test
	public void testTrnWithValuesNoStringMatchOrLocale() throws IOException {
		GetText.setLocale(Locale.CANADA_FRENCH);

		Assert.assertEquals("example's 7 fatal errors", GetText.trn("example's {0} fatal error", "example's {0} fatal errors", 2, 7));
	}

	@Test
	public void testTrncWithValues() throws IOException {
		GetText.setLocale(Locale.JAPAN);

		final String context = "system context";
		final String id = "found {0} fatal error";
		final String idPlural = "found {0} fatal errors";

		Assert.assertEquals("9致命的なエラーを発見", GetText.trnc(context, id, idPlural, 0, 9));
		Assert.assertEquals("4致命的なエラーを発見", GetText.trnc(context, id, idPlural, 1, 4));
	}

	@Test
	public void testTrncWithValuesNoStringMatchOrLocale() throws IOException {
		GetText.setLocale(Locale.CANADA_FRENCH);

		Assert.assertEquals("example's 7 fatal errors", GetText.trnc("context","example's {0} fatal error", "example's {0} fatal errors", 2, 7));
	}

	@Test
	public void testTranslatorComments() throws IOException {
		GetText.setLocale(CATALAN);

		final String id = "Unknown \"system\" error";
		final TranslationEntry entry = GetText.getTranslationEntry(CATALAN, null, id);

		for(int i = 0; i < 2; i++) {
			Assert.assertEquals("translator-comments " + i, entry.getTranslatorComments().get(i).trim());
		}
	}

	@Test
	public void testExtractedComments() throws IOException {
		GetText.setLocale(CATALAN);

		final String id = "Unknown \"system\" error";
		final TranslationEntry entry = GetText.getTranslationEntry(CATALAN, null, id);

		for(int i = 0; i < 2; i++) {
			Assert.assertEquals("extracted-comments " + i, entry.getExtractedComments().get(i).trim());
		}
	}

	@Test
	public void testMergeComments() throws IOException {
		GetText.setLocale(CATALAN);

		final String id = "Unknown \"system\" error";
		final TranslationEntry entry = GetText.getTranslationEntry(CATALAN, null, id);

		for(int i = 0; i < 2; i++) {
			Assert.assertEquals("merge-comment " + i, entry.getMergeComments().get(i).trim());
		}
	}

	@Test
	public void testReference() throws IOException {
		GetText.setLocale(CATALAN);

		final String id = "Unknown \"system\" error";
		final TranslationEntry entry = GetText.getTranslationEntry(CATALAN, null, id);

		Assert.assertEquals("src/msgcmp.java:322", entry.getReference().trim());
	}

	@Test
	public void testFlag() throws IOException {
		GetText.setLocale(CATALAN);

		final String id = "Unknown \"system\" error";
		final TranslationEntry entry = GetText.getTranslationEntry(CATALAN, null, id);

		for(int i = 0; i < 2; i++) {
			Assert.assertEquals("flag " + i, entry.getFlags().get(i).trim());
		}
	}

	private static final List<String> EN = new ArrayList<String>() {
		{
			add("Unknown \"system\" error");
			add("found {0} fatal error");
			add("Here is an example of how one might continue a very long string\\nfor the common case the string represents multi-line output.\\n");
		}
	};
	private static final List<String> CA = new ArrayList<String>() {
		{
			add("Error desconegut del \"sistema\"");
			add("s'ha trobat {0} error fatal");
			add("Aquí teniu un exemple de com es pot continuar una cadena molt llarga per al cas comú,\\nla cadena representa una sortida de diverses línies\\n");
		}
	};
}
