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
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.Tuple;
import org.antlr.v4.runtime.misc.Tuple2;
import org.antlr.v4.runtime.tree.ParseTreeListener;
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
import java.util.*;

public class LuaFile extends LuaBaseListener implements SourceFile {
	public static final String DEFAULT_COMMENT_FORMAT = "#.";
	public static final String DEFAULT_FORCE_EXTRACT_FORMAT = "#!extract";
	public static final String DEFAULT_IGNORE_EXTRACT_FORMAT = "#!ignore";

	protected final String relativePath;
	protected final List<TranslationEntry> translationEntries = new ArrayList<TranslationEntry>();

	protected final Map<String, ArrayList<Tuple2<Integer, String>>> tables = new HashMap<>();
	protected final Map<String, String> variables = new HashMap<String, String>();
	protected final Map<String, Integer> variableByLineNumber = new HashMap<String, Integer>();
	protected final Map<Integer, String> comments = new HashMap<Integer, String>();
	protected final Set<Integer> forceExtract = new HashSet<>();
	protected final Set<Integer> ignore = new HashSet<>();

	/**
	 * Parses a lua file from an input stream using {@link #DEFAULT_COMMENT_FORMAT} as the PO comment prefix.
	 * Any comment line starting with the comment prefix (e.g. --#. This is a note) will be treated as a translation note for the generate PO file.
	 * @param file The input {@link File} to read from
	 * @param relativePath The relative asset path for the file to use as the line reference in the PO translation entries
	 * @throws IOException
	 */
	public LuaFile(File file, String relativePath) throws IOException {
		this(new FileInputStream(file), relativePath, DEFAULT_COMMENT_FORMAT, DEFAULT_FORCE_EXTRACT_FORMAT, DEFAULT_IGNORE_EXTRACT_FORMAT);
	}

	/**
	 * Parses a lua file from an input stream using {@link #DEFAULT_COMMENT_FORMAT} as the PO comment prefix.
	 * Any comment line starting with the comment prefix (e.g. --#. This is a note) will be treated as a translation note for the generate PO file.
	 * @param inputStream The input stream to read from
	 * @param relativePath The relative asset path for the file to use as the line reference in the PO translation entries
	 * @throws IOException
	 */
	public LuaFile(InputStream inputStream, String relativePath) throws IOException {
		this(inputStream, relativePath, DEFAULT_COMMENT_FORMAT, DEFAULT_FORCE_EXTRACT_FORMAT, DEFAULT_IGNORE_EXTRACT_FORMAT);
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
		this(inputStream, relativePath, commentFormatPrefix, DEFAULT_FORCE_EXTRACT_FORMAT, DEFAULT_IGNORE_EXTRACT_FORMAT);
	}

	/**
	 * Parses a lua file from an input stream using a custom PO comment prefix.
	 * Any comment line starting with the comment prefix (e.g. --#. This is a note) will be treated as a translation note for the generate PO file.
	 * @param inputStream The input stream to read from
	 * @param relativePath The relative asset path for the file to use as the line reference in the PO translation entries
	 * @param commentFormatPrefix The custom comment prefix to parse
	 * @param forceExtractFormat The custom comment prefix to force text extraction
	 * @param ignoreFormat The custom comment prefix to ignore text extraction
	 * @throws IOException
	 */
	public LuaFile(InputStream inputStream, String relativePath, String commentFormatPrefix,
	               String forceExtractFormat, String ignoreFormat) throws IOException {
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
					comments.put(token.getLine(), comment.trim());
				} else if(comment.startsWith(forceExtractFormat)) {
					forceExtract.add(token.getLine());
				} else if(comment.startsWith(ignoreFormat)) {
					ignore.add(token.getLine());
				}
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
	public void exitTableconstructor(LuaParser.TableconstructorContext ctx) {
		ArrayList<Tuple2<Integer, String>> tableFields = new ArrayList<>();
		final int lineNumber = ctx.start.getLine();
		if (ctx.fieldlist() != null) {
			List<? extends LuaParser.FieldContext> fields = ctx.fieldlist().field();
			for (LuaParser.FieldContext field : fields) {
				tableFields.add(Tuple.create(lineNumber, stripQuotes(field.getText())));
			}
		}
		tables.put(String.valueOf(lineNumber), tableFields);

		if(ignore.contains(lineNumber - 1)) {
			return;
		}

		if(comments.containsKey(lineNumber - 1)) {
			if(forceExtract.contains(lineNumber - 2)) {
				extractFromTable(lineNumber, String.valueOf(lineNumber));
			}
		} else if(forceExtract.contains(lineNumber - 1)) {
			extractFromTable(lineNumber, String.valueOf(lineNumber));
		}
	}

	private void extractFromTable(int lineNumber, String key) {
		ArrayList<Tuple2<Integer, String>> entries = tables.get(key);
		for (int i = 0; i < entries.size(); i++) {
			Tuple2<Integer, String> entry = entries.get(i);
			final TranslationEntry translationEntry = new TranslationEntry();
			translationEntry.setReference(relativePath + ":" + entry.getItem1());
			String comment = getComment(lineNumber);
			if (comment != null) {
				translationEntry.getExtractedComments().add(comment);
			}
			translationEntry.setId(entry.getItem2());
			translationEntries.add(translationEntry);
		}
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
				} else if(value.startsWith("\"") && value.endsWith("\"")) {
					value = value.substring(1, value.length() - 1);
					value = value.replace("\"..\"", "");
				}

				for(String variableName : variableNames) {
					variables.put(variableName, value);
					variableByLineNumber.put(variableName, ctx.start.getLine());
				}
			}
		}
	}

	@Override
	public void exitPrefixexp(LuaParser.PrefixexpContext ctx) {
		try {
			if(ctx.varOrExp() != null) {
				for(int i = 0; i < ctx.nameAndArgs().size(); i++) {
					generateTranslationEntry(ctx.getStart().getLine(), ctx.varOrExp().var().NAME().getText(),
							ctx.nameAndArgs(i).NAME() != null ? ctx.nameAndArgs(i).NAME().getText() : ctx.varOrExp().var().NAME().getText(), ctx.nameAndArgs(i).args());
				}
			} else {
				for(int i = 0; i < ctx.nameAndArgs().size(); i++) {
					generateTranslationEntry(ctx.getStart().getLine(), "", ctx.nameAndArgs(i).NAME().getText(), ctx.nameAndArgs(i).args());
				}
			}
		} catch (Exception e) {
			System.err.println("Error parsing line: " + ctx.getStart().getLine());
			throw e;
		}
	}

	@Override
	public void exitFunctioncall(LuaParser.FunctioncallContext ctx) {
		try {
			if(ctx.varOrExp() != null) {
				for(int i = 0; i < ctx.nameAndArgs().size(); i++) {
					generateTranslationEntry(ctx.getStart().getLine(), ctx.varOrExp().var().NAME().getText(),
							ctx.nameAndArgs(i).NAME() != null ? ctx.nameAndArgs(i).NAME().getText() : ctx.varOrExp().var().NAME().getText(), ctx.nameAndArgs(i).args());
				}
			} else {
				for(int i = 0; i < ctx.nameAndArgs().size(); i++) {
					generateTranslationEntry(ctx.getStart().getLine(), "", ctx.nameAndArgs(i).NAME().getText(), ctx.nameAndArgs(i).args());
				}
			}
		} catch (Exception e) {
			System.err.println("Error parsing line: " + ctx.getStart().getLine());
			throw e;
		}
	}

	/**
	 * Used when extending LuaFile
	 * @param lineNumber
	 * @param functionName
	 * @param args
	 * @return if a {@link TranslationEntry} has been generated
	 */
	protected boolean generateTranslationEntry(int lineNumber, String variableName, String functionName, LuaParser.ArgsContext args) {
		if(ignore.contains(lineNumber - 1)) {
			return false;
		}
		if(!isGetTextFunction(lineNumber, functionName)) {
			return false;
		}

		final TranslationEntry translationEntry = new TranslationEntry();
		translationEntry.setReference(relativePath + ":" + lineNumber);

		if(comments.containsKey(lineNumber - 1)) {
			translationEntry.getExtractedComments().add(comments.get(lineNumber - 1));
		} else if(forceExtract.contains(lineNumber - 1) && comments.containsKey(lineNumber - 2)) {
			translationEntry.getExtractedComments().add(comments.get(lineNumber - 2));
		}

		final GetTextFunctionType functionType = getFunctionType(functionName, args);
		switch(functionType) {
		case TR:
		case TR_WITH_VALUES:
			translationEntry.setId(getArgument(lineNumber, args, 0));
			break;
		case TR_WITH_LOCALE:
		case TR_WITH_LOCALE_AND_VALUES:
			translationEntry.setId(getArgument(lineNumber, args, 1));
			break;
		case TRC:
		case TRC_WITH_VALUES:
			translationEntry.setContext(getArgument(lineNumber, args, 0));
			translationEntry.setId(getArgument(lineNumber, args, 1));
			break;
		case TRC_WITH_LOCALE:
		case TRC_WITH_LOCALE_AND_VALUES:
			translationEntry.setContext(getArgument(lineNumber, args, 1));
			translationEntry.setId(getArgument(lineNumber, args, 2));
			break;
		case TRN:
		case TRN_WITH_VALUES:
			translationEntry.setId(getArgument(lineNumber, args, 0));
			translationEntry.setIdPlural(getArgument(lineNumber, args, 1));
			break;
		case TRN_WITH_LOCALE:
		case TRN_WITH_LOCALE_AND_VALUES:
			translationEntry.setId(getArgument(lineNumber, args, 1));
			translationEntry.setIdPlural(getArgument(lineNumber, args, 2));
			break;
		case TRNC:
		case TRNC_WITH_VALUES:
			translationEntry.setContext(getArgument(lineNumber, args, 0));
			translationEntry.setId(getArgument(lineNumber, args, 1));
			translationEntry.setIdPlural(getArgument(lineNumber, args, 2));
			break;
		case TRNC_WITH_LOCALE:
		case TRNC_WITH_LOCALE_AND_VALUES:
			translationEntry.setContext(getArgument(lineNumber, args, 1));
			translationEntry.setId(getArgument(lineNumber, args, 2));
			translationEntry.setIdPlural(getArgument(lineNumber, args, 3));
			break;
		case FORCE_EXTRACT:
			if(tables.containsKey(String.valueOf(lineNumber))) {
				return false;
			}
			boolean initialEntryDone = false;

			for(int i = 0; i < args.explist().exp().size(); i++) {
				final LuaParser.ExpContext expContext = args.explist().exp(i);
				final String value = expContext.getText().trim();
				final String resolvedValue;
				if(variables.containsKey(value)) {
					final int variableLineNumber = variableByLineNumber.get(value);
					if(forceExtract.contains(variableLineNumber - 1)) {
						//Already extracted
						continue;
					}
					if(comments.containsKey(variableLineNumber - 1) && forceExtract.contains(variableLineNumber - 2)) {
						//Already extracted
						continue;
					}
					resolvedValue = variables.get(value);
				} else if(value.startsWith("\"")) {
					resolvedValue = stripQuotes(value);
				} else {
					continue;
				}

				if(initialEntryDone) {
					final TranslationEntry additionalEntry = new TranslationEntry();
					additionalEntry.setReference(relativePath + ":" + lineNumber);
					additionalEntry.setId(resolvedValue);
					if(comments.containsKey(lineNumber - 1)) {
						additionalEntry.getExtractedComments().add(comments.get(lineNumber - 1));
					} else if(forceExtract.contains(lineNumber - 1) && comments.containsKey(lineNumber - 2)) {
						additionalEntry.getExtractedComments().add(comments.get(lineNumber - 2));
					}
					translationEntries.add(additionalEntry);
				} else {
					translationEntry.setId(resolvedValue);
					initialEntryDone = true;
				}
			}
			break;
		}

		translationEntries.add(translationEntry);
		return true;
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
		case "tr":
			return totalArgs > 1 ? GetTextFunctionType.TR_WITH_VALUES : GetTextFunctionType.TR;
		default:
			return GetTextFunctionType.FORCE_EXTRACT;
		}
	}

	protected String getArgument(int lineNumber, LuaParser.ArgsContext argsContext, int index) {
		return getArgument(lineNumber, argsContext, index, true);
	}

	protected String getArgument(int lineNumber, LuaParser.ArgsContext argsContext, int index, boolean trimQuotes) {
		if (argsContext.explist() == null) {
			return "";
		}
		if (argsContext.explist().exp() == null) {
			return "";
		}
		if (index < 0) {
			return "";
		} else if (index >= argsContext.explist().exp().size()) {
			return "";
		}
		final LuaParser.ExpContext expContext = argsContext.explist().exp(index);
		final String value = expContext.getText().trim();
		if (variables.containsKey(value)) {
			return variables.get(value);
		} else if (value.startsWith("\"") && value.endsWith("\"")) {
			if (trimQuotes) {
				return value.substring(1, value.length() - 1).replace("\"..\"", "");
			} else {
				return value;
			}
		} else if(value.contains("..")) {
			//String concat
			return value;
		} else {
			throw new RuntimeException("Could not determine variable value for " + value + " on line " + lineNumber);
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

	private boolean isGetTextFunction(int lineNumber, String functionName) {
		if(ignore.contains(lineNumber - 1)) {
			return false;
		}
		if(forceExtract.contains(lineNumber - 1)) {
			return true;
		}else if(comments.containsKey(lineNumber - 1)) {
			if(forceExtract.contains(lineNumber - 2)) {
				return true;
			}
		}
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

	protected String getComment(int line) {
		if (comments.containsKey(line)){
			return comments.get(line);
		}
		return null;
	}

	protected String stripQuotes(String str){
		if (str.startsWith("\"") && str.endsWith("\"")){
			str = str.substring(1, str.length() - 1);
		}
		return str;
	}

	public String getRelativePath() {
		return relativePath;
	}
}
