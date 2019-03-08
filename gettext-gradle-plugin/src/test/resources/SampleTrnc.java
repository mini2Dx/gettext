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

	private static final String CONTEXT_3 = "ctx3";
	private static final String CONTEXT_6 = "ctx6";

	public static void tr() {
		System.out.println("Non translated string 1");
		System.out.println(GetText.trnc("ctx0", "Hello World!", "Hello Worlds!", 1));
		final String result = GetText.trnc("ctx1","Multipart " + "same " + "line","Multiparts " + "same " + "line",0);
		System.out.println(GetText.trnc("ctx2", "Multipart " +
				"multi " +
				"line", "Multipart " +
				"multi " +
				"lines", 3));

		//#.Comment 1
		System.out.println(GetText.trnc(CONTEXT_3, "With comment", "With commentz", 1, 77));
		System.out.println(
				GetText.trnc("ctx4", STATIC_REF, STATIC_REF_PLURAL, 2));
		System.out.println("Non translated string 2");

		final String result2 = STATIC_REF_MULTI_PART;
		final String result3 = STATIC_REF_MULTI_PART_PLURAL;
		System.out.println(GetText.trnc("ctx5",
				result2, result3, 2));

		//#.Comment 2
		System.out.println(GetText.trnc(Locale.ENGLISH, CONTEXT_6, STATIC_REF_MULTI_LINE, STATIC_REF_MULTI_LINE_PLURAL, 2, 52));
	}
}