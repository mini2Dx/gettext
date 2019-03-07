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

public class TranslationMap {
	private final Locale locale;
	private final TranslationContext defaultContext = new TranslationContext();
	private final Map<String, TranslationContext> contexts = new HashMap<String, TranslationContext>();

	private final Map<String, MessageFormat> messageFormatsCache = new HashMap<String, MessageFormat>();

	public TranslationMap(Locale locale) {
		super();
		this.locale = locale;
	}

	public String tr(String sourceText) {
		return defaultContext.tr(sourceText);
	}

	public String tr(String sourceText, Object... values) {
		return format(sourceText, tr(sourceText), values);
	}

	public String trc(String context, String sourceText) {
		final TranslationContext translationContext = contexts.get(context);
		if(translationContext == null) {
			return sourceText;
		}
		return translationContext.tr(sourceText);
	}

	public String trc(String context, String sourceText, Object... values) {
		return MessageFormat.format(tr(sourceText), values);
	}

	public String trn(String sourceText, String sourcePluralText, int n) {
		return defaultContext.trn(sourceText, sourcePluralText, n);
	}

	public String trn(String sourceText, String sourcePluralText, int n, Object... values) {
		final String id;
		if(sourcePluralText != null && !sourcePluralText.isEmpty()) {
			id = sourcePluralText + "-" + n;
		} else {
			id = sourceText + "-" + n;
		}
		return format(id, trn(sourceText, sourcePluralText, n), values);
	}

	public String trnc(String context, String sourceText, String sourcePluralText, int n) {
		final TranslationContext translationContext = contexts.get(context);
		if(translationContext == null) {
			return sourceText;
		}
		return translationContext.trn(sourceText, sourcePluralText, n);
	}

	public String trnc(String context, String sourceText, String sourcePluralText, int n, Object... values) {
		final String id;
		if(sourcePluralText != null && !sourcePluralText.isEmpty()) {
			id = sourcePluralText + "-" + n;
		} else {
			id = sourceText + "-" + n;
		}
		return format(id, trnc(context, sourceText, sourcePluralText, n), values);
	}

	public TranslationEntry getEntry(String sourceText) {
		return getEntry(null, sourceText);
	}

	public TranslationEntry getEntry(String context, String sourceText) {
		final TranslationContext translationContext;
		if(context == null || context.isEmpty()) {
			translationContext = defaultContext;
		} else {
			if(!contexts.containsKey(context)) {
				contexts.put(context, new TranslationContext());
			}
			translationContext = contexts.get(context);
		}
		return translationContext.getEntryBySingularForm(sourceText);
	}

	private String format(String id, String str, Object... values) {
		if(!messageFormatsCache.containsKey(id)) {
			messageFormatsCache.put(id, new MessageFormat(str.replace("'", "''"), locale));
		}
		return messageFormatsCache.get(id).format(values,
				new StringBuffer(str.length() + (values == null ? 1 : values.length)), null).toString();
	}

	public void add(PoFile poFile) {
		if(poFile == null) {
			throw new NullPointerException("Null poFile reference");
		}
		for(TranslationEntry entry : poFile.getEntries()) {
			final TranslationContext context;
			if(entry.getContext() != null && !entry.getContext().isEmpty()) {
				if(!contexts.containsKey(entry.getContext())) {
					contexts.put(entry.getContext(), new TranslationContext());
				}
				context = contexts.get(entry.getContext());
			} else {
				context = defaultContext;
			}
			context.add(entry);
		}
	}
}
