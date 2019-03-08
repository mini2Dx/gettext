import org.mini2Dx.gettext.GetText;

import java.util.Locale;

/**
 *  HelloWorld.java
 */
public class HelloWorld  {
	private static final String STATIC_REF = "Static ref";
	private static final String STATIC_REF_MULTI_PART = "Static ref " + "multi " + "part";
	private static final String STATIC_REF_MULTI_LINE = "Static ref " +
			"multi " +
			"line";

	private static final String CONTEXT_3 = "ctx3";
	private static final String CONTEXT_6 = "ctx6";

	public static void tr() {
		System.out.println("Non translated string 1");
		System.out.println(GetText.trc("ctx0", "Hello World!"));
		final String result = GetText.trc("ctx1","Multipart " + "same " + "line");
		System.out.println(GetText.trc("ctx2", "Multipart " +
				"multi " +
				"line"));

		//#.Comment 1
		System.out.println(GetText.trc(CONTEXT_3, "With comment", 77));
		System.out.println(
				GetText.trc("ctx4", STATIC_REF));
		System.out.println("Non translated string 2");

		final String result2 = STATIC_REF_MULTI_PART;
		System.out.println(GetText.trc("ctx5",
				result2));

		//#.Comment 2
		System.out.println(GetText.trc(Locale.ENGLISH, CONTEXT_6, STATIC_REF_MULTI_LINE, 52));
	}
}