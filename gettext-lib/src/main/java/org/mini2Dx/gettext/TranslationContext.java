package org.mini2Dx.gettext;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

public class TranslationContext {
	private final Map<String, TranslationEntry> entriesBySingularId = new HashMap<String, TranslationEntry>();
	private final Map<String, TranslationEntry> entriesByPluralId = new HashMap<String, TranslationEntry>();

	public String tr(String sourceText) {
		return getResult(sourceText, entriesBySingularId.get(sourceText));
	}

	public String trn(String sourceText, String sourcePluralText, int n) {
		if(!entriesByPluralId.containsKey(sourcePluralText)) {
			return tr(sourceText);
		}

		final TranslationEntry entry = entriesByPluralId.get(sourcePluralText);
		if(n >= entry.getStrings().size()) {
			return entry.getStrings().get(entry.getStrings().size() - 1);
		}
		return entry.getStrings().get(n);
	}

	public TranslationEntry getEntryBySingularForm(String id) {
		return entriesBySingularId.get(id);
	}

	public TranslationEntry getEntryByPluralForm(String idPlural) {
		return entriesByPluralId.get(idPlural);
	}

	public void add(TranslationEntry entry) {
		if(entry.getId() != null) {
			entriesBySingularId.put(entry.getId(), entry);
		}
		if(entry.getIdPlural() != null) {
			entriesByPluralId.put(entry.getId(), entry);
		}
	}

	private String getResult(String sourceText, TranslationEntry entry) {
		if(entry == null) {
			return sourceText;
		}
		if(entry.getStrings().isEmpty()) {
			return sourceText;
		}
		return entry.getStrings().get(0);
	}
}
