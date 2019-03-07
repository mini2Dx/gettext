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

import org.mini2Dx.gettext.TranslationEntry;
import org.mini2Dx.gettext.plugin.antlr.LuaBaseListener;
import org.mini2Dx.gettext.plugin.antlr.LuaParser;

import java.io.File;
import java.util.List;

public class LuaFile extends LuaBaseListener implements SourceFile {

	public LuaFile(File file, String relativePath) {
		super();
	}

	@Override
	public void getTranslationEntries(List<TranslationEntry> result) {

	}

	@Override
	public void dispose() {

	}

	@Override
	public void exitFunctioncall(LuaParser.FunctioncallContext ctx) {

	}
}
