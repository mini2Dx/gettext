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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A single entry from a .po file
 */
public class TranslationEntry {
	private final List<String> translatorComments = new ArrayList<String>(2);
	private final List<String> extractedComments = new ArrayList<String>(2);
	private final List<String> flags = new ArrayList<String>(2);
	private final List<String> mergeComments = new ArrayList<String>(2);
	private String reference;

	private String context;
	private String id, idPlural;
	private final ArrayList<String> strings = new ArrayList<String>(2);

	public void writeTo(PrintWriter printWriter) {
		for(String comment : translatorComments) {
			if(comment.length() == 0) {
				continue;
			}
			printWriter.println("# " + trimComment(comment));
		}
		for(String comment : extractedComments) {
			if(comment.length() == 0) {
				continue;
			}
			printWriter.println("#. " + trimComment(comment));
		}
		for(String comment : flags) {
			if(comment.length() == 0) {
				continue;
			}
			printWriter.println("#, " + trimComment(comment));
		}
		for(String comment : mergeComments) {
			if(comment.length() == 0) {
				continue;
			}
			printWriter.println("#| " + trimComment(comment));
		}
		if(reference != null && !reference.isEmpty()) {
			printWriter.println("#: " + trimComment(reference));
		}
		if(context != null && !context.isEmpty()) {
			printWriter.println("msgctxt \"" + context + "\"");
		}
		if(id != null && !id.isEmpty()) {
			printWriter.println("msgid \"" + Utils.escapeDoubleQuotes(id) + "\"");
		}
		if(idPlural != null && !idPlural.isEmpty()) {
			printWriter.println("msgid_plural \"" + Utils.escapeDoubleQuotes(idPlural) + "\"");
		}
		if(strings.isEmpty()) {
			if(idPlural != null && !idPlural.isEmpty()) {
				printWriter.println("msgstr[0] \"\"");
				printWriter.println("msgstr[1] \"\"");
				printWriter.println("msgstr[2] \"\"");
			} else {
				printWriter.println("msgstr \"\"");
			}
		} else if(strings.size() > 1) {
			for(int i = 0; i < strings.size(); i++) {
				final String str = strings.get(i);
				if(str == null) {
					printWriter.println("msgstr[" + i + "] \"\"");
				} else {
					printWriter.println("msgstr[" + i + "] \"" + Utils.escapeDoubleQuotes(str) + "\"");
				}
			}
			printWriter.println("msgid_plural \"" + Utils.escapeDoubleQuotes(idPlural) + "\"");
		} else {
			printWriter.println("msgstr \"" + Utils.escapeDoubleQuotes(strings.get(0)) + "\"");
		}
	}

	public void writeTo(DataOutputStream outputStream) throws IOException {
		outputStream.writeInt(translatorComments.size());
		for(String comment : translatorComments) {
			outputStream.writeUTF(comment);
		}

		outputStream.writeInt(extractedComments.size());
		for(String comment : extractedComments) {
			outputStream.writeUTF(comment);
		}

		outputStream.writeInt(flags.size());
		for(String comment : flags) {
			outputStream.writeUTF(comment);
		}

		outputStream.writeInt(mergeComments.size());
		for(String comment : mergeComments) {
			outputStream.writeUTF(comment);
		}

		if(reference == null) {
			outputStream.writeUTF("");
		} else {
			outputStream.writeUTF(reference);
		}
		if(context == null) {
			outputStream.writeUTF("");
		} else {
			outputStream.writeUTF(context);
		}
		if(id == null) {
			outputStream.writeUTF("");
		} else {
			outputStream.writeUTF(id);
		}
		if(idPlural == null) {
			outputStream.writeUTF("");
		} else {
			outputStream.writeUTF(idPlural);
		}

		if(strings.isEmpty()) {
			if(idPlural != null && !idPlural.isEmpty()) {
				outputStream.writeInt(3);
				outputStream.writeUTF("");
				outputStream.writeUTF("");
				outputStream.writeUTF("");
			} else {
				outputStream.writeInt(1);
				outputStream.writeUTF("");
			}
		} else if(strings.size() > 1) {
			outputStream.writeInt(strings.size());
			for(int i = 0; i < strings.size(); i++) {
				final String str = strings.get(i);
				if(str == null) {
					outputStream.writeUTF("");
				} else {
					outputStream.writeUTF(str);
				}
			}
		} else {
			outputStream.writeInt(1);
			outputStream.writeUTF(strings.get(0));
		}
	}

	public void readFrom(DataInputStream inputStream) throws IOException {
		final int totalTranslatorComments = inputStream.readInt();
		for(int i = 0; i < totalTranslatorComments; i++) {
			translatorComments.add(inputStream.readUTF());
		}

		final int totalExtractedComments = inputStream.readInt();
		for(int i = 0; i < totalExtractedComments; i++) {
			extractedComments.add(inputStream.readUTF());
		}

		final int totalFlags = inputStream.readInt();
		for(int i = 0; i < totalFlags; i++) {
			flags.add(inputStream.readUTF());
		}

		final int totalMergeComments = inputStream.readInt();
		for(int i = 0; i < totalMergeComments; i++) {
			mergeComments.add(inputStream.readUTF());
		}

		reference = inputStream.readUTF();
		context = inputStream.readUTF();
		id = inputStream.readUTF();
		idPlural = inputStream.readUTF();

		if(reference.isEmpty()) {
			reference = null;
		}
		if(context.isEmpty()) {
			context = null;
		}
		if(idPlural.isEmpty()) {
			idPlural = null;
		}

		final int totalStrings = inputStream.readInt();
		for(int i = 0; i < totalStrings; i++) {
			strings.add(inputStream.readUTF());
		}
	}

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

	private String trimComment(String str) {
		if(str.length() == 0) {
			return str;
		}
		if(str.charAt(0) == ' ') {
			return str.substring(1);
		}
		return str;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		TranslationEntry entry = (TranslationEntry) o;
		return Objects.equals(translatorComments, entry.translatorComments) &&
				Objects.equals(extractedComments, entry.extractedComments) &&
				Objects.equals(flags, entry.flags) &&
				Objects.equals(mergeComments, entry.mergeComments) &&
				Objects.equals(reference, entry.reference) &&
				Objects.equals(context, entry.context) &&
				Objects.equals(id, entry.id) &&
				Objects.equals(idPlural, entry.idPlural) &&
				Objects.equals(strings, entry.strings);
	}

	@Override
	public int hashCode() {
		return Objects.hash(translatorComments, extractedComments, flags, mergeComments, reference, context, id, idPlural, strings);
	}

	@Override
	public String toString() {
		return "TranslationEntry{" +
				"translatorComments=" + translatorComments +
				", extractedComments=" + extractedComments +
				", flags=" + flags +
				", mergeComments=" + mergeComments +
				", reference='" + reference + '\'' +
				", context='" + context + '\'' +
				", id='" + id + '\'' +
				", idPlural='" + idPlural + '\'' +
				", strings=" + strings +
				'}';
	}
}
