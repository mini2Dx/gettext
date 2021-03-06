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

public class PoParseSettings {
	public static final PoParseSettings DEFAULT = new PoParseSettings();

	/**
	 * True if extracted comments should be stored
	 */
	public boolean extractedComments = true;

	/**
	 * True if flag comments should be stored
	 */
	public boolean flags = true;

	/**
	 * True if merge comments should be stored
	 */
	public boolean mergeComments = true;

	/**
	 * True if reference comments should be stored
	 */
	public boolean reference = true;

	/**
	 * True if translator comments should be stored
	 */
	public boolean translatorComments = true;
}
