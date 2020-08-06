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

import java.util.HashMap;
import java.util.Map;

public class TranslationContext {
	private final Map<String, TranslationEntry> entriesBySingularId = new HashMap<String, TranslationEntry>();
	private final Map<String, TranslationEntry> entriesByPluralId = new HashMap<String, TranslationEntry>();

	public String tr(String sourceText) {
		return getResult(sourceText, entriesBySingularId.get(sourceText));
	}

	public String trn(String sourceText, String sourcePluralText, int n) {
		final TranslationEntry entry;
		if(sourcePluralText == null || !entriesByPluralId.containsKey(sourcePluralText)) {
			entry = entriesBySingularId.get(sourceText);
		} else {
			entry = entriesByPluralId.get(sourcePluralText);
		}
		if(entry == null) {
			return sourceText;
		}
		if(entry.getStrings().isEmpty()) {
			return sourceText;
		}
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
			entriesByPluralId.put(entry.getIdPlural(), entry);
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
