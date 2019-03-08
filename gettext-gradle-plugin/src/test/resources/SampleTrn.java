import org.mini2Dx.gettext.GetText;

import java.util.Locale;

/**
 *  HelloWorld.java
 */
public class HelloWorld  {
	private static final String STATIC_REF = "Static ref";
	private static final String STATIC_REF_PLURAL = "Static ref plural";
	private static final String STATIC_REF_MULTI_PART = "Static ref " + "multi " + "part";
	private static final String STATIC_REF_MULTI_PART_PLURAL = "Static ref " + "multi " + "part plural";
	private static final String STATIC_REF_MULTI_LINE = "Static ref " +
			"multi " +
			"line";
	private static final String STATIC_REF_MULTI_LINE_PLURAL = "Static ref " +
			"multi " +
			"lines";

	public static void tr() {
		System.out.println("Non translated string 1");
		System.out.println(GetText.trn("Hello World!", "Hello Worlds!", 2, 1));
		final String result = GetText.trn("Multipart " + "same " + "line" , "Multiparts " + "same " + "line", 1);
		System.out.println(GetText.trn("Multipart " +
				"multi " +
				"line", "Multipart " +
				"multi " +
				"lines", 0));

		//#.Comment 1
		System.out.println(GetText.trn("With comment", "With commentz", 2));
		System.out.println(
				GetText.trn(STATIC_REF, STATIC_REF_PLURAL, 1));
		System.out.println("Non translated string 2");

		final String result2 = STATIC_REF_MULTI_PART;
		final String result3 = STATIC_REF_MULTI_PART_PLURAL;
		System.out.println(GetText.trn(Locale.ENGLISH,
				result2, result3, 4));

		//#.Comment 2
		System.out.println(GetText.trn(Locale.ENGLISH, STATIC_REF_MULTI_LINE, STATIC_REF_MULTI_LINE_PLURAL, 7, 90));
	}
}