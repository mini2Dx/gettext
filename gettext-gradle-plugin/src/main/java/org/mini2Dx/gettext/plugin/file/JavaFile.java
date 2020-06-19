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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JavaFile extends JavaBaseListener implements SourceFile {
	public static final String DEFAULT_COMMENT_FORMAT = "#.";

	private final List<TranslationEntry> translationEntries = new ArrayList<TranslationEntry>();
	private final String relativePath;

	private final Map<String, String> staticVariables = new HashMap<String, String>();
	private final Map<String, String> instanceVariables = new HashMap<String, String>();
	private final Map<String, String> localVariables = new HashMap<String, String>();
	private final Map<Integer, String> comments = new HashMap<Integer, String>();

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
				} else {
					continue;
				}
				comments.put(token.getLine(), comment);
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
			staticVariables.put(ctx.variableDeclaratorId().getText(), value);
		} else {
			switch(parseState) {
			case CLASS:
				instanceVariables.put(ctx.variableDeclaratorId().getText(), value);
				break;
			default:
			case METHOD:
				localVariables.put(ctx.variableDeclaratorId().getText(), value);
				break;
			}
		}
	}

	@Override
	public void exitMethodInvocation_lfno_primary(JavaParser.MethodInvocation_lfno_primaryContext ctx) {
		generateTranslationEntry(ctx.getStart().getLine(), ctx.Identifier(), ctx.argumentList());
	}

	@Override
	public void exitMethodInvocation(JavaParser.MethodInvocationContext ctx) {
		generateTranslationEntry(ctx.getStart().getLine(), ctx.Identifier(), ctx.argumentList());
	}

	private void generateTranslationEntry(int lineNumber, TerminalNode identifier, JavaParser.ArgumentListContext argListCtxt) {
		if(identifier == null) {
			return;
		}
		if(!isGetTextMethod(identifier.getText())) {
			return;
		}
		final TranslationEntry translationEntry = new TranslationEntry();
		translationEntry.setReference(relativePath + ":" + lineNumber);

		if(comments.containsKey(lineNumber - 1)) {
			translationEntry.getExtractedComments().add(comments.get(lineNumber - 1));
		}

		final GetTextFunctionType functionType = getFunctionType(identifier, argListCtxt);
		switch(functionType) {
		case TR:
		case TR_WITH_VALUES:
			translationEntry.setId(getArgument(argListCtxt, 0));
			break;
		case TR_WITH_LOCALE:
		case TR_WITH_LOCALE_AND_VALUES:
			translationEntry.setId(getArgument(argListCtxt, 1));
			break;
		case TRC:
		case TRC_WITH_VALUES:
			translationEntry.setContext(getArgument(argListCtxt, 0));
			translationEntry.setId(getArgument(argListCtxt, 1));
			break;
		case TRC_WITH_LOCALE:
		case TRC_WITH_LOCALE_AND_VALUES:
			translationEntry.setContext(getArgument(argListCtxt, 1));
			translationEntry.setId(getArgument(argListCtxt, 2));
			break;
		case TRN:
		case TRN_WITH_VALUES:
			translationEntry.setId(getArgument(argListCtxt, 0));
			translationEntry.setIdPlural(getArgument(argListCtxt, 1));
			break;
		case TRN_WITH_LOCALE:
		case TRN_WITH_LOCALE_AND_VALUES:
			translationEntry.setId(getArgument(argListCtxt, 1));
			translationEntry.setIdPlural(getArgument(argListCtxt, 2));
			break;
		case TRNC:
		case TRNC_WITH_VALUES:
			translationEntry.setContext(getArgument(argListCtxt, 0));
			translationEntry.setId(getArgument(argListCtxt, 1));
			translationEntry.setIdPlural(getArgument(argListCtxt, 2));
			break;
		case TRNC_WITH_LOCALE:
		case TRNC_WITH_LOCALE_AND_VALUES:
			translationEntry.setContext(getArgument(argListCtxt, 1));
			translationEntry.setId(getArgument(argListCtxt, 2));
			translationEntry.setIdPlural(getArgument(argListCtxt, 3));
			break;
		}

		translationEntries.add(translationEntry);
	}

	private String getArgument(JavaParser.ArgumentListContext argumentListContext, int index) {
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
			throw new RuntimeException("Could not determine variable value for " + value);
		}
	}

	private GetTextFunctionType getFunctionType(TerminalNode identifier, JavaParser.ArgumentListContext argumentListContext) {
		final String methodName = identifier.getText();
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
		default:
		case "tr":
			if(firstArgIsLocale) {
				return totalArgs > 2 ? GetTextFunctionType.TR_WITH_LOCALE_AND_VALUES : GetTextFunctionType.TR_WITH_LOCALE;
			} else {
				return totalArgs > 1 ? GetTextFunctionType.TR_WITH_VALUES : GetTextFunctionType.TR;
			}
		}
	}

	private boolean isGetTextMethod(String methodName) {
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

	public String getRelativePath() {
		return relativePath;
	}

	private enum ParseState {
		CLASS,
		METHOD
	}
}
