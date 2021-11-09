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

import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.gradle.internal.impldep.org.mozilla.javascript.ast.ConditionalExpression;
import org.mini2Dx.gettext.TranslationEntry;
import org.mini2Dx.gettext.antlr.GetTextLexer;
import org.mini2Dx.gettext.antlr.GetTextParser;
import org.mini2Dx.gettext.plugin.GetTextFunctionType;
import org.mini2Dx.gettext.plugin.antlr.JavaBaseListener;
import org.mini2Dx.gettext.plugin.antlr.JavaLexer;
import org.mini2Dx.gettext.plugin.antlr.JavaParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class JavaFile extends JavaBaseListener implements SourceFile {
	public static final String DEFAULT_COMMENT_FORMAT = "#.";
	public static final String DEFAULT_FORCE_EXTRACT_FORMAT = "#!extract";
	public static final String DEFAULT_IGNORE_EXTRACT_FORMAT = "#!ignore";

	protected final List<TranslationEntry> translationEntries = new ArrayList<TranslationEntry>();
	protected final String relativePath;

	protected final Map<String, String> staticVariables = new HashMap<String, String>();
	protected final Map<String, String> instanceVariables = new HashMap<String, String>();
	protected final Map<String, String> localVariables = new HashMap<String, String>();
	protected final Map<String, Integer> staticVariablesByLineNumber = new HashMap<>();
	protected final Map<String, Integer> instanceVariablesByLineNumber = new HashMap<>();
	protected final Map<String, Integer> localVariablesByLineNumber = new HashMap<>();
	protected final Map<Integer, String> comments = new HashMap<Integer, String>();
	protected final Set<Integer> forceExtract = new HashSet<>();
	protected final Set<Integer> ignore = new HashSet<>();

	private ParseState parseState = ParseState.CLASS;
	private boolean nextFieldIsStatic = true;

	/**
	 * Parses a java file from an input stream using {@link #DEFAULT_COMMENT_FORMAT} as the PO comment prefix.
	 * Any comment line starting with the comment prefix (e.g. //#. This is a note) will be treated as a translation note for the generate PO file.
	 * @param file The input {@link File} to read from
	 * @param relativePath The relative asset path for the file to use as the line reference in the PO translation entries
	 * @throws IOException
	 */
	public JavaFile(File file, String relativePath) throws IOException {
		this(new FileInputStream(file), relativePath, DEFAULT_COMMENT_FORMAT);
	}

	/**
	 * Parses a java file from an input stream using {@link #DEFAULT_COMMENT_FORMAT} as the PO comment prefix.
	 * Any comment line starting with the comment prefix (e.g. //#. This is a note) will be treated as a translation note for the generate PO file.
	 * @param inputStream The input stream to read from
	 * @param relativePath The relative asset path for the file to use as the line reference in the PO translation entries
	 * @throws IOException
	 */
	public JavaFile(InputStream inputStream, String relativePath) throws IOException {
		this(inputStream, relativePath, DEFAULT_COMMENT_FORMAT);
	}

	/**
	 * Parses a java file from an input stream using a custom PO comment prefix.
	 * Any comment line starting with the comment prefix (e.g. //#. This is a note) will be treated as a translation note for the generate PO file.
	 * @param inputStream The input stream to read from
	 * @param relativePath The relative asset path for the file to use as the line reference in the PO translation entries
	 * @param commentFormatPrefix The custom comment prefix to parse
	 * @throws IOException
	 */
	public JavaFile(InputStream inputStream, String relativePath, String commentFormatPrefix) throws IOException {
		this(inputStream, relativePath, commentFormatPrefix, DEFAULT_FORCE_EXTRACT_FORMAT, DEFAULT_IGNORE_EXTRACT_FORMAT);
	}

	/**
	 * Parses a java file from an input stream using a custom PO comment prefix.
	 * Any comment line starting with the comment prefix (e.g. //#. This is a note) will be treated as a translation note for the generate PO file.
	 * @param inputStream The input stream to read from
	 * @param relativePath The relative asset path for the file to use as the line reference in the PO translation entries
	 * @param commentFormatPrefix The custom comment prefix to parse
	 * @param forceExtractFormat The custom comment prefix to force text extraction
	 * @param ignoreFormat The custom comment prefix to ignore text extraction
	 * @throws IOException
	 */
	public JavaFile(InputStream inputStream, String relativePath, String commentFormatPrefix,
	                String forceExtractFormat, String ignoreFormat) throws IOException {
		super();
		this.relativePath = relativePath;

		final JavaLexer lexer = new JavaLexer(CharStreams.fromStream(inputStream));

		final CommonTokenStream commentStream = new CommonTokenStream(lexer, Token.HIDDEN_CHANNEL);
		commentStream.getNumberOfOnChannelTokens();
		for(Token token : commentStream.get(0, commentStream.size())) {
			if(token.getChannel() ==  Token.HIDDEN_CHANNEL) {
				String comment = token.getText();
				if(comment.startsWith("//")) {
					comment = comment.substring(2);
				}
				if(comment.startsWith(commentFormatPrefix)) {
					comment = comment.substring(commentFormatPrefix.length());
					comments.put(token.getLine(), comment);
				} else if(comment.startsWith(forceExtractFormat)) {
					forceExtract.add(token.getLine());
				} else if(comment.startsWith(ignoreFormat)) {
					ignore.add(token.getLine());
				}
			}
		}

		lexer.reset();
		final CommonTokenStream codeStream = new CommonTokenStream(lexer, Token.DEFAULT_CHANNEL);

		final JavaParser parser = new JavaParser(codeStream);

		final JavaParser.CompilationUnitContext context = parser.compilationUnit();
		final ParseTreeWalker parseTreeWalker = new ParseTreeWalker();

		parseTreeWalker.walk(this, context);
		inputStream.close();
	}

	@Override
	public void enterMethodDeclaration(JavaParser.MethodDeclarationContext ctx) {
		parseState = ParseState.METHOD;
	}

	@Override
	public void exitMethodDeclaration(JavaParser.MethodDeclarationContext ctx) {
		localVariables.clear();
		parseState = ParseState.CLASS;
	}

	@Override
	public void enterFieldDeclaration(JavaParser.FieldDeclarationContext ctx) {
		for(int i = 0; i < ctx.fieldModifier().size(); i++) {
			if(ctx.fieldModifier(i).STATIC() != null) {
				nextFieldIsStatic = true;
				return;
			}
		}
		nextFieldIsStatic = false;
	}

	@Override
	public void exitVariableDeclarator(JavaParser.VariableDeclaratorContext ctx) {
		final int lineNumber = ctx.start.getLine();
		if (ignore.contains(lineNumber - 1)) {
			return;
		}

		String variableName;
		String value;

		if(ctx.variableInitializer() != null) {
			value = ctx.variableInitializer().getText();
			if(value.startsWith("\"")) {
				value = value.substring(1, value.length() - 1);
			} else if(staticVariables.containsKey(value)) {
				//Reference
				value = staticVariables.get(value);
			} else if(instanceVariables.containsKey(value)) {
				value = instanceVariables.get(value);
			} else if(localVariables.containsKey(value)) {
				value = localVariables.get(value);
			}
			value = value.replace("\"+\"", "");
		} else {
			value = "";
		}
		if(nextFieldIsStatic) {
			variableName = ctx.variableDeclaratorId().getText();
			staticVariables.put(variableName, value);
			staticVariablesByLineNumber.put(variableName, lineNumber);
		} else {
			variableName = ctx.variableDeclaratorId().getText();

			switch(parseState) {
			case CLASS:
				instanceVariables.put(variableName, value);
				instanceVariablesByLineNumber.put(variableName, lineNumber);
				break;
			default:
			case METHOD:
				localVariables.put(variableName, value);
				localVariablesByLineNumber.put(variableName, lineNumber);
				break;
			}
		}

		if(value == null || value.isEmpty()) {
			return;
		}

		if(forceExtract.contains(lineNumber - 1)) {
			createForcedEntryForValue(lineNumber, value);
		} else if(comments.containsKey(lineNumber - 1) && forceExtract.contains(lineNumber - 2)) {
			createForcedEntryForValue(lineNumber, value);
		}
	}

	private void createForcedEntryForValue(int lineNumber, String value) {
		if (ignore.contains(lineNumber - 1)) {
			return;
		}

		final TranslationEntry additionalEntry = new TranslationEntry();
		additionalEntry.setReference(relativePath + ":" + lineNumber);
		additionalEntry.setId(value);
		if(comments.containsKey(lineNumber - 1)) {
			additionalEntry.getExtractedComments().add(comments.get(lineNumber - 1));
		} else if(forceExtract.contains(lineNumber - 1) && comments.containsKey(lineNumber - 2)) {
			additionalEntry.getExtractedComments().add(comments.get(lineNumber - 2));
		}
		translationEntries.add(additionalEntry);
	}

	@Override
	public void exitMethodInvocation_lfno_primary(JavaParser.MethodInvocation_lfno_primaryContext ctx) {
		if(ctx.methodName() != null) {
			generateTranslationEntry(ctx.getStart().getLine(), ctx.methodName().getText(), ctx.argumentList());
		} else {
			generateTranslationEntry(ctx.getStart().getLine(), ctx.Identifier().getText(), ctx.argumentList());
		}
	}

	@Override
	public void exitMethodInvocation(JavaParser.MethodInvocationContext ctx) {
		if(ctx.methodName() != null) {
			generateTranslationEntry(ctx.getStart().getLine(), ctx.methodName().getText(), ctx.argumentList());
		} else {
			generateTranslationEntry(ctx.getStart().getLine(), ctx.Identifier().getText(), ctx.argumentList());
		}
	}

	/**
	 * Used when extending JavaFile
	 * @param lineNumber
	 * @param methodName
	 * @param argListCtxt
	 * @return if a {@link TranslationEntry} has been generated
	 */
	protected boolean generateTranslationEntry(int lineNumber, String methodName, JavaParser.ArgumentListContext argListCtxt) {
		if (ignore.contains(lineNumber - 1)) {
			return false;
		}
		if(methodName == null) {
			return false;
		}
		if(!isGetTextMethod(lineNumber, methodName)) {
			return false;
		}
		final TranslationEntry translationEntry = new TranslationEntry();
		translationEntry.setReference(relativePath + ":" + lineNumber);

		if(comments.containsKey(lineNumber - 1)) {
			translationEntry.getExtractedComments().add(comments.get(lineNumber - 1));
		}

		final GetTextFunctionType functionType = getFunctionType(methodName, argListCtxt);
		switch(functionType) {
		case TR:
		case TR_WITH_VALUES:
			translationEntry.setId(getArgument(argListCtxt, 0, lineNumber));
			break;
		case TR_WITH_LOCALE:
		case TR_WITH_LOCALE_AND_VALUES:
			translationEntry.setId(getArgument(argListCtxt, 1, lineNumber));
			break;
		case TRC:
		case TRC_WITH_VALUES:
			translationEntry.setContext(getArgument(argListCtxt, 0, lineNumber));
			translationEntry.setId(getArgument(argListCtxt, 1, lineNumber));
			break;
		case TRC_WITH_LOCALE:
		case TRC_WITH_LOCALE_AND_VALUES:
			translationEntry.setContext(getArgument(argListCtxt, 1, lineNumber));
			translationEntry.setId(getArgument(argListCtxt, 2, lineNumber));
			break;
		case TRN:
		case TRN_WITH_VALUES:
			translationEntry.setId(getArgument(argListCtxt, 0, lineNumber));
			translationEntry.setIdPlural(getArgument(argListCtxt, 1, lineNumber));
			break;
		case TRN_WITH_LOCALE:
		case TRN_WITH_LOCALE_AND_VALUES:
			translationEntry.setId(getArgument(argListCtxt, 1, lineNumber));
			translationEntry.setIdPlural(getArgument(argListCtxt, 2, lineNumber));
			break;
		case TRNC:
		case TRNC_WITH_VALUES:
			translationEntry.setContext(getArgument(argListCtxt, 0, lineNumber));
			translationEntry.setId(getArgument(argListCtxt, 1, lineNumber));
			translationEntry.setIdPlural(getArgument(argListCtxt, 2, lineNumber));
			break;
		case TRNC_WITH_LOCALE:
		case TRNC_WITH_LOCALE_AND_VALUES:
			translationEntry.setContext(getArgument(argListCtxt, 1, lineNumber));
			translationEntry.setId(getArgument(argListCtxt, 2, lineNumber));
			translationEntry.setIdPlural(getArgument(argListCtxt, 3, lineNumber));
			break;
		case FORCE_EXTRACT:
			boolean initialEntryDone = false;

			for(int i = 0; i < argListCtxt.expression().size(); i++) {
				final String value = argListCtxt.expression(i).assignmentExpression().getText().trim();
				if (staticVariables.containsKey(value)) {
					final int variableLineNumber = staticVariablesByLineNumber.get(value);
					if(forceExtract.contains(variableLineNumber - 1)) {
						//Already extracted
						continue;
					}
					if(comments.containsKey(variableLineNumber - 1) && forceExtract.contains(variableLineNumber - 2)) {
						//Already extracted
						continue;
					}
				}
				if(localVariables.containsKey(value)) {
					final int variableLineNumber = localVariablesByLineNumber.get(value);
					if(forceExtract.contains(variableLineNumber - 1)) {
						//Already extracted
						continue;
					}
					if(comments.containsKey(variableLineNumber - 1) && forceExtract.contains(variableLineNumber - 2)) {
						//Already extracted
						continue;
					}
				}
				if(instanceVariables.containsKey(value)) {
					final int variableLineNumber = instanceVariablesByLineNumber.get(value);
					if(forceExtract.contains(variableLineNumber - 1)) {
						//Already extracted
						continue;
					}
					if(comments.containsKey(variableLineNumber - 1) && forceExtract.contains(variableLineNumber - 2)) {
						//Already extracted
						continue;
					}
				}

				final String argument = getArgument(argListCtxt, i, lineNumber);
				if(argument == null || argument.isEmpty()) {
					continue;
				}

				if(initialEntryDone) {
					final TranslationEntry additionalEntry = new TranslationEntry();
					additionalEntry.setReference(relativePath + ":" + lineNumber);
					additionalEntry.setId(argument);
					if(comments.containsKey(lineNumber - 1)) {
						additionalEntry.getExtractedComments().add(comments.get(lineNumber - 1));
					} else if(forceExtract.contains(lineNumber - 1) && comments.containsKey(lineNumber - 2)) {
						additionalEntry.getExtractedComments().add(comments.get(lineNumber - 2));
					}
					translationEntries.add(additionalEntry);
				} else {
					translationEntry.setId(argument);
					initialEntryDone = true;
				}
			}
			break;
		}

		translationEntries.add(translationEntry);
		return true;
	}

	protected String getArgument(JavaParser.ArgumentListContext argumentListContext, int index, int lineNumber) {
		if(index < 0) {
			return "";
		} else if(index >= argumentListContext.expression().size()) {
			return "";
		}

		final JavaParser.AssignmentExpressionContext expressionContext =
				argumentListContext.expression(index).assignmentExpression();
		if(expressionContext == null) {
			return "";
		}
		final String value = expressionContext.getText().trim();
		if (staticVariables.containsKey(value)) {
			return staticVariables.get(value);
		} else if(localVariables.containsKey(value)) {
			return localVariables.get(value);
		} else if(instanceVariables.containsKey(value)) {
			return instanceVariables.get(value);
		} else if(value.startsWith("\"") && value.endsWith("\"")) {
			return value.substring(1, value.length() - 1).replace("\"+\"", "");
		} else {
			throw new RuntimeException("Could not determine variable value for " + value + " on line " + lineNumber);
		}
	}

	private GetTextFunctionType getFunctionType(String methodName, JavaParser.ArgumentListContext argumentListContext) {
		final int totalArgs = argumentListContext.expression().size();
		boolean firstArgIsLocale = true;

		if(argumentListContext.expression().size() > 1) {
			final JavaParser.AssignmentExpressionContext expressionContext =
					argumentListContext.expression(0).assignmentExpression();
			if(expressionContext != null) {
				final String value = expressionContext.assignment() != null ? expressionContext.assignment().getText().trim() :
						expressionContext.conditionalExpression().getText().trim();
				if (staticVariables.containsKey(value)) {
					firstArgIsLocale = false;
				} else if(localVariables.containsKey(value)) {
					firstArgIsLocale = false;
				} else if(value.startsWith("\"") && value.endsWith("\"")) {
					firstArgIsLocale = false;
				}
			}
		} else {
			firstArgIsLocale = false;
		}

		switch(methodName) {
		case "trnc":
			if(firstArgIsLocale) {
				return totalArgs > 4 ? GetTextFunctionType.TRNC_WITH_LOCALE_AND_VALUES : GetTextFunctionType.TRNC_WITH_LOCALE;
			} else {
				return totalArgs > 3 ? GetTextFunctionType.TRNC_WITH_VALUES : GetTextFunctionType.TRNC;
			}
		case "trn":
			if(firstArgIsLocale) {
				return totalArgs > 3 ? GetTextFunctionType.TRN_WITH_LOCALE_AND_VALUES : GetTextFunctionType.TRN_WITH_LOCALE;
			} else {
				return totalArgs > 2 ? GetTextFunctionType.TRN_WITH_VALUES : GetTextFunctionType.TRN;
			}
		case "trc":
			if(firstArgIsLocale) {
				return totalArgs > 3 ? GetTextFunctionType.TRC_WITH_LOCALE_AND_VALUES : GetTextFunctionType.TRC_WITH_LOCALE;
			} else {
				return totalArgs > 2 ? GetTextFunctionType.TRC_WITH_VALUES : GetTextFunctionType.TRC;
			}
		case "tr":
			if(firstArgIsLocale) {
				return totalArgs > 2 ? GetTextFunctionType.TR_WITH_LOCALE_AND_VALUES : GetTextFunctionType.TR_WITH_LOCALE;
			} else {
				return totalArgs > 1 ? GetTextFunctionType.TR_WITH_VALUES : GetTextFunctionType.TR;
			}
		default:
			return GetTextFunctionType.FORCE_EXTRACT;
		}
	}

	private boolean isGetTextMethod(int lineNumber, String methodName) {
		if(forceExtract.contains(lineNumber - 1)) {
			return true;
		}else if(comments.containsKey(lineNumber - 1)) {
			if(forceExtract.contains(lineNumber - 2)) {
				return true;
			}
		}
		if(methodName.equals("tr")) {
			return true;
		}
		if(methodName.equals("trn")) {
			return true;
		}
		if(methodName.equals("trc")) {
			return true;
		}
		if(methodName.equals("trnc")) {
			return true;
		}
		return false;
	}

	@Override
	public void getTranslationEntries(List<TranslationEntry> result) {
		result.addAll(translationEntries);
	}

	@Override
	public void dispose() {
		staticVariables.clear();
		instanceVariables.clear();
		localVariables.clear();
		comments.clear();
		translationEntries.clear();
	}

	protected String getComment(int line) {
		if (comments.containsKey(line)){
			return comments.get(line);
		}
		return null;
	}

	public String getRelativePath() {
		return relativePath;
	}

	private enum ParseState {
		CLASS,
		METHOD
	}
}
