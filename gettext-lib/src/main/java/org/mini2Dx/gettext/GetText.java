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
package org.mini2Dx.gettext;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Main class for using gettext. Most developer should just need this class and calling {@link #add(PoFile)} to add translations.<br>
 * <br>
 * By default, the {@link Locale} is set to {@link Locale#ENGLISH}. This can be changed via {@link #setLocale(Locale)}
 */
public class GetText {
	private static final Map<Locale, TranslationMap> TRANSLATIONS = new HashMap<Locale, TranslationMap>();
	private static Locale LOCALE = Locale.ENGLISH;

	/**
	 * Translates source based on currently set {@link Locale}
	 * @param sourceText The source text to translate (msgid)
	 * @return The source text if no translation can be found, else returns msgstr
	 */
	public static String tr(String sourceText) {
		return tr(LOCALE, sourceText);
	}

	/**
	 * Translates source based on currently set {@link Locale} and injects values into the text
	 * @param sourceText The source text to translate (msgid)
	 * @param values The values to be injected using {@link MessageFormat}
	 * @return The source text if no translation can be found, else returns msgstr
	 */
	public static String tr(String sourceText, Object... values) {
		return tr(LOCALE, sourceText, values);
	}

	/**
	 * Translates source based on currently set {@link Locale} based on a specified context
	 * @param context The translation context (msgctxt)
	 * @param sourceText The source text to translate (msgid)
	 * @return The source text if no translation can be found, else returns msgstr
	 */
	public static String trc(String context, String sourceText) {
		return trc(LOCALE, context, sourceText);
	}

	/**
	 * Translates source based on currently set {@link Locale} based on a specified context and injects values into the text
	 * @param context The translation context (msgctxt)
	 * @param sourceText The source text to translate (msgid)
	 * @param values The values to be injected using {@link MessageFormat}
	 * @return The source text if no translation can be found, else returns msgstr
	 */
	public static String trc(String context, String sourceText, Object... values) {
		return trc(LOCALE, context, sourceText, values);
	}

	/**
	 * Translates the plural form of a source based on currently set {@link Locale}
	 * @param sourceText The source text to translate (msgid)
	 * @param sourcePluralText The plural source text to translate (msgid_plural)
	 * @param n The plural index (msgstr[n])
	 * @return The source text if no translation can be found, else returns msgstr[n]
	 */
	public static String trn(String sourceText, String sourcePluralText, int n) {
		return trn(LOCALE, sourceText, sourcePluralText, n);
	}

	/**
	 * Translates the plural form of a source based on currently set {@link Locale} and injects values into the text
	 * @param sourceText The source text to translate (msgid)
	 * @param sourcePluralText The plural source text to translate (msgid_plural)
	 * @param n The plural index (msgstr[n])
	 * @param values The values to be injected using {@link MessageFormat}
	 * @return The source text if no translation can be found, else returns msgstr[n]
	 */
	public static String trn(String sourceText, String sourcePluralText, int n, Object... values) {
		return trn(LOCALE, sourceText, sourcePluralText, n, values);
	}

	/**
	 * Translates the plural form of a source based on currently set {@link Locale} based on a specified context
	 * @param context The translation context (msgctxt)
	 * @param sourceText The source text to translate (msgid)
	 * @param sourcePluralText The plural source text to translate (msgid_plural)
	 * @param n The plural index (msgstr[n])
	 * @return The source text if no translation can be found, else returns msgstr[n]
	 */
	public static String trnc(String context, String sourceText, String sourcePluralText, int n) {
		return trnc(LOCALE, context, sourceText, sourcePluralText, n);
	}

	/**
	 * Translates the plural form of a source based on currently set {@link Locale} based on a specified context and injects values into the text
	 * @param context The translation context (msgctxt)
	 * @param sourceText The source text to translate (msgid)
	 * @param sourcePluralText The plural source text to translate (msgid_plural)
	 * @param n The plural index (msgstr[n])
	 * @param values The values to be injected using {@link MessageFormat}
	 * @return The source text if no translation can be found, else returns msgstr[n]
	 */
	public static String trnc(String context, String sourceText, String sourcePluralText, int n, Object... values) {
		return trnc(LOCALE, context, sourceText, sourcePluralText, n, values);
	}

	/**
	 * Same as {@link #tr(String)} using a specific {@link Locale}
	 */
	public static String tr(Locale locale, String sourceText) {
		final TranslationMap translationMap = TRANSLATIONS.get(locale);
		if(translationMap == null) {
			return sourceText;
		}
		return translationMap.tr(sourceText);
	}

	/**
	 * Same as {@link #tr(String, Object...)} using a specific {@link Locale}
	 */
	public static String tr(Locale locale, String sourceText, Object... values) {
		final TranslationMap translationMap = TRANSLATIONS.get(locale);
		if(translationMap == null) {
			return MessageFormat.format(TranslationMap.messageFormatSanitise(sourceText), values);
		}
		return translationMap.tr(sourceText, values);
	}

	/**
	 * Same as {@link #trc(String, String)} using a specific {@link Locale}
	 */
	public static String trc(Locale locale, String context, String sourceText) {
		final TranslationMap translationMap = TRANSLATIONS.get(locale);
		if(translationMap == null) {
			return sourceText;
		}
		return translationMap.trc(context, sourceText);
	}

	/**
	 * Same as {@link #trc(String, String, Object...)} using a specific {@link Locale}
	 */
	public static String trc(Locale locale, String context, String sourceText, Object... values) {
		final TranslationMap translationMap = TRANSLATIONS.get(locale);
		if(translationMap == null) {
			return MessageFormat.format(TranslationMap.messageFormatSanitise(sourceText), values);
		}
		return translationMap.trc(context, sourceText, values);
	}

	/**
	 * Same as {@link #trn(String, String, int)} using a specific {@link Locale}
	 */
	public static String trn(Locale locale, String sourceText, String sourcePluralText, int n) {
		final TranslationMap translationMap = TRANSLATIONS.get(locale);
		if(translationMap == null) {
			return sourcePluralText;
		}
		return translationMap.trn(sourceText, sourcePluralText, n);
	}

	/**
	 * Same as {@link #trn(String, String, int, Object...)} using a specific {@link Locale}
	 */
	public static String trn(Locale locale, String sourceText, String sourcePluralText, int n, Object... values) {
		final TranslationMap translationMap = TRANSLATIONS.get(locale);
		if(translationMap == null) {
			if(n > 1) {
				return MessageFormat.format(TranslationMap.messageFormatSanitise(sourcePluralText), values);
			}
			return MessageFormat.format(TranslationMap.messageFormatSanitise(sourceText), values);
		}
		return translationMap.trn(sourceText, sourcePluralText, n, values);
	}

	/**
	 * Same as {@link #trnc(String, String, String, int)} using a specific {@link Locale}
	 */
	public static String trnc(Locale locale, String context, String sourceText, String sourcePluralText, int n) {
		final TranslationMap translationMap = TRANSLATIONS.get(locale);
		if(translationMap == null) {
			return sourcePluralText;
		}
		return translationMap.trnc(context, sourceText, sourcePluralText, n);
	}

	/**
	 * Same as {@link #trnc(String, String, String, int, Object...)} using a specific {@link Locale}
	 */
	public static String trnc(Locale locale, String context, String sourceText, String sourcePluralText, int n, Object... values) {
		final TranslationMap translationMap = TRANSLATIONS.get(locale);
		if (translationMap == null) {
			if(n > 1) {
				return MessageFormat.format(TranslationMap.messageFormatSanitise(sourcePluralText), values);
			}
			return MessageFormat.format(TranslationMap.messageFormatSanitise(sourceText), values);
		}
		return translationMap.trnc(context, sourceText, sourcePluralText, n, values);
	}

	/**
	 * Returns the underlying {@link TranslationEntry} for a specific msgid
	 * @param locale The {@link Locale} to look up
	 * @param context Null if default context should be used, or, msgctxt
	 * @param sourceText The source text to translate (msgid)
	 * @return Null if no such entry exists
	 */
	public static TranslationEntry getTranslationEntry(Locale locale, String context, String sourceText) {
		final TranslationMap translationMap = TRANSLATIONS.get(locale);
		if(translationMap == null) {
			return null;
		}
		return translationMap.getEntry(context, sourceText);
	}

	/**
	 * Loads a {@link PoFile} for translation usage.
	 * Note: This does <b>not</b> need to match the current {@link Locale}
	 * @param poFile A {@link PoFile} instance
	 */
	public static void add(PoFile poFile) {
		if(!TRANSLATIONS.containsKey(poFile.getLocale())) {
			TRANSLATIONS.put(poFile.getLocale(), new TranslationMap(poFile.getLocale()));
		}
		TRANSLATIONS.get(poFile.getLocale()).add(poFile);
	}

	/**
	 * Sets the default {@link Locale}
	 * @param locale The {@link Locale} to use
	 */
	public static void setLocale(Locale locale) {
		if(locale == null) {
			return;
		}
		LOCALE = locale;
	}
}
