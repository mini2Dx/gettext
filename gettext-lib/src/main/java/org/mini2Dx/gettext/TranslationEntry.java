package org.mini2Dx.gettext;

import java.util.ArrayList;
import java.util.List;

public class TranslationEntry {
	private final List<String> translatorComments = new ArrayList<String>(2);
	private final List<String> extractedComments = new ArrayList<String>(2);
	private final List<String> flags = new ArrayList<String>(2);
	private final List<String> mergeComments = new ArrayList<String>(2);
	private String reference;

	private String context;
	private String id, idPlural;
	private final ArrayList<String> strings = new ArrayList<String>(2);

	public List<String> getTranslatorComments() {
		return translatorComments;
	}

	public List<String> getExtractedComments() {
		return extractedComments;
	}

	public List<String> getFlags() {
		return flags;
	}

	public List<String> getMergeComments() {
		return mergeComments;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIdPlural() {
		return idPlural;
	}

	public void setIdPlural(String idPlural) {
		this.idPlural = idPlural;
	}

	public List<String> getStrings() {
		return strings;
	}

	public void setString(int index, String str) {
		strings.ensureCapacity(index);

		while(index >= strings.size()) {
			strings.add(null);
		}
		strings.set(index, str);
	}
}
