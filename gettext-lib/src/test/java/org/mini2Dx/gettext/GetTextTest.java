package org.mini2Dx.gettext;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GetTextTest {
	private static final Locale CATALAN = Locale.forLanguageTag("ca-ES");

	@BeforeClass
	public static void setUp() throws IOException  {
		final PoFile enFile = new PoFile(Locale.ENGLISH, GetTextTest.class.getResourceAsStream("/sample_en.po"));
		final PoFile caFile = new PoFile(CATALAN, GetTextTest.class.getResourceAsStream("/sample_ca.po"));
		GetText.add(enFile);
		GetText.add(caFile);

		GetText.setLocale(CATALAN);
	}

	@Test
	public void testTr() throws IOException {
		for(int i = 0; i < EN.size(); i++) {
			Assert.assertEquals(CA.get(i), GetText.tr(EN.get(i)));
		}
	}

	@Test
	public void testTrc() throws IOException {

	}

	@Test
	public void testTrn() throws IOException {

	}

	@Test
	public void testTrnc() throws IOException {

	}

	private static final List<String> EN = new ArrayList<String>() {
		{
			add("Unknown system error");
			add("found %d fatal error");
			add("Here is an example of how one might continue a very long string\nfor the common case the string represents multi-line output.\n");
		}
	};
	private static final List<String> CA = new ArrayList<String>() {
		{
			add("Error desconegut del sistema");
			add("s'ha trobat %d error fatal");
			add("Aquí teniu un exemple de com es pot continuar una cadena molt llarga per al cas comú,\nla cadena representa una sortida de diverses línies\n");
		}
	};
}
