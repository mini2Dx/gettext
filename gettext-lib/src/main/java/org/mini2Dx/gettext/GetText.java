package org.mini2Dx.gettext;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class GetText {
	private static final Map<Locale, TranslationMap> TRANSLATIONS = new HashMap<Locale, TranslationMap>();
	private static Locale LOCALE = Locale.ENGLISH;

	public static String tr(String sourceText) {
		return tr(LOCALE, sourceText);
	}

	public static String tr(String sourceText, Object... values) {
		return tr(LOCALE, sourceText, values);
	}

	public static String trc(String context, String sourceText) {
		return trc(LOCALE, context, sourceText);
	}

	public static String trc(String context, String sourceText, Object... values) {
		return trc(LOCALE, context, sourceText, values);
	}

	public static String trn(String sourceText, String sourcePluralText, int n) {
		return trn(LOCALE, sourceText, sourcePluralText, n);
	}

	public static String trn(String sourceText, String sourcePluralText, int n, Object... values) {
		return trn(LOCALE, sourceText, sourcePluralText, n, values);
	}

	public static String trnc(String context, String sourceText, String sourcePluralText, int n) {
		return trnc(LOCALE, context, sourceText, sourcePluralText, n);
	}

	public static String trnc(String context, String sourceText, String sourcePluralText, int n, Object... values) {
		return trnc(LOCALE, context, sourceText, sourcePluralText, n, values);
	}

	public static String tr(Locale locale, String sourceText) {
		final TranslationMap translationMap = TRANSLATIONS.get(locale);
		if(translationMap == null) {
			return sourceText;
		}
		return translationMap.tr(sourceText);
	}

	public static String tr(Locale locale, String sourceText, Object... values) {
		final TranslationMap translationMap = TRANSLATIONS.get(locale);
		if(translationMap == null) {
			return MessageFormat.format(sourceText, values);
		}
		return translationMap.tr(sourceText, values);
	}

	public static String trc(Locale locale, String context, String sourceText) {
		final TranslationMap translationMap = TRANSLATIONS.get(locale);
		if(translationMap == null) {
			return sourceText;
		}
		return translationMap.trc(context, sourceText);
	}

	public static String trc(Locale locale, String context, String sourceText, Object... values) {
		final TranslationMap translationMap = TRANSLATIONS.get(locale);
		if(translationMap == null) {
			return MessageFormat.format(sourceText, values);
		}
		return translationMap.trc(context, sourceText, values);
	}

	public static String trn(Locale locale, String sourceText, String sourcePluralText, int n) {
		final TranslationMap translationMap = TRANSLATIONS.get(locale);
		if(translationMap == null) {
			return sourcePluralText;
		}
		return translationMap.trn(sourceText, sourcePluralText, n);
	}

	public static String trn(Locale locale, String sourceText, String sourcePluralText, int n, Object... values) {
		final TranslationMap translationMap = TRANSLATIONS.get(locale);
		if(translationMap == null) {
			return MessageFormat.format(sourcePluralText, values);
		}
		return translationMap.trn(sourceText, sourcePluralText, n, values);
	}

	public static String trnc(Locale locale, String context, String sourceText, String sourcePluralText, int n) {
		final TranslationMap translationMap = TRANSLATIONS.get(locale);
		if(translationMap == null) {
			return sourcePluralText;
		}
		return translationMap.trnc(context, sourceText, sourcePluralText, n);
	}

	public static String trnc(Locale locale, String context, String sourceText, String sourcePluralText, int n, Object... values) {
		final TranslationMap translationMap = TRANSLATIONS.get(locale);
		if (translationMap == null) {
			return MessageFormat.format(sourcePluralText, values);
		}
		return translationMap.trnc(context, sourceText, sourcePluralText, n, values);
	}

	/**
	 *
	 * @param locale
	 * @param context Null if default context should be used
	 * @param sourceText
	 * @return Null if no such entry exists
	 */
	public static TranslationEntry getTranslationEntry(Locale locale, String context, String sourceText) {
		final TranslationMap translationMap = TRANSLATIONS.get(locale);
		if(translationMap == null) {
			return null;
		}
		return translationMap.getEntry(context, sourceText);
	}

	public static void add(PoFile poFile) {
		if(!TRANSLATIONS.containsKey(poFile.getLocale())) {
			TRANSLATIONS.put(poFile.getLocale(), new TranslationMap(poFile.getLocale()));
		}
		TRANSLATIONS.get(poFile.getLocale()).add(poFile);
	}

	public static void setLocale(Locale locale) {
		if(locale == null) {
			return;
		}
		LOCALE = locale;
	}
}
