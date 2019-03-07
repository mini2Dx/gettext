import org.mini2Dx.gettext.GetText;

/**
 *  HelloWorld.java
 */
public class HelloWorld  {
	private static final String STATIC_REF = "Static ref";
	private static final String STATIC_REF_MULTI_PART = "Static ref " + "multi " + " part";
	private static final String STATIC_REF_MULTI_LINE = "Static ref " +
			"multi " +
			"line";

	public static void tr() {
		System.out.println("Non translated string 1");
		System.out.println(GetText.tr("Hello World!"));
		final String result = GetText.tr("Multipart " + "same " + "line");
		System.out.println(GetText.tr("Multipart " +
				"multi " +
				"line"));

		//#.Comment 1
		System.out.println(GetText.tr("With comment"));
		System.out.println(
				GetText.tr(STATIC_REF));
		System.out.println("Non translated string 2");

		final String result2 = STATIC_REF_MULTI_PART;
		System.out.println(GetText.tr(
				result2));

		//#.Comment 2
		final String result3 = STATIC_REF_MULTI_LINE;
		System.out.println(GetText.tr(result3));
	}
}