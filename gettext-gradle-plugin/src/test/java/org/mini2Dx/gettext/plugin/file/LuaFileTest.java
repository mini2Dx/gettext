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

import org.junit.*;
import org.mini2Dx.gettext.TranslationEntry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LuaFileTest {
	private static final String TR_FILENAME = "sampleTr.lua";
	private static final String TRC_FILENAME = "sampleTrc.lua";
	private static final String TRN_FILENAME = "sampleTrn.lua";
	private static final String TRNC_FILENAME = "sampleTrnc.lua";
	private static final String COMMENT_FORMAT = "#.";
	private static final String FORCE_EXTRACT_FORMAT = "#!extract";
	private static final String IGNORE_FORMAT = "#!ignore";

	private static final String TR_CUSTOM_COMMENT_FILENAME = "sampleTrCustomComment.lua";
	private static final String CUSTOM_COMMENT_FORMAT = " #. ";

	private static LuaFile TR_FILE, TRC_FILE, TRN_FILE, TRNC_FILE, TR_CUSTOM_COMMENT_FILE;

	private final List<TranslationEntry> results = new ArrayList<TranslationEntry>();

	@BeforeClass
	public static void loadFiles() throws IOException {
		try {
			TR_FILE = new LuaFile(LuaFileTest.class.getResourceAsStream("/" + TR_FILENAME), TR_FILENAME,
					COMMENT_FORMAT, FORCE_EXTRACT_FORMAT, IGNORE_FORMAT);
			TRC_FILE = new LuaFile(LuaFileTest.class.getResourceAsStream("/" + TRC_FILENAME), TRC_FILENAME,
					COMMENT_FORMAT, FORCE_EXTRACT_FORMAT, IGNORE_FORMAT);
			TRN_FILE = new LuaFile(LuaFileTest.class.getResourceAsStream("/" + TRN_FILENAME), TRN_FILENAME,
					COMMENT_FORMAT, FORCE_EXTRACT_FORMAT, IGNORE_FORMAT);
			TRNC_FILE = new LuaFile(LuaFileTest.class.getResourceAsStream("/" + TRNC_FILENAME), TRNC_FILENAME,
					COMMENT_FORMAT, FORCE_EXTRACT_FORMAT, IGNORE_FORMAT);
			TR_CUSTOM_COMMENT_FILE = new LuaFile(LuaFileTest.class.getResourceAsStream("/" + TR_CUSTOM_COMMENT_FILENAME), TR_CUSTOM_COMMENT_FILENAME,
					CUSTOM_COMMENT_FORMAT, FORCE_EXTRACT_FORMAT, IGNORE_FORMAT);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@AfterClass
	public static void disposeFiles() {
		dispose(TR_FILE);
		dispose(TRC_FILE);
		dispose(TRN_FILE);
		dispose(TRNC_FILE);
		dispose(TR_CUSTOM_COMMENT_FILE);
	}

	private static void dispose(LuaFile file) {
		if(file == null) {
			return;
		}
		file.dispose();
	}

	@After
	public void teardown() {
		results.clear();
	}

	@Test
	public void testTr() {
		TR_FILE.getTranslationEntries(results);
		Assert.assertEquals(7, results.size());

		final TranslationEntry entry0 = results.get(0);
		Assert.assertEquals(TR_FILENAME + ":1", entry0.getReference());
		Assert.assertEquals("Simple", entry0.getId());
		Assert.assertEquals(0, entry0.getExtractedComments().size());

		final TranslationEntry entry1 = results.get(1);
		Assert.assertEquals(TR_FILENAME + ":3", entry1.getReference());
		Assert.assertEquals("Multi part", entry1.getId());
		Assert.assertEquals(0, entry1.getExtractedComments().size());

		final TranslationEntry entry2 = results.get(2);
		Assert.assertEquals(TR_FILENAME + ":6", entry2.getReference());
		Assert.assertEquals("Variable Ref", entry2.getId());
		Assert.assertEquals(0, entry2.getExtractedComments().size());

		final TranslationEntry entry3 = results.get(3);
		Assert.assertEquals(TR_FILENAME + ":9", entry3.getReference());
		Assert.assertEquals("Tr with args and comment", entry3.getId());
		Assert.assertEquals(1, entry3.getExtractedComments().size());
		Assert.assertEquals("Comment 1", entry3.getExtractedComments().get(0));

		final TranslationEntry entry4 = results.get(4);
		Assert.assertEquals(TR_FILENAME + ":12", entry4.getReference());
		Assert.assertEquals("value1", entry4.getId());
		Assert.assertEquals(0, entry4.getExtractedComments().size());

		final TranslationEntry entry5 = results.get(5);
		Assert.assertEquals(TR_FILENAME + ":12", entry5.getReference());
		Assert.assertEquals("value2", entry5.getId());
		Assert.assertEquals(0, entry5.getExtractedComments().size());

		final TranslationEntry entry6 = results.get(6);
		Assert.assertEquals(TR_FILENAME + ":15", entry6.getReference());
		Assert.assertEquals("value0", entry6.getId());
		Assert.assertEquals(1, entry6.getExtractedComments().size());
		Assert.assertEquals("Comment 2", entry6.getExtractedComments().get(0));
	}

	@Test
	public void testTrc() {
		TRC_FILE.getTranslationEntries(results);

		Assert.assertEquals(4, results.size());

		final TranslationEntry entry0 = results.get(0);
		Assert.assertEquals(TRC_FILENAME + ":1", entry0.getReference());
		Assert.assertEquals("ctx0", entry0.getContext());
		Assert.assertEquals("Simple", entry0.getId());
		Assert.assertEquals(0, entry0.getExtractedComments().size());

		final TranslationEntry entry1 = results.get(1);
		Assert.assertEquals(TRC_FILENAME + ":3", entry1.getReference());
		Assert.assertEquals("ctx1", entry1.getContext());
		Assert.assertEquals("Multi part", entry1.getId());
		Assert.assertEquals(0, entry1.getExtractedComments().size());

		final TranslationEntry entry2 = results.get(2);
		Assert.assertEquals(TRC_FILENAME + ":6", entry2.getReference());
		Assert.assertEquals("ctx2", entry2.getContext());
		Assert.assertEquals("Variable Ref", entry2.getId());
		Assert.assertEquals(0, entry2.getExtractedComments().size());

		final TranslationEntry entry3 = results.get(3);
		Assert.assertEquals(TRC_FILENAME + ":9", entry3.getReference());
		Assert.assertEquals("ctx3", entry3.getContext());
		Assert.assertEquals("Tr with args and comment", entry3.getId());
		Assert.assertEquals(1, entry3.getExtractedComments().size());
		Assert.assertEquals("Comment 1", entry3.getExtractedComments().get(0));
	}

	@Test
	public void testTrn() {
		TRN_FILE.getTranslationEntries(results);

		Assert.assertEquals(4, results.size());

		final TranslationEntry entry0 = results.get(0);
		Assert.assertEquals(TRN_FILENAME + ":1", entry0.getReference());
		Assert.assertEquals("Simple", entry0.getId());
		Assert.assertEquals("Simples", entry0.getIdPlural());
		Assert.assertEquals(0, entry0.getExtractedComments().size());

		final TranslationEntry entry1 = results.get(1);
		Assert.assertEquals(TRN_FILENAME + ":3", entry1.getReference());
		Assert.assertEquals("Multi part", entry1.getId());
		Assert.assertEquals("Multi parts", entry1.getIdPlural());
		Assert.assertEquals(0, entry1.getExtractedComments().size());

		final TranslationEntry entry2 = results.get(2);
		Assert.assertEquals(TRN_FILENAME + ":7", entry2.getReference());
		Assert.assertEquals("Variable Ref", entry2.getId());
		Assert.assertEquals("Variable Refs", entry2.getIdPlural());
		Assert.assertEquals(0, entry2.getExtractedComments().size());

		final TranslationEntry entry3 = results.get(3);
		Assert.assertEquals(TRN_FILENAME + ":10", entry3.getReference());
		Assert.assertEquals("Tr with args and comment", entry3.getId());
		Assert.assertEquals("Tr with args and comment plural", entry3.getIdPlural());
		Assert.assertEquals(1, entry3.getExtractedComments().size());
		Assert.assertEquals("Comment 1", entry3.getExtractedComments().get(0));
	}

	@Test
	public void testTrnc() {
		TRNC_FILE.getTranslationEntries(results);

		Assert.assertEquals(4, results.size());

		final TranslationEntry entry0 = results.get(0);
		Assert.assertEquals(TRNC_FILENAME + ":1", entry0.getReference());
		Assert.assertEquals("ctx0", entry0.getContext());
		Assert.assertEquals("Simple", entry0.getId());
		Assert.assertEquals("Simples", entry0.getIdPlural());
		Assert.assertEquals(0, entry0.getExtractedComments().size());

		final TranslationEntry entry1 = results.get(1);
		Assert.assertEquals(TRNC_FILENAME + ":3", entry1.getReference());
		Assert.assertEquals("ctx1", entry1.getContext());
		Assert.assertEquals("Multi part", entry1.getId());
		Assert.assertEquals("Multi parts", entry1.getIdPlural());
		Assert.assertEquals(0, entry1.getExtractedComments().size());

		final TranslationEntry entry2 = results.get(2);
		Assert.assertEquals(TRNC_FILENAME + ":8", entry2.getReference());
		Assert.assertEquals("ctx2", entry2.getContext());
		Assert.assertEquals("Variable Ref", entry2.getId());
		Assert.assertEquals("Variable Refs", entry2.getIdPlural());
		Assert.assertEquals(0, entry2.getExtractedComments().size());

		final TranslationEntry entry3 = results.get(3);
		Assert.assertEquals(TRNC_FILENAME + ":11", entry3.getReference());
		Assert.assertEquals("ctx3", entry3.getContext());
		Assert.assertEquals("Tr with args and comment", entry3.getId());
		Assert.assertEquals("Tr with args and comment plural", entry3.getIdPlural());
		Assert.assertEquals(1, entry3.getExtractedComments().size());
		Assert.assertEquals("Comment 1", entry3.getExtractedComments().get(0));
	}

	@Test
	public void testTrCustomComment() {
		TR_CUSTOM_COMMENT_FILE.getTranslationEntries(results);

		Assert.assertEquals(4, results.size());

		final TranslationEntry entry0 = results.get(0);
		Assert.assertEquals(TR_CUSTOM_COMMENT_FILENAME + ":1", entry0.getReference());
		Assert.assertEquals("Simple", entry0.getId());
		Assert.assertEquals(0, entry0.getExtractedComments().size());

		final TranslationEntry entry1 = results.get(1);
		Assert.assertEquals(TR_CUSTOM_COMMENT_FILENAME + ":3", entry1.getReference());
		Assert.assertEquals("Multi part", entry1.getId());
		Assert.assertEquals(0, entry1.getExtractedComments().size());

		final TranslationEntry entry2 = results.get(2);
		Assert.assertEquals(TR_CUSTOM_COMMENT_FILENAME + ":6", entry2.getReference());
		Assert.assertEquals("Variable Ref", entry2.getId());
		Assert.assertEquals(0, entry2.getExtractedComments().size());

		final TranslationEntry entry3 = results.get(3);
		Assert.assertEquals(TR_CUSTOM_COMMENT_FILENAME + ":9", entry3.getReference());
		Assert.assertEquals("Tr with args and comment", entry3.getId());
		Assert.assertEquals(1, entry3.getExtractedComments().size());
		Assert.assertEquals("Comment 1", entry3.getExtractedComments().get(0));
	}
}
