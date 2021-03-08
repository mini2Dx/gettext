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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class SourceFileParser {

	public static SourceFile parse(File file, String relativePath, String commentFormat, String forceExtractFormat, String ignoreFormat) throws IOException {
		final String filename = file.getName().toLowerCase();
		if(!filename.contains(".")) {
			throw new RuntimeException("Cannot file type for file " + relativePath);
		}
		final String suffix = filename.substring(filename.lastIndexOf('.') + 1);
		switch(suffix) {
		case "lua":
			return new LuaFile(new FileInputStream(file), relativePath, commentFormat, forceExtractFormat, ignoreFormat);
		case "java":
			return new JavaFile(new FileInputStream(file), relativePath, commentFormat, forceExtractFormat, ignoreFormat);
		case "txt":
			return new TextFile(new FileInputStream(file), relativePath, commentFormat);
		default:
			throw new RuntimeException("Unable to generate .pot file from " + suffix + " file type");
		}
	}
}
