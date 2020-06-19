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
package org.mini2Dx.gettext.plugin.file;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.mini2Dx.gettext.GetText;
import org.mini2Dx.gettext.TranslationEntry;
import org.mini2Dx.gettext.plugin.GetTextFunctionType;
import org.mini2Dx.gettext.plugin.antlr.LuaBaseListener;
import org.mini2Dx.gettext.plugin.antlr.LuaLexer;
import org.mini2Dx.gettext.plugin.antlr.LuaParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LuaFile extends LuaBaseListener implements SourceFile {
	public static final String DEFAULT_COMMENT_FORMAT = "#.";

	private final List<TranslationEntry> translationEntries = new ArrayList<TranslationEntry>();
	private final String relativePath;

	private final Map<String, String> variables = new HashMap<String, String>();
	private final Map<Integer, String> comments = new HashMap<Integer, String>();

	/**
	 * Parses a lua file from an input stream using {@link #DEFAULT_COMMENT_FORMAT} as the PO comment prefix.
	 * Any comment line starting with the comment prefix (e.g. --#. This is a note) will be treated as a translation note for the generate PO file.
	 * @param file The input {@link File} to read from
	 * @param relativePath The relative asset path for the file to use as the line reference in the PO translation entries
	 * @throws IOException
	 */
	public LuaFile(File file, String relativePath) throws IOException {
		this(new FileInputStream(file), relativePath, DEFAULT_COMMENT_FORMAT);
	}

	/**
	 * Parses a lua file from an input stream using {@link #DEFAULT_COMMENT_FORMAT} as the PO comment prefix.
	 * Any comment line starting with the comment prefix (e.g. --#. This is a note) will be treated as a translation note for the generate PO file.
	 * @param inputStream The input stream to read from
	 * @param relativePath The relative asset path for the file to use as the line reference in the PO translation entries
	 * @throws IOException
	 */
	public LuaFile(InputStream inputStream, String relativePath) throws IOException {
		this(inputStream, relativePath, DEFAULT_COMMENT_FORMAT);
	}

	/**
	 * Parses a lua file from an input stream using a custom PO comment prefix.
	 * Any comment line starting with the comment prefix (e.g. --#. This is a note) will be treated as a translation note for the generate PO file.
	 * @param inputStream The input stream to read from
	 * @param relativePath The relative asset path for the file to use as the line reference in the PO translation entries
	 * @param commentFormatPrefix The custom comment prefix to parse
	 * @throws IOException
	 */
	public LuaFile(InputStream inputStream, String relativePath, String commentFormatPrefix) throws IOException {
		super();
		this.relativePath = relativePath;

		final LuaLexer lexer = new LuaLexer(CharStreams.fromStream(inputStream));
		final CommonTokenStream commentStream = new CommonTokenStream(lexer, Token.HIDDEN_CHANNEL);
		commentStream.getNumberOfOnChannelTokens();
		for(Token token : commentStream.get(0, commentStream.size())) {
			if(token.getChannel() ==  Token.HIDDEN_CHANNEL) {
				String comment = token.getText();
				if(comment.startsWith("--")) {
					comment = comment.substring(2);
				}
				if(comment.startsWith(commentFormatPrefix)) {
					comment = comment.substring(commentFormatPrefix.length());
				} else {
					continue;
				}
				comments.put(token.getLine(), comment.trim());
			}
		}

		lexer.reset();
		final CommonTokenStream codeStream = new CommonTokenStream(lexer, Token.DEFAULT_CHANNEL);

		final LuaParser parser = new LuaParser(codeStream);

		final LuaParser.ChunkContext context = parser.chunk();
		final ParseTreeWalker parseTreeWalker = new ParseTreeWalker();
		parseTreeWalker.walk(this, context);
		inputStream.close();
	}

	@Override
	public void exitStat(LuaParser.StatContext ctx) {
		if(ctx.namelist() != null && ctx.explist() != null) {
			final List<String> variableNames = new ArrayList<String>();
			for(int i = 0; i < ctx.namelist().NAME().size(); i++) {
				variableNames.add(ctx.namelist().NAME().get(i).getText());
			}
			for(int i = 0; i < ctx.explist().exp().size(); i++) {
				String value = ctx.explist().exp(i).getText();
				if(variables.containsKey(value)) {
					value = variables.get(value);
				} else if(value.startsWith("\"") && value.startsWith("\"")) {
					value = value.substring(1, value.length() - 1);
					value = value.replace("\"..\"", "");
				}

				for(String variableName : variableNames) {
					variables.put(variableName, value);
				}
			}
		}
	}

	@Override
	public void exitPrefixexp(LuaParser.PrefixexpContext ctx) {
		for(int i = 0; i < ctx.nameAndArgs().size(); i++) {
			generateTranslationEntry(ctx.getStart().getLine(), ctx.nameAndArgs(i));
		}
	}

	@Override
	public void exitFunctioncall(LuaParser.FunctioncallContext ctx) {
		for(int i = 0; i < ctx.nameAndArgs().size(); i++) {
			generateTranslationEntry(ctx.getStart().getLine(), ctx.nameAndArgs(i));
		}
	}

	private void generateTranslationEntry(int lineNumber, LuaParser.NameAndArgsContext ctx) {
		if(ctx == null || ctx.NAME() == null) {
			throw new RuntimeException("Error parsing lua file at line: "+ lineNumber);
		}
		final String functionName = ctx.NAME().getText();
		if(!isGetTextFunction(functionName)) {
			return;
		}

		final TranslationEntry translationEntry = new TranslationEntry();
		translationEntry.setReference(relativePath + ":" + lineNumber);

		if(comments.containsKey(lineNumber - 1)) {
			translationEntry.getExtractedComments().add(comments.get(lineNumber - 1));
		}

		final GetTextFunctionType functionType = getFunctionType(functionName, ctx.args());
		switch(functionType) {
		case TR:
		case TR_WITH_VALUES:
			translationEntry.setId(getArgument(ctx.args(), 0));
			break;
		case TR_WITH_LOCALE:
		case TR_WITH_LOCALE_AND_VALUES:
			translationEntry.setId(getArgument(ctx.args(), 1));
			break;
		case TRC:
		case TRC_WITH_VALUES:
			translationEntry.setContext(getArgument(ctx.args(), 0));
			translationEntry.setId(getArgument(ctx.args(), 1));
			break;
		case TRC_WITH_LOCALE:
		case TRC_WITH_LOCALE_AND_VALUES:
			translationEntry.setContext(getArgument(ctx.args(), 1));
			translationEntry.setId(getArgument(ctx.args(), 2));
			break;
		case TRN:
		case TRN_WITH_VALUES:
			translationEntry.setId(getArgument(ctx.args(), 0));
			translationEntry.setIdPlural(getArgument(ctx.args(), 1));
			break;
		case TRN_WITH_LOCALE:
		case TRN_WITH_LOCALE_AND_VALUES:
			translationEntry.setId(getArgument(ctx.args(), 1));
			translationEntry.setIdPlural(getArgument(ctx.args(), 2));
			break;
		case TRNC:
		case TRNC_WITH_VALUES:
			translationEntry.setContext(getArgument(ctx.args(), 0));
			translationEntry.setId(getArgument(ctx.args(), 1));
			translationEntry.setIdPlural(getArgument(ctx.args(), 2));
			break;
		case TRNC_WITH_LOCALE:
		case TRNC_WITH_LOCALE_AND_VALUES:
			translationEntry.setContext(getArgument(ctx.args(), 1));
			translationEntry.setId(getArgument(ctx.args(), 2));
			translationEntry.setIdPlural(getArgument(ctx.args(), 3));
			break;
		}

		translationEntries.add(translationEntry);
	}

	private GetTextFunctionType getFunctionType(String functionName, LuaParser.ArgsContext argsContext) {
		final int totalArgs = argsContext.explist().exp().size();
		switch(functionName.toLowerCase()) {
		case "trnc":
			return totalArgs > 1 ? GetTextFunctionType.TRNC_WITH_VALUES : GetTextFunctionType.TRNC;
		case "trn":
			return totalArgs > 1 ? GetTextFunctionType.TRN_WITH_VALUES : GetTextFunctionType.TRN;
		case "trc":
			return totalArgs > 2 ? GetTextFunctionType.TRC_WITH_VALUES : GetTextFunctionType.TRC;
		default:
		case "tr":
			return totalArgs > 1 ? GetTextFunctionType.TR_WITH_VALUES : GetTextFunctionType.TR;
		}
	}

	private String getArgument(LuaParser.ArgsContext argsContext, int index) {
		if(index < 0) {
			return "";
		} else if(index >= argsContext.explist().exp().size()) {
			return "";
		}
		final LuaParser.ExpContext expContext = argsContext.explist().exp(index);
		final String value = expContext.getText().trim();
		if(variables.containsKey(value)) {
			return variables.get(value);
		} else if(value.startsWith("\"") && value.startsWith("\"")) {
			return value.substring(1, value.length() - 1).replace("\"..\"", "");
		} else {
			throw new RuntimeException("Could not determine variable value for " + value);
		}
	}

	@Override
	public void getTranslationEntries(List<TranslationEntry> result) {
		result.addAll(translationEntries);
	}

	@Override
	public void dispose() {
		comments.clear();
		variables.clear();
		translationEntries.clear();
	}

	private boolean isGetTextFunction(String functionName) {
		if(functionName.startsWith(":")) {
			functionName = functionName.substring(1);
		}
		if(functionName.equals("tr")) {
			return true;
		}
		if(functionName.equals("trn")) {
			return true;
		}
		if(functionName.equals("trc")) {
			return true;
		}
		if(functionName.equals("trnc")) {
			return true;
		}
		return false;
	}

	public String getRelativePath() {
		return relativePath;
	}
}
