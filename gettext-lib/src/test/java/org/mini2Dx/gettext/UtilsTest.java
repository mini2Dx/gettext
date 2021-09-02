/**
 * Copyright 2021 Viridian Software Ltd.
 */
package org.mini2Dx.gettext;

import org.junit.Assert;
import org.junit.Test;

public class UtilsTest {
	@Test
	public void testEscapeDoubleQuotes() {
		Assert.assertEquals("\\\"Hello\\\"" , Utils.escapeDoubleQuotes("\"Hello\""));
		Assert.assertEquals("\\\"Hello\\\"" , Utils.escapeDoubleQuotes("\\\"Hello\\\""));
		Assert.assertEquals("\\\\\"Hello\\\\\"" , Utils.escapeDoubleQuotes("\\\\\"Hello\\\\\""));
	}
}
