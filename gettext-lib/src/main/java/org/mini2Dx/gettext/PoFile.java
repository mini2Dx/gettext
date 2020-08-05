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

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.mini2Dx.gettext.antlr.GetTextBaseListener;
import org.mini2Dx.gettext.antlr.GetTextLexer;
import org.mini2Dx.gettext.antlr.GetTextParser;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Parses a .po file and stores a {@link TranslationEntry} for each translation
 */
public class PoFile extends GetTextBaseListener {
	private static final LexerErrorListener LEXER_ERROR_LISTENER = new LexerErrorListener();
	private static final ParserErrorListener PARSER_ERROR_LISTENER = new ParserErrorListener();
	private static final String EMPTY_STRING = "";

	private final Locale locale;
	private final PoParseSettings parseSettings;
	private final List<TranslationEntry> entries = new ArrayList<TranslationEntry>();

	private TranslationEntry currentEntry = null;
	
	public PoFile(Locale locale, File file) throws IOException {
		this(locale, new FileReader(file));
	}

	public PoFile(Locale locale, Reader reader) throws IOException {
		this(locale, reader, PoParseSettings.DEFAULT);
	}

	public PoFile(Locale locale, InputStream inputStream) throws IOException {
		this(locale, inputStream, PoParseSettings.DEFAULT);
	}

	public PoFile(Locale locale, File file, PoParseSettings parseSettings) throws IOException {
		this(locale, new FileReader(file), parseSettings);
	}

	public PoFile(Locale locale, Reader reader, PoParseSettings parseSettings) throws IOException {
		super();
		this.locale = locale;
		this.parseSettings = parseSettings;

		read(CharStreams.fromReader(reader));
		reader.close();
	}

	public PoFile(Locale locale, InputStream inputStream, PoParseSettings parseSettings) throws IOException {
		super();
		this.locale = locale;
		this.parseSettings = parseSettings;

		read(CharStreams.fromStream(inputStream));
		inputStream.close();
	}

	public PoFile(Locale locale) {
		super();
		this.locale = locale;
		this.parseSettings = PoParseSettings.DEFAULT;
	}

	private void read(CharStream charStream) {
		final GetTextLexer lexer = new GetTextLexer(charStream);
		lexer.removeErrorListeners();
		lexer.addErrorListener(LEXER_ERROR_LISTENER);
		final GetTextParser parser = new GetTextParser(new BufferedTokenStream(lexer));
		parser.removeErrorListeners();
		parser.addErrorListener(PARSER_ERROR_LISTENER);

		final GetTextParser.PoContext context = parser.po();
		final ParseTreeWalker parseTreeWalker = new ParseTreeWalker();

		parseTreeWalker.walk(this, context);
	}

	public void saveTo(File file) throws IOException {
		final PrintWriter printWriter = new PrintWriter(file, StandardCharsets.UTF_8.name());
		for(TranslationEntry translationEntry : entries) {
			translationEntry.writeTo(printWriter);
			printWriter.println();
		}
		printWriter.flush();
		printWriter.close();
	}

	@Override
	public void enterEntry(GetTextParser.EntryContext ctx) {
		currentEntry = new TranslationEntry();
	}

	@Override
	public void exitEntry(GetTextParser.EntryContext ctx) {
		entries.add(currentEntry);
		currentEntry = null;
	}

	@Override
	public void exitMessageContext(GetTextParser.MessageContextContext ctx) {
		if(ctx.quotedTextLiteral() != null && ctx.quotedTextLiteral().size() > 0) {
			final StringBuilder result = new StringBuilder();
			for(int i = 0; i < ctx.quotedTextLiteral().size(); i++) {
				if(ctx.quotedTextLiteral(i) == null) {
					continue;
				}
				result.append(unquoteText(ctx.quotedTextLiteral().get(i).getText()));
			}
			currentEntry.setContext(result.toString());
		} else if(ctx.unquotedTextLiteral() != null) {
			currentEntry.setContext(repairEscapedQuotes(ctx.unquotedTextLiteral().getText()));
		}
	}

	@Override
	public void exitMessageId(GetTextParser.MessageIdContext ctx) {
		if(ctx.quotedTextLiteral() != null && ctx.quotedTextLiteral().size() > 0) {
			final StringBuilder result = new StringBuilder();
			for(int i = 0; i < ctx.quotedTextLiteral().size(); i++) {
				if(ctx.quotedTextLiteral(i) == null) {
					continue;
				}
				result.append(unquoteText(ctx.quotedTextLiteral().get(i).getText()));
			}
			currentEntry.setId(result.toString());
		} else if(ctx.unquotedTextLiteral() != null) {
			currentEntry.setId(repairEscapedQuotes(ctx.unquotedTextLiteral().getText()));
		}
	}

	@Override
	public void exitMessageIdPlural(GetTextParser.MessageIdPluralContext ctx) {
		if(ctx.quotedTextLiteral() != null && ctx.quotedTextLiteral().size() > 0) {
			final StringBuilder result = new StringBuilder();
			for(int i = 0; i < ctx.quotedTextLiteral().size(); i++) {
				if(ctx.quotedTextLiteral(i) == null) {
					continue;
				}
				result.append(unquoteText(ctx.quotedTextLiteral().get(i).getText()));
			}
			currentEntry.setIdPlural(result.toString());
		} else if(ctx.unquotedTextLiteral() != null) {
			currentEntry.setIdPlural(repairEscapedQuotes(ctx.unquotedTextLiteral().getText()));
		}
	}

	@Override
	public void exitMessageNumStr(GetTextParser.MessageNumStrContext ctx) {
		final int index;

		if(ctx.numericIndexLiteral() != null) {
			//Numeric index
			index = Integer.parseInt(ctx.numericIndexLiteral().digits().getText());
		} else {
			index = 0;
		}

		if(ctx.unquotedTextLiteral() != null) {
			currentEntry.setString(index, repairEscapedQuotes(ctx.unquotedTextLiteral().getText()));
		} else {
			final StringBuilder result = new StringBuilder();
			for(int i = 0; i < ctx.quotedTextLiteral().size(); i++) {
				if(ctx.quotedTextLiteral(i) == null) {
					continue;
				}
				result.append(unquoteText(ctx.quotedTextLiteral().get(i).getText()));
			}
			currentEntry.setString(index, result.toString());
		}
	}

	@Override
	public void exitMessageStr(GetTextParser.MessageStrContext ctx) {
		if(ctx.unquotedTextLiteral() != null) {
			currentEntry.setString(0, repairEscapedQuotes(ctx.unquotedTextLiteral().getText()));
		} else {
			final StringBuilder result = new StringBuilder();
			for(int i = 0; i < ctx.quotedTextLiteral().size(); i++) {
				if(ctx.quotedTextLiteral(i) == null) {
					continue;
				}
				result.append(unquoteText(ctx.quotedTextLiteral().get(i).getText()));
			}
			currentEntry.setString(0, result.toString());
		}
	}

	@Override
	public void exitExtractedComment(GetTextParser.ExtractedCommentContext ctx) {
		if(!parseSettings.extractedComments) {
			return;
		}
		if(ctx.unquotedTextLiteral() != null) {
			currentEntry.getExtractedComments().add(ctx.unquotedTextLiteral().getText());
		}
	}

	@Override
	public void exitFlag(GetTextParser.FlagContext ctx) {
		if(!parseSettings.flags) {
			return;
		}
		if(ctx.unquotedTextLiteral() != null) {
			currentEntry.getFlags().add(ctx.unquotedTextLiteral().getText());
		}
	}

	@Override
	public void exitMergeComment(GetTextParser.MergeCommentContext ctx) {
		if(!parseSettings.mergeComments) {
			return;
		}
		if(ctx.unquotedTextLiteral() != null) {
			currentEntry.getMergeComments().add(ctx.unquotedTextLiteral().getText());
		}
	}

	@Override
	public void exitReference(GetTextParser.ReferenceContext ctx) {
		if(!parseSettings.reference) {
			return;
		}
		if(ctx.unquotedTextLiteral() != null) {
			currentEntry.setReference(ctx.unquotedTextLiteral().getText());
		}
	}

	@Override
	public void exitTranslatorComment(GetTextParser.TranslatorCommentContext ctx) {
		if(!parseSettings.translatorComments) {
			return;
		}
		if(ctx.unquotedTextLiteral() != null) {
			currentEntry.getTranslatorComments().add(ctx.unquotedTextLiteral().getText());
		}
	}

	private String unquoteText(String str) {
		if(str.length() == 2 && str.equalsIgnoreCase("\"\"")) {
			return EMPTY_STRING;
		}
		return repairEscapedQuotes(str.substring(1, str.length() - 1));
	}

	private String repairEscapedQuotes(String str) {
		return str.replace("\\\"", "\"");
	}

	public Locale getLocale() {
		return locale;
	}

	public List<TranslationEntry> getEntries() {
		return entries;
	}
}
