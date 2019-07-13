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

public class JavaFileTest {
    private static final String TR_FILENAME = "SampleTr.java";
    private static final String TRC_FILENAME = "SampleTrc.java";
    private static final String TRN_FILENAME = "SampleTrn.java";
    private static final String TRNC_FILENAME = "SampleTrnc.java";
    private static final String COMMENT_FORMAT = "#.";

    private static final String TR_CUSTOM_COMMENT_FILENAME = "SampleTrCustomComment.java";
    private static final String CUSTOM_COMMENT_FORMAT = " #. ";

	private static JavaFile TR_FILE, TRC_FILE, TRN_FILE, TRNC_FILE, TR_CUSTOM_COMMENT_FILE;

	private final List<TranslationEntry> results = new ArrayList<TranslationEntry>();

	@BeforeClass
	public static void loadFiles() throws IOException {
		TR_FILE = new JavaFile(JavaFileTest.class.getResourceAsStream("/" + TR_FILENAME), TR_FILENAME, COMMENT_FORMAT);
		TRC_FILE = new JavaFile(JavaFileTest.class.getResourceAsStream("/" + TRC_FILENAME), TRC_FILENAME, COMMENT_FORMAT);
		TRN_FILE = new JavaFile(JavaFileTest.class.getResourceAsStream("/" + TRN_FILENAME), TRN_FILENAME, COMMENT_FORMAT);
		TRNC_FILE = new JavaFile(JavaFileTest.class.getResourceAsStream("/" + TRNC_FILENAME), TRNC_FILENAME, COMMENT_FORMAT);
		TR_CUSTOM_COMMENT_FILE = new JavaFile(JavaFileTest.class.getResourceAsStream("/" + TR_CUSTOM_COMMENT_FILENAME), TR_CUSTOM_COMMENT_FILENAME, CUSTOM_COMMENT_FORMAT);
	}

	@AfterClass
	public static void disposeFiles() {
		TR_FILE.dispose();
		TRC_FILE.dispose();
		TRN_FILE.dispose();
        TRNC_FILE.dispose();
        TR_CUSTOM_COMMENT_FILE.dispose();
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
		Assert.assertEquals(TR_FILENAME + ":17", entry0.getReference());
		Assert.assertEquals("Hello World!", entry0.getId());
		Assert.assertEquals(0, entry0.getExtractedComments().size());

		final TranslationEntry entry1 = results.get(1);
		Assert.assertEquals(TR_FILENAME + ":18", entry1.getReference());
		Assert.assertEquals("Multipart same line", entry1.getId());
		Assert.assertEquals(0, entry1.getExtractedComments().size());

		final TranslationEntry entry2 = results.get(2);
		Assert.assertEquals(TR_FILENAME + ":19", entry2.getReference());
		Assert.assertEquals("Multipart multi line", entry2.getId());
		Assert.assertEquals(0, entry2.getExtractedComments().size());

		final TranslationEntry entry3 = results.get(3);
		Assert.assertEquals(TR_FILENAME + ":24", entry3.getReference());
		Assert.assertEquals("With comment", entry3.getId());
		Assert.assertEquals(1, entry3.getExtractedComments().size());
		Assert.assertEquals("Comment 1", entry3.getExtractedComments().get(0));

		final TranslationEntry entry4 = results.get(4);
		Assert.assertEquals(TR_FILENAME + ":26", entry4.getReference());
		Assert.assertEquals("Static ref", entry4.getId());
		Assert.assertEquals(0, entry4.getExtractedComments().size());

		final TranslationEntry entry5 = results.get(5);
		Assert.assertEquals(TR_FILENAME + ":30", entry5.getReference());
		Assert.assertEquals("Static ref multi part", entry5.getId());
		Assert.assertEquals(0, entry5.getExtractedComments().size());

		final TranslationEntry entry6 = results.get(6);
		Assert.assertEquals(TR_FILENAME + ":34", entry6.getReference());
		Assert.assertEquals("Static ref multi line", entry6.getId());
		Assert.assertEquals(1, entry6.getExtractedComments().size());
		Assert.assertEquals("Comment 2", entry6.getExtractedComments().get(0));
	}

	@Test
	public void testTrc() {
		TRC_FILE.getTranslationEntries(results);
		Assert.assertEquals(7, results.size());

		final TranslationEntry entry0 = results.get(0);
		Assert.assertEquals(TRC_FILENAME + ":20", entry0.getReference());
		Assert.assertEquals("ctx0", entry0.getContext());
		Assert.assertEquals("Hello World!", entry0.getId());
		Assert.assertEquals(0, entry0.getExtractedComments().size());

		final TranslationEntry entry1 = results.get(1);
		Assert.assertEquals(TRC_FILENAME + ":21", entry1.getReference());
		Assert.assertEquals("ctx1", entry1.getContext());
		Assert.assertEquals("Multipart same line", entry1.getId());
		Assert.assertEquals(0, entry1.getExtractedComments().size());

		final TranslationEntry entry2 = results.get(2);
		Assert.assertEquals(TRC_FILENAME + ":22", entry2.getReference());
		Assert.assertEquals("ctx2", entry2.getContext());
		Assert.assertEquals("Multipart multi line", entry2.getId());
		Assert.assertEquals(0, entry2.getExtractedComments().size());

		final TranslationEntry entry3 = results.get(3);
		Assert.assertEquals(TRC_FILENAME + ":27", entry3.getReference());
		Assert.assertEquals("ctx3", entry3.getContext());
		Assert.assertEquals("With comment", entry3.getId());
		Assert.assertEquals(1, entry3.getExtractedComments().size());
		Assert.assertEquals("Comment 1", entry3.getExtractedComments().get(0));

		final TranslationEntry entry4 = results.get(4);
		Assert.assertEquals(TRC_FILENAME + ":29", entry4.getReference());
		Assert.assertEquals("ctx4", entry4.getContext());
		Assert.assertEquals("Static ref", entry4.getId());
		Assert.assertEquals(0, entry4.getExtractedComments().size());

		final TranslationEntry entry5 = results.get(5);
		Assert.assertEquals(TRC_FILENAME + ":33", entry5.getReference());
		Assert.assertEquals("ctx5", entry5.getContext());
		Assert.assertEquals("Static ref multi part", entry5.getId());
		Assert.assertEquals(0, entry5.getExtractedComments().size());

		final TranslationEntry entry6 = results.get(6);
		Assert.assertEquals(TRC_FILENAME + ":37", entry6.getReference());
		Assert.assertEquals("ctx6", entry6.getContext());
		Assert.assertEquals("Static ref multi line", entry6.getId());
		Assert.assertEquals(1, entry6.getExtractedComments().size());
		Assert.assertEquals("Comment 2", entry6.getExtractedComments().get(0));
	}

	@Test
	public void testTrn() {
		TRN_FILE.getTranslationEntries(results);
		Assert.assertEquals(7, results.size());

		final TranslationEntry entry0 = results.get(0);
		Assert.assertEquals(TRN_FILENAME + ":22", entry0.getReference());
		Assert.assertEquals("Hello World!", entry0.getId());
		Assert.assertEquals("Hello Worlds!", entry0.getIdPlural());
		Assert.assertEquals(0, entry0.getExtractedComments().size());

		final TranslationEntry entry1 = results.get(1);
		Assert.assertEquals(TRN_FILENAME + ":23", entry1.getReference());
		Assert.assertEquals("Multipart same line", entry1.getId());
		Assert.assertEquals("Multiparts same line", entry1.getIdPlural());
		Assert.assertEquals(0, entry1.getExtractedComments().size());

		final TranslationEntry entry2 = results.get(2);
		Assert.assertEquals(TRN_FILENAME + ":24", entry2.getReference());
		Assert.assertEquals("Multipart multi line", entry2.getId());
		Assert.assertEquals("Multipart multi lines", entry2.getIdPlural());
		Assert.assertEquals(0, entry2.getExtractedComments().size());

		final TranslationEntry entry3 = results.get(3);
		Assert.assertEquals(TRN_FILENAME + ":31", entry3.getReference());
		Assert.assertEquals("With comment", entry3.getId());
		Assert.assertEquals("With commentz", entry3.getIdPlural());
		Assert.assertEquals(1, entry3.getExtractedComments().size());
		Assert.assertEquals("Comment 1", entry3.getExtractedComments().get(0));

		final TranslationEntry entry4 = results.get(4);
		Assert.assertEquals(TRN_FILENAME + ":33", entry4.getReference());
		Assert.assertEquals("Static ref", entry4.getId());
		Assert.assertEquals("Static ref plural", entry4.getIdPlural());
		Assert.assertEquals(0, entry4.getExtractedComments().size());

		final TranslationEntry entry5 = results.get(5);
		Assert.assertEquals(TRN_FILENAME + ":38", entry5.getReference());
		Assert.assertEquals("Static ref multi part", entry5.getId());
		Assert.assertEquals("Static ref multi part plural", entry5.getIdPlural());
		Assert.assertEquals(0, entry5.getExtractedComments().size());

		final TranslationEntry entry6 = results.get(6);
		Assert.assertEquals(TRN_FILENAME + ":42", entry6.getReference());
		Assert.assertEquals("Static ref multi line", entry6.getId());
		Assert.assertEquals("Static ref multi lines", entry6.getIdPlural());
		Assert.assertEquals(1, entry6.getExtractedComments().size());
		Assert.assertEquals("Comment 2", entry6.getExtractedComments().get(0));
	}

	@Test
	public void testTrnc() {
		TRNC_FILE.getTranslationEntries(results);
		Assert.assertEquals(7, results.size());

		final TranslationEntry entry0 = results.get(0);
		Assert.assertEquals(TRNC_FILENAME + ":25", entry0.getReference());
		Assert.assertEquals("ctx0", entry0.getContext());
		Assert.assertEquals("Hello World!", entry0.getId());
		Assert.assertEquals("Hello Worlds!", entry0.getIdPlural());
		Assert.assertEquals(0, entry0.getExtractedComments().size());

		final TranslationEntry entry1 = results.get(1);
		Assert.assertEquals(TRNC_FILENAME + ":26", entry1.getReference());
		Assert.assertEquals("ctx1", entry1.getContext());
		Assert.assertEquals("Multipart same line", entry1.getId());
		Assert.assertEquals("Multiparts same line", entry1.getIdPlural());
		Assert.assertEquals(0, entry1.getExtractedComments().size());

		final TranslationEntry entry2 = results.get(2);
		Assert.assertEquals(TRNC_FILENAME + ":27", entry2.getReference());
		Assert.assertEquals("ctx2", entry2.getContext());
		Assert.assertEquals("Multipart multi line", entry2.getId());
		Assert.assertEquals("Multipart multi lines", entry2.getIdPlural());
		Assert.assertEquals(0, entry2.getExtractedComments().size());

		final TranslationEntry entry3 = results.get(3);
		Assert.assertEquals(TRNC_FILENAME + ":34", entry3.getReference());
		Assert.assertEquals("ctx3", entry3.getContext());
		Assert.assertEquals("With comment", entry3.getId());
		Assert.assertEquals("With commentz", entry3.getIdPlural());
		Assert.assertEquals(1, entry3.getExtractedComments().size());
		Assert.assertEquals("Comment 1", entry3.getExtractedComments().get(0));

		final TranslationEntry entry4 = results.get(4);
		Assert.assertEquals(TRNC_FILENAME + ":36", entry4.getReference());
		Assert.assertEquals("ctx4", entry4.getContext());
		Assert.assertEquals("Static ref", entry4.getId());
		Assert.assertEquals("Static ref plural", entry4.getIdPlural());
		Assert.assertEquals(0, entry4.getExtractedComments().size());

		final TranslationEntry entry5 = results.get(5);
		Assert.assertEquals(TRNC_FILENAME + ":41", entry5.getReference());
		Assert.assertEquals("ctx5", entry5.getContext());
		Assert.assertEquals("Static ref multi part", entry5.getId());
		Assert.assertEquals("Static ref multi part plural", entry5.getIdPlural());
		Assert.assertEquals(0, entry5.getExtractedComments().size());

		final TranslationEntry entry6 = results.get(6);
		Assert.assertEquals(TRNC_FILENAME + ":45", entry6.getReference());
		Assert.assertEquals("ctx6", entry6.getContext());
		Assert.assertEquals("Static ref multi line", entry6.getId());
		Assert.assertEquals("Static ref multi lines", entry6.getIdPlural());
		Assert.assertEquals(1, entry6.getExtractedComments().size());
		Assert.assertEquals("Comment 2", entry6.getExtractedComments().get(0));
    }

	@Test
	public void testTrCustomComment() {
		TR_CUSTOM_COMMENT_FILE.getTranslationEntries(results);
		Assert.assertEquals(7, results.size());

		final TranslationEntry entry0 = results.get(0);
		Assert.assertEquals(TR_CUSTOM_COMMENT_FILENAME + ":17", entry0.getReference());
		Assert.assertEquals("Hello World!", entry0.getId());
		Assert.assertEquals(0, entry0.getExtractedComments().size());

		final TranslationEntry entry1 = results.get(1);
		Assert.assertEquals(TR_CUSTOM_COMMENT_FILENAME + ":18", entry1.getReference());
		Assert.assertEquals("Multipart same line", entry1.getId());
		Assert.assertEquals(0, entry1.getExtractedComments().size());

		final TranslationEntry entry2 = results.get(2);
		Assert.assertEquals(TR_CUSTOM_COMMENT_FILENAME + ":19", entry2.getReference());
		Assert.assertEquals("Multipart multi line", entry2.getId());
		Assert.assertEquals(0, entry2.getExtractedComments().size());

		final TranslationEntry entry3 = results.get(3);
		Assert.assertEquals(TR_CUSTOM_COMMENT_FILENAME + ":24", entry3.getReference());
		Assert.assertEquals("With comment", entry3.getId());
		Assert.assertEquals(1, entry3.getExtractedComments().size());
		Assert.assertEquals("Comment 1", entry3.getExtractedComments().get(0));

		final TranslationEntry entry4 = results.get(4);
		Assert.assertEquals(TR_CUSTOM_COMMENT_FILENAME + ":26", entry4.getReference());
		Assert.assertEquals("Static ref", entry4.getId());
		Assert.assertEquals(0, entry4.getExtractedComments().size());

		final TranslationEntry entry5 = results.get(5);
		Assert.assertEquals(TR_CUSTOM_COMMENT_FILENAME + ":30", entry5.getReference());
		Assert.assertEquals("Static ref multi part", entry5.getId());
		Assert.assertEquals(0, entry5.getExtractedComments().size());

		final TranslationEntry entry6 = results.get(6);
		Assert.assertEquals(TR_CUSTOM_COMMENT_FILENAME + ":34", entry6.getReference());
		Assert.assertEquals("Static ref multi line", entry6.getId());
		Assert.assertEquals(1, entry6.getExtractedComments().size());
		Assert.assertEquals("Comment 2", entry6.getExtractedComments().get(0));
	}
}
