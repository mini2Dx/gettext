package org.mini2Dx.gettext.plugin.file;

import org.junit.Assert;
import org.junit.Test;
import org.mini2Dx.gettext.TranslationEntry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TextFileTest {
	private static final String FILENAME = "sample.txt";
	private static final String COMMENT_FORMAT = "#.";

	@Test
	public void testTextFile() throws IOException {
		final TextFile textFile = new TextFile(TextFileTest.class.getResourceAsStream("/" + FILENAME), FILENAME, COMMENT_FORMAT);
		final List<TranslationEntry> results = new ArrayList<TranslationEntry>();
		textFile.getTranslationEntries(results);

		Assert.assertEquals(4, results.size());

		final TranslationEntry entry0 = results.get(0);
		Assert.assertEquals(FILENAME + ":1", entry0.getReference());
		Assert.assertEquals("Line 0", entry0.getId());
		Assert.assertEquals(0, entry0.getExtractedComments().size());

		final TranslationEntry entry1 = results.get(1);
		Assert.assertEquals(FILENAME + ":3", entry1.getReference());
		Assert.assertEquals("Line 1", entry1.getId());
		Assert.assertEquals(1, entry1.getExtractedComments().size());
		Assert.assertEquals("Comment 0", entry1.getExtractedComments().get(0));

		final TranslationEntry entry2 = results.get(2);
		Assert.assertEquals(FILENAME + ":6", entry2.getReference());
		Assert.assertEquals("Line 2", entry2.getId());
		Assert.assertEquals(1, entry2.getExtractedComments().size());
		Assert.assertEquals("Comment 1", entry2.getExtractedComments().get(0));

		final TranslationEntry entry3 = results.get(3);
		Assert.assertEquals(FILENAME + ":7", entry3.getReference());
		Assert.assertEquals("Line 3", entry3.getId());
		Assert.assertEquals(0, entry3.getExtractedComments().size());
	}
}
