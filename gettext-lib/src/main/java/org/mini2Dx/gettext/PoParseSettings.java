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
