/**
 * Copyright 2020 Viridian Software Ltd.
 */
package org.mini2Dx.gettext;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Locale;

public class PoFileTest {

	@Test
	public void testReadWritePoFile() throws IOException {
		final PoFile expected = new PoFile(Locale.ENGLISH, GetTextTest.class.getResourceAsStream("/sample_en.po"));
		final File tmpFile = Files.createTempFile("", ".po").toFile();
		expected.saveTo(tmpFile);

		final PoFile result = new PoFile(Locale.ENGLISH, tmpFile);

		Assert.assertEquals(expected.getEntries().size(), result.getEntries().size());
		for(int i = 0; i < expected.getEntries().size(); i++) {
			final TranslationEntry expectedEntry = expected.getEntries().get(i);
			final TranslationEntry resultEntry = result.getEntries().get(i);
			Assert.assertEquals(expectedEntry, resultEntry);
		}
	}
}
