package org.mini2Dx.gettext;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

public class TranslationMap {
	private final TranslationContext defaultContext = new TranslationContext();
	private final Map<String, TranslationContext> contexts = new HashMap<String, TranslationContext>();

	public String tr(String sourceText) {
		return defaultContext.tr(sourceText);
	}

	public String tr(String sourceText, Object... values) {
		return MessageFormat.format(tr(sourceText), values);
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
		return MessageFormat.format(trn(sourceText, sourcePluralText, n), values);
	}

	public String trnc(String context, String sourceText, String sourcePluralText, int n) {
		final TranslationContext translationContext = contexts.get(context);
		if(translationContext == null) {
			return sourceText;
		}
		return translationContext.trn(sourceText, sourcePluralText, n);
	}

	public String trnc(String context, String sourceText, String sourcePluralText, int n, Object... values) {
		return MessageFormat.format(trnc(context, sourceText, sourcePluralText, n), values);
	}

	public TranslationEntry getEntry(String sourceText) {
		return getEntry(null, sourceText);
	}

	public TranslationEntry getEntry(String context, String sourceText) {
		final TranslationContext translationContext;
		if(context == null) {
			translationContext = defaultContext;
		} else {
			if(!contexts.containsKey(context)) {
				contexts.put(context, new TranslationContext());
			}
			translationContext = contexts.get(context);
		}
		return translationContext.getEntryBySingularForm(sourceText);
	}

	public void add(PoFile poFile) {
		if(poFile == null) {
			throw new NullPointerException("Null poFile reference");
		}
		for(TranslationEntry entry : poFile.getEntries()) {
			final TranslationContext context;
			if(entry.getContext() != null) {
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
