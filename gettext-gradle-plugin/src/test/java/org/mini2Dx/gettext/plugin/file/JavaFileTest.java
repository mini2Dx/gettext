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
	private static JavaFile TR_FILE;

	private final List<TranslationEntry> results = new ArrayList<TranslationEntry>();

	@BeforeClass
	public static void loadFiles() throws IOException {
		TR_FILE = new JavaFile(JavaFileTest.class.getResourceAsStream("/" + TR_FILENAME), TR_FILENAME);
	}

	@AfterClass
	public static void disposeFiles() {
		TR_FILE.dispose();
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
		Assert.assertEquals(TR_FILENAME + ":15", entry0.getReference());
		Assert.assertEquals("Hello World!", entry0.getId());
		Assert.assertEquals(0, entry0.getExtractedComments().size());

		final TranslationEntry entry1 = results.get(1);
		Assert.assertEquals(TR_FILENAME + ":16", entry1.getReference());
		Assert.assertEquals("Multipart same line", entry1.getId());
		Assert.assertEquals(0, entry1.getExtractedComments().size());

		final TranslationEntry entry2 = results.get(2);
		Assert.assertEquals(TR_FILENAME + ":17", entry2.getReference());
		Assert.assertEquals("Multipart multi line", entry2.getId());
		Assert.assertEquals(0, entry2.getExtractedComments().size());

		final TranslationEntry entry3 = results.get(3);
		Assert.assertEquals(TR_FILENAME + ":22", entry3.getReference());
		Assert.assertEquals("With comment", entry3.getId());
		Assert.assertEquals(1, entry3.getExtractedComments().size());
		Assert.assertEquals("Comment 1", entry3.getExtractedComments().get(0));
	}

	@Test
	public void testTrc() {

	}

	@Test
	public void testTrn() {

	}

	@Test
	public void testTrnc() {

	}
}
