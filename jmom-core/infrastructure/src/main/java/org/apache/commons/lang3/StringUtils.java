/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.lang3;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.text.Normalizer;
import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * <p>Operations on {@link java.lang.String} that are
 * {@code null} safe.</p>
 * <p>
 * <ul>
 * <li><b>IsEmpty/IsBlank</b>
 * - checks if a String contains text</li>
 * <li><b>Trim/Strip</b>
 * - removes leading and trailing whitespace</li>
 * <li><b>Equals</b>
 * - compares two strings null-safe</li>
 * <li><b>startsWith</b>
 * - check if a String starts with a prefix null-safe</li>
 * <li><b>endsWith</b>
 * - check if a String ends with a suffix null-safe</li>
 * <li><b>IndexOf/LastIndexOf/Contains</b>
 * - null-safe index-of checks
 * <li><b>IndexOfAny/LastIndexOfAny/IndexOfAnyBut/LastIndexOfAnyBut</b>
 * - index-of any of a set of Strings</li>
 * <li><b>ContainsOnly/ContainsNone/ContainsAny</b>
 * - does String contains only/none/any of these characters</li>
 * <li><b>Substring/Left/Right/Mid</b>
 * - null-safe substring extractions</li>
 * <li><b>SubstringBefore/SubstringAfter/SubstringBetween</b>
 * - substring extraction relative to other strings</li>
 * <li><b>Split/Join</b>
 * - splits a String into an array of substrings and vice versa</li>
 * <li><b>Remove/Delete</b>
 * - removes part of a String</li>
 * <li><b>Replace/Overlay</b>
 * - Searches a String and replaces one String with another</li>
 * <li><b>Chomp/Chop</b>
 * - removes the last part of a String</li>
 * <li><b>AppendIfMissing</b>
 * - appends a suffix to the end of the String if not present</li>
 * <li><b>PrependIfMissing</b>
 * - prepends a prefix to the start of the String if not present</li>
 * <li><b>LeftPad/RightPad/Center/Repeat</b>
 * - pads a String</li>
 * <li><b>UpperCase/LowerCase/SwapCase/Capitalize/Uncapitalize</b>
 * - changes the case of a String</li>
 * <li><b>CountMatches</b>
 * - counts the number of occurrences of one String in another</li>
 * <li><b>IsAlpha/IsNumeric/IsWhitespace/IsAsciiPrintable</b>
 * - checks the characters in a String</li>
 * <li><b>DefaultString</b>
 * - protects against a null input String</li>
 * <li><b>Reverse/ReverseDelimited</b>
 * - reverses a String</li>
 * <li><b>Abbreviate</b>
 * - abbreviates a string using ellipsis</li>
 * <li><b>Difference</b>
 * - compares Strings and reports on their differences</li>
 * <li><b>LevenshteinDistance</b>
 * - the number of changes needed to change one String into another</li>
 * </ul>
 * <p>
 * <p>The {@code StringUtils} class defines certain words related to
 * String handling.</p>
 * <p>
 * <ul>
 * <li>null - {@code null}</li>
 * <li>empty - a zero-length string ({@code ""})</li>
 * <li>space - the space character ({@code ' '}, char 32)</li>
 * <li>whitespace - the characters defined by {@link Character#isWhitespace(char)}</li>
 * <li>trim - the characters &lt;= 32 as in {@link String#trim()}</li>
 * </ul>
 * <p>
 * <p>{@code StringUtils} handles {@code null} input Strings quietly.
 * That is to say that a {@code null} input will return {@code null}.
 * Where a {@code boolean} or {@code int} is being returned
 * details vary by method.</p>
 * <p>
 * <p>A side effect of the {@code null} handling is that a
 * {@code NullPointerException} should be considered a bug in
 * {@code StringUtils}.</p>
 * <p>
 * <p>Methods in this class give sample code to explain their operation.
 * The symbol {@code *} is used to indicate any input including {@code null}.</p>
 * <p>
 * <p>#ThreadSafe#</p>
 *
 * @version $Id: StringUtils.java 1583482 2014-03-31 22:54:57Z niallp $
 * @see java.lang.String
 * @since 1.0
 */
//@Immutable
public class StringUtils {
    // Performance testing notes (JDK 1.4, Jul03, scolebourne)
    // Whitespace:
    // Character.isWhitespace() is faster than WHITESPACE.indexOf()
    // where WHITESPACE is a string of all whitespace characters
    //
    // Character access:
    // String.charAt(n) versus toCharArray(), then array[n]
    // String.charAt(n) is about 15% worse for a 10K string
    // They are about equal for a length 50 string
    // String.charAt(n) is about 4 times better for a length 3 string
    // String.charAt(n) is best bet overall
    //
    // Append:
    // String.concat about twice as fast as StringBuffer.append
    // (not sure who tested this)

    /**
     * A String for a space character.
     *
     * @since 3.2
     */
    public static final String SPACE = " ";

    /**
     * The empty String {@code ""}.
     *
     * @since 2.0
     */
    public static final String EMPTY = "";

    /**
     * A String for linefeed LF ("\n").
     *
     * @see <a href="http://docs.oracle.com/javase/specs/jls/se7/html/jls-3.html#jls-3.10.6">JLF: Escape Sequences
     * for Character and String Literals</a>
     * @since 3.2
     */
    public static final String LF = "\n";

    /**
     * A String for carriage return CR ("\r").
     *
     * @see <a href="http://docs.oracle.com/javase/specs/jls/se7/html/jls-3.html#jls-3.10.6">JLF: Escape Sequences
     * for Character and String Literals</a>
     * @since 3.2
     */
    public static final String CR = "\r";

    /**
     * Represents a failed index search.
     *
     * @since 2.1
     */
    public static final int INDEX_NOT_FOUND = -1;

    /**
     * <p>The maximum size to which the padding constant(s) can expand.</p>
     */
    private static final int PAD_LIMIT = 8192;

    /**
     * A regex pattern for recognizing blocks of whitespace characters.
     * The apparent convolutedness of the pattern serves the purpose of
     * ignoring "blocks" consisting of only a single space:  the pattern
     * is used only to normalize whitespace, condensing "blocks" down to a
     * single space, thus matching the same would likely cause a great
     * many noop replacements.
     */
    private static final Pattern WHITESPACE_PATTERN = Pattern.compile("(?: |\\u00A0|\\s|[\\s&&[^ ]])\\s*");

    /**
     * <p>{@code StringUtils} instances should NOT be constructed in
     * standard programming. Instead, the class should be used as
     * {@code StringUtils.trim(" foo ");}.</p>
     * <p>
     * <p>This constructor is public to permit tools that require a JavaBean
     * instance to operate.</p>
     */
    public StringUtils() {
        super();
    }

    // Empty checks
    //-----------------------------------------------------------------------

    /**
     * <p>Checks if a CharSequence is empty ("") or null.</p>
     * <p>
     * <pre>
     * StringUtils.isEmpty(null)      = true
     * StringUtils.isEmpty("")        = true
     * StringUtils.isEmpty(" ")       = false
     * StringUtils.isEmpty("bob")     = false
     * StringUtils.isEmpty("  bob  ") = false
     * </pre>
     * <p>
     * <p>NOTE: This method changed in Lang version 2.0.
     * It no longer trims the CharSequence.
     * That functionality is available in isBlank().</p>
     *
     * @param cs the CharSequence to check, may be null
     * @return {@code true} if the CharSequence is empty or null
     * @since 3.0 Changed signature from isEmpty(String) to isEmpty(CharSequence)
     */
    public static boolean isEmpty(final CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    /**
     * <p>Checks if a CharSequence is not empty ("") and not null.</p>
     * <p>
     * <pre>
     * StringUtils.isNotEmpty(null)      = false
     * StringUtils.isNotEmpty("")        = false
     * StringUtils.isNotEmpty(" ")       = true
     * StringUtils.isNotEmpty("bob")     = true
     * StringUtils.isNotEmpty("  bob  ") = true
     * </pre>
     *
     * @param cs the CharSequence to check, may be null
     * @return {@code true} if the CharSequence is not empty and not null
     * @since 3.0 Changed signature from isNotEmpty(String) to isNotEmpty(CharSequence)
     */
    public static boolean isNotEmpty(final CharSequence cs) {
        return !StringUtils.isEmpty(cs);
    }

    /**
     * <p>Checks if a CharSequence is whitespace, empty ("") or null.</p>
     * <p>
     * <pre>
     * StringUtils.isBlank(null)      = true
     * StringUtils.isBlank("")        = true
     * StringUtils.isBlank(" ")       = true
     * StringUtils.isBlank("bob")     = false
     * StringUtils.isBlank("  bob  ") = false
     * </pre>
     *
     * @param cs the CharSequence to check, may be null
     * @return {@code true} if the CharSequence is null, empty or whitespace
     * @since 3.0 Changed signature from isBlank(String) to isBlank(CharSequence)
     */
    public static boolean isBlank(final CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (Character.isWhitespace(cs.charAt(i)) == false) {
                return false;
            }
        }
        return true;
    }

    /**
     * <p>Checks if a CharSequence is not empty (""), not null and not whitespace only.</p>
     * <p>
     * <pre>
     * StringUtils.isNotBlank(null)      = false
     * StringUtils.isNotBlank("")        = false
     * StringUtils.isNotBlank(" ")       = false
     * StringUtils.isNotBlank("bob")     = true
     * StringUtils.isNotBlank("  bob  ") = true
     * </pre>
     *
     * @param cs the CharSequence to check, may be null
     * @return {@code true} if the CharSequence is
     * not empty and not null and not whitespace
     * @since 3.0 Changed signature from isNotBlank(String) to isNotBlank(CharSequence)
     */
    public static boolean isNotBlank(final CharSequence cs) {
        return !StringUtils.isBlank(cs);
    }

    // Trim
    //-----------------------------------------------------------------------

    /**
     * <p>Removes control characters (char &lt;= 32) from both
     * ends of this String, handling {@code null} by returning
     * {@code null}.</p>
     * <p>
     * <p>The String is trimmed using {@link String#trim()}.
     * Trim removes start and end characters &lt;= 32.
     * To strip whitespace use {@link #strip(String)}.</p>
     * <p>
     * <p>To trim your choice of characters, use the
     * {@link #strip(String, String)} methods.</p>
     * <p>
     * <pre>
     * StringUtils.trim(null)          = null
     * StringUtils.trim("")            = ""
     * StringUtils.trim("     ")       = ""
     * StringUtils.trim("abc")         = "abc"
     * StringUtils.trim("    abc    ") = "abc"
     * </pre>
     *
     * @param str the String to be trimmed, may be null
     * @return the trimmed string, {@code null} if null String input
     */
    public static String trim(final String str) {
        return str == null ? null : str.trim();
    }

    /**
     * <p>Removes control characters (char &lt;= 32) from both
     * ends of this String returning {@code null} if the String is
     * empty ("") after the trim or if it is {@code null}.
     * <p>
     * <p>The String is trimmed using {@link String#trim()}.
     * Trim removes start and end characters &lt;= 32.
     * To strip whitespace use {@link #stripToNull(String)}.</p>
     * <p>
     * <pre>
     * StringUtils.trimToNull(null)          = null
     * StringUtils.trimToNull("")            = null
     * StringUtils.trimToNull("     ")       = null
     * StringUtils.trimToNull("abc")         = "abc"
     * StringUtils.trimToNull("    abc    ") = "abc"
     * </pre>
     *
     * @param str the String to be trimmed, may be null
     * @return the trimmed String,
     * {@code null} if only chars &lt;= 32, empty or null String input
     * @since 2.0
     */
    public static String trimToNull(final String str) {
        final String ts = trim(str);
        return isEmpty(ts) ? null : ts;
    }

    /**
     * <p>Removes control characters (char &lt;= 32) from both
     * ends of this String returning an empty String ("") if the String
     * is empty ("") after the trim or if it is {@code null}.
     * <p>
     * <p>The String is trimmed using {@link String#trim()}.
     * Trim removes start and end characters &lt;= 32.
     * To strip whitespace use {@link #stripToEmpty(String)}.</p>
     * <p>
     * <pre>
     * StringUtils.trimToEmpty(null)          = ""
     * StringUtils.trimToEmpty("")            = ""
     * StringUtils.trimToEmpty("     ")       = ""
     * StringUtils.trimToEmpty("abc")         = "abc"
     * StringUtils.trimToEmpty("    abc    ") = "abc"
     * </pre>
     *
     * @param str the String to be trimmed, may be null
     * @return the trimmed String, or an empty String if {@code null} input
     * @since 2.0
     */
    public static String trimToEmpty(final String str) {
        return str == null ? EMPTY : str.trim();
    }

    // Stripping
    //-----------------------------------------------------------------------

    /**
     * <p>Strips whitespace from the start and end of a String.</p>
     * <p>
     * <p>This is similar to {@link #trim(String)} but removes whitespace.
     * Whitespace is defined by {@link Character#isWhitespace(char)}.</p>
     * <p>
     * <p>A {@code null} input String returns {@code null}.</p>
     * <p>
     * <pre>
     * StringUtils.strip(null)     = null
     * StringUtils.strip("")       = ""
     * StringUtils.strip("   ")    = ""
     * StringUtils.strip("abc")    = "abc"
     * StringUtils.strip("  abc")  = "abc"
     * StringUtils.strip("abc  ")  = "abc"
     * StringUtils.strip(" abc ")  = "abc"
     * StringUtils.strip(" ab c ") = "ab c"
     * </pre>
     *
     * @param str the String to remove whitespace from, may be null
     * @return the stripped String, {@code null} if null String input
     */
    public static String strip(final String str) {
        return strip(str, null);
    }

    /**
     * <p>Strips whitespace from the start and end of a String  returning
     * {@code null} if the String is empty ("") after the strip.</p>
     * <p>
     * <p>This is similar to {@link #trimToNull(String)} but removes whitespace.
     * Whitespace is defined by {@link Character#isWhitespace(char)}.</p>
     * <p>
     * <pre>
     * StringUtils.stripToNull(null)     = null
     * StringUtils.stripToNull("")       = null
     * StringUtils.stripToNull("   ")    = null
     * StringUtils.stripToNull("abc")    = "abc"
     * StringUtils.stripToNull("  abc")  = "abc"
     * StringUtils.stripToNull("abc  ")  = "abc"
     * StringUtils.stripToNull(" abc ")  = "abc"
     * StringUtils.stripToNull(" ab c ") = "ab c"
     * </pre>
     *
     * @param str the String to be stripped, may be null
     * @return the stripped String,
     * {@code null} if whitespace, empty or null String input
     * @since 2.0
     */
    public static String stripToNull(String str) {
        if (str == null) {
            return null;
        }
        str = strip(str, null);
        return str.isEmpty() ? null : str;
    }

    /**
     * <p>Strips whitespace from the start and end of a String  returning
     * an empty String if {@code null} input.</p>
     * <p>
     * <p>This is similar to {@link #trimToEmpty(String)} but removes whitespace.
     * Whitespace is defined by {@link Character#isWhitespace(char)}.</p>
     * <p>
     * <pre>
     * StringUtils.stripToEmpty(null)     = ""
     * StringUtils.stripToEmpty("")       = ""
     * StringUtils.stripToEmpty("   ")    = ""
     * StringUtils.stripToEmpty("abc")    = "abc"
     * StringUtils.stripToEmpty("  abc")  = "abc"
     * StringUtils.stripToEmpty("abc  ")  = "abc"
     * StringUtils.stripToEmpty(" abc ")  = "abc"
     * StringUtils.stripToEmpty(" ab c ") = "ab c"
     * </pre>
     *
     * @param str the String to be stripped, may be null
     * @return the trimmed String, or an empty String if {@code null} input
     * @since 2.0
     */
    public static String stripToEmpty(final String str) {
        return str == null ? EMPTY : strip(str, null);
    }

    /**
     * <p>Strips any of a set of characters from the start and end of a String.
     * This is similar to {@link String#trim()} but allows the characters
     * to be stripped to be controlled.</p>
     * <p>
     * <p>A {@code null} input String returns {@code null}.
     * An empty string ("") input returns the empty string.</p>
     * <p>
     * <p>If the stripChars String is {@code null}, whitespace is
     * stripped as defined by {@link Character#isWhitespace(char)}.
     * Alternatively use {@link #strip(String)}.</p>
     * <p>
     * <pre>
     * StringUtils.strip(null, *)          = null
     * StringUtils.strip("", *)            = ""
     * StringUtils.strip("abc", null)      = "abc"
     * StringUtils.strip("  abc", null)    = "abc"
     * StringUtils.strip("abc  ", null)    = "abc"
     * StringUtils.strip(" abc ", null)    = "abc"
     * StringUtils.strip("  abcyx", "xyz") = "  abc"
     * </pre>
     *
     * @param str        the String to remove characters from, may be null
     * @param stripChars the characters to remove, null treated as whitespace
     * @return the stripped String, {@code null} if null String input
     */
    public static String strip(String str, final String stripChars) {
        if (isEmpty(str)) {
            return str;
        }
        str = stripStart(str, stripChars);
        return stripEnd(str, stripChars);
    }

    /**
     * <p>Strips any of a set of characters from the start of a String.</p>
     * <p>
     * <p>A {@code null} input String returns {@code null}.
     * An empty string ("") input returns the empty string.</p>
     * <p>
     * <p>If the stripChars String is {@code null}, whitespace is
     * stripped as defined by {@link Character#isWhitespace(char)}.</p>
     * <p>
     * <pre>
     * StringUtils.stripStart(null, *)          = null
     * StringUtils.stripStart("", *)            = ""
     * StringUtils.stripStart("abc", "")        = "abc"
     * StringUtils.stripStart("abc", null)      = "abc"
     * StringUtils.stripStart("  abc", null)    = "abc"
     * StringUtils.stripStart("abc  ", null)    = "abc  "
     * StringUtils.stripStart(" abc ", null)    = "abc "
     * StringUtils.stripStart("yxabc  ", "xyz") = "abc  "
     * </pre>
     *
     * @param str        the String to remove characters from, may be null
     * @param stripChars the characters to remove, null treated as whitespace
     * @return the stripped String, {@code null} if null String input
     */
    public static String stripStart(final String str, final String stripChars) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return str;
        }
        int start = 0;
        if (stripChars == null) {
            while (start != strLen && Character.isWhitespace(str.charAt(start))) {
                start++;
            }
        } else if (stripChars.isEmpty()) {
            return str;
        } else {
            while (start != strLen && stripChars.indexOf(str.charAt(start)) != INDEX_NOT_FOUND) {
                start++;
            }
        }
        return str.substring(start);
    }

    /**
     * <p>Strips any of a set of characters from the end of a String.</p>
     * <p>
     * <p>A {@code null} input String returns {@code null}.
     * An empty string ("") input returns the empty string.</p>
     * <p>
     * <p>If the stripChars String is {@code null}, whitespace is
     * stripped as defined by {@link Character#isWhitespace(char)}.</p>
     * <p>
     * <pre>
     * StringUtils.stripEnd(null, *)          = null
     * StringUtils.stripEnd("", *)            = ""
     * StringUtils.stripEnd("abc", "")        = "abc"
     * StringUtils.stripEnd("abc", null)      = "abc"
     * StringUtils.stripEnd("  abc", null)    = "  abc"
     * StringUtils.stripEnd("abc  ", null)    = "abc"
     * StringUtils.stripEnd(" abc ", null)    = " abc"
     * StringUtils.stripEnd("  abcyx", "xyz") = "  abc"
     * StringUtils.stripEnd("120.00", ".0")   = "12"
     * </pre>
     *
     * @param str        the String to remove characters from, may be null
     * @param stripChars the set of characters to remove, null treated as whitespace
     * @return the stripped String, {@code null} if null String input
     */
    public static String stripEnd(final String str, final String stripChars) {
        int end;
        if (str == null || (end = str.length()) == 0) {
            return str;
        }

        if (stripChars == null) {
            while (end != 0 && Character.isWhitespace(str.charAt(end - 1))) {
                end--;
            }
        } else if (stripChars.isEmpty()) {
            return str;
        } else {
            while (end != 0 && stripChars.indexOf(str.charAt(end - 1)) != INDEX_NOT_FOUND) {
                end--;
            }
        }
        return str.substring(0, end);
    }

    // StripAll
    //-----------------------------------------------------------------------

    /**
     * <p>Strips whitespace from the start and end of every String in an array.
     * Whitespace is defined by {@link Character#isWhitespace(char)}.</p>
     * <p>
     * <p>A new array is returned each time, except for length zero.
     * A {@code null} array will return {@code null}.
     * An empty array will return itself.
     * A {@code null} array entry will be ignored.</p>
     * <p>
     * <pre>
     * StringUtils.stripAll(null)             = null
     * StringUtils.stripAll([])               = []
     * StringUtils.stripAll(["abc", "  abc"]) = ["abc", "abc"]
     * StringUtils.stripAll(["abc  ", null])  = ["abc", null]
     * </pre>
     *
     * @param strs the array to remove whitespace from, may be null
     * @return the stripped Strings, {@code null} if null array input
     */
    public static String[] stripAll(final String... strs) {
        return stripAll(strs, null);
    }

    /**
     * <p>Strips any of a set of characters from the start and end of every
     * String in an array.</p>
     * <p>Whitespace is defined by {@link Character#isWhitespace(char)}.</p>
     * <p>
     * <p>A new array is returned each time, except for length zero.
     * A {@code null} array will return {@code null}.
     * An empty array will return itself.
     * A {@code null} array entry will be ignored.
     * A {@code null} stripChars will strip whitespace as defined by
     * {@link Character#isWhitespace(char)}.</p>
     * <p>
     * <pre>
     * StringUtils.stripAll(null, *)                = null
     * StringUtils.stripAll([], *)                  = []
     * StringUtils.stripAll(["abc", "  abc"], null) = ["abc", "abc"]
     * StringUtils.stripAll(["abc  ", null], null)  = ["abc", null]
     * StringUtils.stripAll(["abc  ", null], "yz")  = ["abc  ", null]
     * StringUtils.stripAll(["yabcz", null], "yz")  = ["abc", null]
     * </pre>
     *
     * @param strs       the array to remove characters from, may be null
     * @param stripChars the characters to remove, null treated as whitespace
     * @return the stripped Strings, {@code null} if null array input
     */
    public static String[] stripAll(final String[] strs, final String stripChars) {
        int strsLen;
        if (strs == null || (strsLen = strs.length) == 0) {
            return strs;
        }
        final String[] newArr = new String[strsLen];
        for (int i = 0; i < strsLen; i++) {
            newArr[i] = strip(strs[i], stripChars);
        }
        return newArr;
    }

    /**
     * <p>Removes diacritics (~= accents) from a string. The case will not be altered.</p>
     * <p>For instance, '&agrave;' will be replaced by 'a'.</p>
     * <p>Note that ligatures will be left as is.</p>
     * <p>
     * <pre>
     * StringUtils.stripAccents(null)                = null
     * StringUtils.stripAccents("")                  = ""
     * StringUtils.stripAccents("control")           = "control"
     * StringUtils.stripAccents("&eacute;clair")     = "eclair"
     * </pre>
     *
     * @param input String to be stripped
     * @return input text with diacritics removed
     * @since 3.0
     */
    // See also Lucene's ASCIIFoldingFilter (Lucene 2.9) that replaces accented characters by their unaccented equivalent (and uncommitted bug fix: https://issues.apache.org/jira/browse/LUCENE-1343?focusedCommentId=12858907&page=com.atlassian.jira.plugin.system.issuetabpanels%3Acomment-tabpanel#action_12858907).
    public static String stripAccents(final String input) {
        if (input == null) {
            return null;
        }
        final Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");//$NON-NLS-1$
        final String decomposed = Normalizer.normalize(input, Normalizer.Form.NFD);
        // Note that this doesn't correctly remove ligatures...
        return pattern.matcher(decomposed).replaceAll("");//$NON-NLS-1$
    }

    /**
     * Check whether the given CharSequence contains any whitespace characters.
     *
     * @param seq the CharSequence to check (may be {@code null})
     * @return {@code true} if the CharSequence is not empty and
     * contains at least 1 whitespace character
     * @see java.lang.Character#isWhitespace
     * @since 3.0
     */
    // From org.springframework.util.StringUtils, under Apache License 2.0
    public static boolean containsWhitespace(final CharSequence seq) {
        if (isEmpty(seq)) {
            return false;
        }
        final int strLen = seq.length();
        for (int i = 0; i < strLen; i++) {
            if (Character.isWhitespace(seq.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    // ContainsNone
    //-----------------------------------------------------------------------

    /**
     * <p>Checks that the CharSequence does not contain certain characters.</p>
     * <p>
     * <p>A {@code null} CharSequence will return {@code true}.
     * A {@code null} invalid character array will return {@code true}.
     * An empty CharSequence (length()=0) always returns true.</p>
     * <p>
     * <pre>
     * StringUtils.containsNone(null, *)       = true
     * StringUtils.containsNone(*, null)       = true
     * StringUtils.containsNone("", *)         = true
     * StringUtils.containsNone("ab", '')      = true
     * StringUtils.containsNone("abab", 'xyz') = true
     * StringUtils.containsNone("ab1", 'xyz')  = true
     * StringUtils.containsNone("abz", 'xyz')  = false
     * </pre>
     *
     * @param cs          the CharSequence to check, may be null
     * @param searchChars an array of invalid chars, may be null
     * @return true if it contains none of the invalid chars, or is null
     * @since 3.0 Changed signature from containsNone(String, char[]) to containsNone(CharSequence, char...)
     */
    public static boolean containsNone(final CharSequence cs, final char... searchChars) {
        if (cs == null || searchChars == null) {
            return true;
        }
        final int csLen = cs.length();
        final int csLast = csLen - 1;
        final int searchLen = searchChars.length;
        final int searchLast = searchLen - 1;
        for (int i = 0; i < csLen; i++) {
            final char ch = cs.charAt(i);
            for (int j = 0; j < searchLen; j++) {
                if (searchChars[j] == ch) {
                    if (Character.isHighSurrogate(ch)) {
                        if (j == searchLast) {
                            // missing low surrogate, fine, like String.indexOf(String)
                            return false;
                        }
                        if (i < csLast && searchChars[j + 1] == cs.charAt(i + 1)) {
                            return false;
                        }
                    } else {
                        // ch is in the Basic Multilingual Plane
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * <p>Checks that the CharSequence does not contain certain characters.</p>
     * <p>
     * <p>A {@code null} CharSequence will return {@code true}.
     * A {@code null} invalid character array will return {@code true}.
     * An empty String ("") always returns true.</p>
     * <p>
     * <pre>
     * StringUtils.containsNone(null, *)       = true
     * StringUtils.containsNone(*, null)       = true
     * StringUtils.containsNone("", *)         = true
     * StringUtils.containsNone("ab", "")      = true
     * StringUtils.containsNone("abab", "xyz") = true
     * StringUtils.containsNone("ab1", "xyz")  = true
     * StringUtils.containsNone("abz", "xyz")  = false
     * </pre>
     *
     * @param cs           the CharSequence to check, may be null
     * @param invalidChars a String of invalid chars, may be null
     * @return true if it contains none of the invalid chars, or is null
     * @since 3.0 Changed signature from containsNone(String, String) to containsNone(CharSequence, String)
     */
    public static boolean containsNone(final CharSequence cs, final String invalidChars) {
        if (cs == null || invalidChars == null) {
            return true;
        }
        return containsNone(cs, invalidChars.toCharArray());
    }

    // Substring
    //-----------------------------------------------------------------------

    /**
     * <p>Gets a substring from the specified String avoiding exceptions.</p>
     * <p>
     * <p>A negative start position can be used to start {@code n}
     * characters from the end of the String.</p>
     * <p>
     * <p>A {@code null} String will return {@code null}.
     * An empty ("") String will return "".</p>
     * <p>
     * <pre>
     * StringUtils.substring(null, *)   = null
     * StringUtils.substring("", *)     = ""
     * StringUtils.substring("abc", 0)  = "abc"
     * StringUtils.substring("abc", 2)  = "c"
     * StringUtils.substring("abc", 4)  = ""
     * StringUtils.substring("abc", -2) = "bc"
     * StringUtils.substring("abc", -4) = "abc"
     * </pre>
     *
     * @param str   the String to get the substring from, may be null
     * @param start the position to start from, negative means
     *              count back from the end of the String by this many characters
     * @return substring from start position, {@code null} if null String input
     */
    public static String substring(final String str, int start) {
        if (str == null) {
            return null;
        }

        // handle negatives, which means last n characters
        if (start < 0) {
            start = str.length() + start; // remember start is negative
        }

        if (start < 0) {
            start = 0;
        }
        if (start > str.length()) {
            return EMPTY;
        }

        return str.substring(start);
    }

    /**
     * <p>Gets a substring from the specified String avoiding exceptions.</p>
     * <p>
     * <p>A negative start position can be used to start/end {@code n}
     * characters from the end of the String.</p>
     * <p>
     * <p>The returned substring starts with the character in the {@code start}
     * position and ends before the {@code end} position. All position counting is
     * zero-based -- i.e., to start at the beginning of the string use
     * {@code start = 0}. Negative start and end positions can be used to
     * specify offsets relative to the end of the String.</p>
     * <p>
     * <p>If {@code start} is not strictly to the left of {@code end}, ""
     * is returned.</p>
     * <p>
     * <pre>
     * StringUtils.substring(null, *, *)    = null
     * StringUtils.substring("", * ,  *)    = "";
     * StringUtils.substring("abc", 0, 2)   = "ab"
     * StringUtils.substring("abc", 2, 0)   = ""
     * StringUtils.substring("abc", 2, 4)   = "c"
     * StringUtils.substring("abc", 4, 6)   = ""
     * StringUtils.substring("abc", 2, 2)   = ""
     * StringUtils.substring("abc", -2, -1) = "b"
     * StringUtils.substring("abc", -4, 2)  = "ab"
     * </pre>
     *
     * @param str   the String to get the substring from, may be null
     * @param start the position to start from, negative means
     *              count back from the end of the String by this many characters
     * @param end   the position to end at (exclusive), negative means
     *              count back from the end of the String by this many characters
     * @return substring from start position to end position,
     * {@code null} if null String input
     */
    public static String substring(final String str, int start, int end) {
        if (str == null) {
            return null;
        }

        // handle negatives
        if (end < 0) {
            end = str.length() + end; // remember end is negative
        }
        if (start < 0) {
            start = str.length() + start; // remember start is negative
        }

        // check length next
        if (end > str.length()) {
            end = str.length();
        }

        // if start is greater than end, return ""
        if (start > end) {
            return EMPTY;
        }

        if (start < 0) {
            start = 0;
        }
        if (end < 0) {
            end = 0;
        }

        return str.substring(start, end);
    }

    // Left/Right/Mid
    //-----------------------------------------------------------------------

    /**
     * <p>Gets the leftmost {@code len} characters of a String.</p>
     * <p>
     * <p>If {@code len} characters are not available, or the
     * String is {@code null}, the String will be returned without
     * an exception. An empty String is returned if len is negative.</p>
     * <p>
     * <pre>
     * StringUtils.left(null, *)    = null
     * StringUtils.left(*, -ve)     = ""
     * StringUtils.left("", *)      = ""
     * StringUtils.left("abc", 0)   = ""
     * StringUtils.left("abc", 2)   = "ab"
     * StringUtils.left("abc", 4)   = "abc"
     * </pre>
     *
     * @param str the String to get the leftmost characters from, may be null
     * @param len the length of the required String
     * @return the leftmost characters, {@code null} if null String input
     */
    public static String left(final String str, final int len) {
        if (str == null) {
            return null;
        }
        if (len < 0) {
            return EMPTY;
        }
        if (str.length() <= len) {
            return str;
        }
        return str.substring(0, len);
    }

    /**
     * <p>Gets the rightmost {@code len} characters of a String.</p>
     * <p>
     * <p>If {@code len} characters are not available, or the String
     * is {@code null}, the String will be returned without an
     * an exception. An empty String is returned if len is negative.</p>
     * <p>
     * <pre>
     * StringUtils.right(null, *)    = null
     * StringUtils.right(*, -ve)     = ""
     * StringUtils.right("", *)      = ""
     * StringUtils.right("abc", 0)   = ""
     * StringUtils.right("abc", 2)   = "bc"
     * StringUtils.right("abc", 4)   = "abc"
     * </pre>
     *
     * @param str the String to get the rightmost characters from, may be null
     * @param len the length of the required String
     * @return the rightmost characters, {@code null} if null String input
     */
    public static String right(final String str, final int len) {
        if (str == null) {
            return null;
        }
        if (len < 0) {
            return EMPTY;
        }
        if (str.length() <= len) {
            return str;
        }
        return str.substring(str.length() - len);
    }

    /**
     * <p>Gets {@code len} characters from the middle of a String.</p>
     * <p>
     * <p>If {@code len} characters are not available, the remainder
     * of the String will be returned without an exception. If the
     * String is {@code null}, {@code null} will be returned.
     * An empty String is returned if len is negative or exceeds the
     * length of {@code str}.</p>
     * <p>
     * <pre>
     * StringUtils.mid(null, *, *)    = null
     * StringUtils.mid(*, *, -ve)     = ""
     * StringUtils.mid("", 0, *)      = ""
     * StringUtils.mid("abc", 0, 2)   = "ab"
     * StringUtils.mid("abc", 0, 4)   = "abc"
     * StringUtils.mid("abc", 2, 4)   = "c"
     * StringUtils.mid("abc", 4, 2)   = ""
     * StringUtils.mid("abc", -2, 2)  = "ab"
     * </pre>
     *
     * @param str the String to get the characters from, may be null
     * @param pos the position to start from, negative treated as zero
     * @param len the length of the required String
     * @return the middle characters, {@code null} if null String input
     */
    public static String mid(final String str, int pos, final int len) {
        if (str == null) {
            return null;
        }
        if (len < 0 || pos > str.length()) {
            return EMPTY;
        }
        if (pos < 0) {
            pos = 0;
        }
        if (str.length() <= pos + len) {
            return str.substring(pos);
        }
        return str.substring(pos, pos + len);
    }

    // SubStringAfter/SubStringBefore
    //-----------------------------------------------------------------------

    /**
     * <p>Gets the substring before the first occurrence of a separator.
     * The separator is not returned.</p>
     * <p>
     * <p>A {@code null} string input will return {@code null}.
     * An empty ("") string input will return the empty string.
     * A {@code null} separator will return the input string.</p>
     * <p>
     * <p>If nothing is found, the string input is returned.</p>
     * <p>
     * <pre>
     * StringUtils.substringBefore(null, *)      = null
     * StringUtils.substringBefore("", *)        = ""
     * StringUtils.substringBefore("abc", "a")   = ""
     * StringUtils.substringBefore("abcba", "b") = "a"
     * StringUtils.substringBefore("abc", "c")   = "ab"
     * StringUtils.substringBefore("abc", "d")   = "abc"
     * StringUtils.substringBefore("abc", "")    = ""
     * StringUtils.substringBefore("abc", null)  = "abc"
     * </pre>
     *
     * @param str       the String to get a substring from, may be null
     * @param separator the String to search for, may be null
     * @return the substring before the first occurrence of the separator,
     * {@code null} if null String input
     * @since 2.0
     */
    public static String substringBefore(final String str, final String separator) {
        if (isEmpty(str) || separator == null) {
            return str;
        }
        if (separator.isEmpty()) {
            return EMPTY;
        }
        final int pos = str.indexOf(separator);
        if (pos == INDEX_NOT_FOUND) {
            return str;
        }
        return str.substring(0, pos);
    }

    /**
     * <p>Gets the substring after the first occurrence of a separator.
     * The separator is not returned.</p>
     * <p>
     * <p>A {@code null} string input will return {@code null}.
     * An empty ("") string input will return the empty string.
     * A {@code null} separator will return the empty string if the
     * input string is not {@code null}.</p>
     * <p>
     * <p>If nothing is found, the empty string is returned.</p>
     * <p>
     * <pre>
     * StringUtils.substringAfter(null, *)      = null
     * StringUtils.substringAfter("", *)        = ""
     * StringUtils.substringAfter(*, null)      = ""
     * StringUtils.substringAfter("abc", "a")   = "bc"
     * StringUtils.substringAfter("abcba", "b") = "cba"
     * StringUtils.substringAfter("abc", "c")   = ""
     * StringUtils.substringAfter("abc", "d")   = ""
     * StringUtils.substringAfter("abc", "")    = "abc"
     * </pre>
     *
     * @param str       the String to get a substring from, may be null
     * @param separator the String to search for, may be null
     * @return the substring after the first occurrence of the separator,
     * {@code null} if null String input
     * @since 2.0
     */
    public static String substringAfter(final String str, final String separator) {
        if (isEmpty(str)) {
            return str;
        }
        if (separator == null) {
            return EMPTY;
        }
        final int pos = str.indexOf(separator);
        if (pos == INDEX_NOT_FOUND) {
            return EMPTY;
        }
        return str.substring(pos + separator.length());
    }

    /**
     * <p>Gets the substring before the last occurrence of a separator.
     * The separator is not returned.</p>
     * <p>
     * <p>A {@code null} string input will return {@code null}.
     * An empty ("") string input will return the empty string.
     * An empty or {@code null} separator will return the input string.</p>
     * <p>
     * <p>If nothing is found, the string input is returned.</p>
     * <p>
     * <pre>
     * StringUtils.substringBeforeLast(null, *)      = null
     * StringUtils.substringBeforeLast("", *)        = ""
     * StringUtils.substringBeforeLast("abcba", "b") = "abc"
     * StringUtils.substringBeforeLast("abc", "c")   = "ab"
     * StringUtils.substringBeforeLast("a", "a")     = ""
     * StringUtils.substringBeforeLast("a", "z")     = "a"
     * StringUtils.substringBeforeLast("a", null)    = "a"
     * StringUtils.substringBeforeLast("a", "")      = "a"
     * </pre>
     *
     * @param str       the String to get a substring from, may be null
     * @param separator the String to search for, may be null
     * @return the substring before the last occurrence of the separator,
     * {@code null} if null String input
     * @since 2.0
     */
    public static String substringBeforeLast(final String str, final String separator) {
        if (isEmpty(str) || isEmpty(separator)) {
            return str;
        }
        final int pos = str.lastIndexOf(separator);
        if (pos == INDEX_NOT_FOUND) {
            return str;
        }
        return str.substring(0, pos);
    }

    /**
     * <p>Gets the substring after the last occurrence of a separator.
     * The separator is not returned.</p>
     * <p>
     * <p>A {@code null} string input will return {@code null}.
     * An empty ("") string input will return the empty string.
     * An empty or {@code null} separator will return the empty string if
     * the input string is not {@code null}.</p>
     * <p>
     * <p>If nothing is found, the empty string is returned.</p>
     * <p>
     * <pre>
     * StringUtils.substringAfterLast(null, *)      = null
     * StringUtils.substringAfterLast("", *)        = ""
     * StringUtils.substringAfterLast(*, "")        = ""
     * StringUtils.substringAfterLast(*, null)      = ""
     * StringUtils.substringAfterLast("abc", "a")   = "bc"
     * StringUtils.substringAfterLast("abcba", "b") = "a"
     * StringUtils.substringAfterLast("abc", "c")   = ""
     * StringUtils.substringAfterLast("a", "a")     = ""
     * StringUtils.substringAfterLast("a", "z")     = ""
     * </pre>
     *
     * @param str       the String to get a substring from, may be null
     * @param separator the String to search for, may be null
     * @return the substring after the last occurrence of the separator,
     * {@code null} if null String input
     * @since 2.0
     */
    public static String substringAfterLast(final String str, final String separator) {
        if (isEmpty(str)) {
            return str;
        }
        if (isEmpty(separator)) {
            return EMPTY;
        }
        final int pos = str.lastIndexOf(separator);
        if (pos == INDEX_NOT_FOUND || pos == str.length() - separator.length()) {
            return EMPTY;
        }
        return str.substring(pos + separator.length());
    }

    // Substring between
    //-----------------------------------------------------------------------

    /**
     * <p>Gets the String that is nested in between two instances of the
     * same String.</p>
     * <p>
     * <p>A {@code null} input String returns {@code null}.
     * A {@code null} tag returns {@code null}.</p>
     * <p>
     * <pre>
     * StringUtils.substringBetween(null, *)            = null
     * StringUtils.substringBetween("", "")             = ""
     * StringUtils.substringBetween("", "tag")          = null
     * StringUtils.substringBetween("tagabctag", null)  = null
     * StringUtils.substringBetween("tagabctag", "")    = ""
     * StringUtils.substringBetween("tagabctag", "tag") = "abc"
     * </pre>
     *
     * @param str the String containing the substring, may be null
     * @param tag the String before and after the substring, may be null
     * @return the substring, {@code null} if no match
     * @since 2.0
     */
    public static String substringBetween(final String str, final String tag) {
        return substringBetween(str, tag, tag);
    }

    /**
     * <p>Gets the String that is nested in between two Strings.
     * Only the first match is returned.</p>
     * <p>
     * <p>A {@code null} input String returns {@code null}.
     * A {@code null} open/close returns {@code null} (no match).
     * An empty ("") open and close returns an empty string.</p>
     * <p>
     * <pre>
     * StringUtils.substringBetween("wx[b]yz", "[", "]") = "b"
     * StringUtils.substringBetween(null, *, *)          = null
     * StringUtils.substringBetween(*, null, *)          = null
     * StringUtils.substringBetween(*, *, null)          = null
     * StringUtils.substringBetween("", "", "")          = ""
     * StringUtils.substringBetween("", "", "]")         = null
     * StringUtils.substringBetween("", "[", "]")        = null
     * StringUtils.substringBetween("yabcz", "", "")     = ""
     * StringUtils.substringBetween("yabcz", "y", "z")   = "abc"
     * StringUtils.substringBetween("yabczyabcz", "y", "z")   = "abc"
     * </pre>
     *
     * @param str   the String containing the substring, may be null
     * @param open  the String before the substring, may be null
     * @param close the String after the substring, may be null
     * @return the substring, {@code null} if no match
     * @since 2.0
     */
    public static String substringBetween(final String str, final String open, final String close) {
        if (str == null || open == null || close == null) {
            return null;
        }
        final int start = str.indexOf(open);
        if (start != INDEX_NOT_FOUND) {
            final int end = str.indexOf(close, start + open.length());
            if (end != INDEX_NOT_FOUND) {
                return str.substring(start + open.length(), end);
            }
        }
        return null;
    }

    // Delete
    //-----------------------------------------------------------------------

    /**
     * <p>Deletes all whitespaces from a String as defined by
     * {@link Character#isWhitespace(char)}.</p>
     * <p>
     * <pre>
     * StringUtils.deleteWhitespace(null)         = null
     * StringUtils.deleteWhitespace("")           = ""
     * StringUtils.deleteWhitespace("abc")        = "abc"
     * StringUtils.deleteWhitespace("   ab  c  ") = "abc"
     * </pre>
     *
     * @param str the String to delete whitespace from, may be null
     * @return the String without whitespaces, {@code null} if null String input
     */
    public static String deleteWhitespace(final String str) {
        if (isEmpty(str)) {
            return str;
        }
        final int sz = str.length();
        final char[] chs = new char[sz];
        int count = 0;
        for (int i = 0; i < sz; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                chs[count++] = str.charAt(i);
            }
        }
        if (count == sz) {
            return str;
        }
        return new String(chs, 0, count);
    }

    // Remove
    //-----------------------------------------------------------------------

    /**
     * <p>Removes a substring only if it is at the beginning of a source string,
     * otherwise returns the source string.</p>
     * <p>
     * <p>A {@code null} source string will return {@code null}.
     * An empty ("") source string will return the empty string.
     * A {@code null} search string will return the source string.</p>
     * <p>
     * <pre>
     * StringUtils.removeStart(null, *)      = null
     * StringUtils.removeStart("", *)        = ""
     * StringUtils.removeStart(*, null)      = *
     * StringUtils.removeStart("www.domain.com", "www.")   = "domain.com"
     * StringUtils.removeStart("domain.com", "www.")       = "domain.com"
     * StringUtils.removeStart("www.domain.com", "domain") = "www.domain.com"
     * StringUtils.removeStart("abc", "")    = "abc"
     * </pre>
     *
     * @param str    the source String to search, may be null
     * @param remove the String to search for and remove, may be null
     * @return the substring with the string removed if found,
     * {@code null} if null String input
     * @since 2.1
     */
    public static String removeStart(final String str, final String remove) {
        if (isEmpty(str) || isEmpty(remove)) {
            return str;
        }
        if (str.startsWith(remove)) {
            return str.substring(remove.length());
        }
        return str;
    }

    /**
     * <p>Removes all occurrences of a character from within the source string.</p>
     * <p>
     * <p>A {@code null} source string will return {@code null}.
     * An empty ("") source string will return the empty string.</p>
     * <p>
     * <pre>
     * StringUtils.remove(null, *)       = null
     * StringUtils.remove("", *)         = ""
     * StringUtils.remove("queued", 'u') = "qeed"
     * StringUtils.remove("queued", 'z') = "queued"
     * </pre>
     *
     * @param str    the source String to search, may be null
     * @param remove the char to search for and remove, may be null
     * @return the substring with the char removed if found,
     * {@code null} if null String input
     * @since 2.1
     */
    public static String remove(final String str, final char remove) {
        if (isEmpty(str) || str.indexOf(remove) == INDEX_NOT_FOUND) {
            return str;
        }
        final char[] chars = str.toCharArray();
        int pos = 0;
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] != remove) {
                chars[pos++] = chars[i];
            }
        }
        return new String(chars, 0, pos);
    }

    /**
     * <p>Returns padding using the specified delimiter repeated
     * to a given length.</p>
     * <p>
     * <pre>
     * StringUtils.repeat('e', 0)  = ""
     * StringUtils.repeat('e', 3)  = "eee"
     * StringUtils.repeat('e', -2) = ""
     * </pre>
     * <p>
     * <p>Note: this method doesn't not support padding with
     * <a href="http://www.unicode.org/glossary/#supplementary_character">Unicode Supplementary Characters</a>
     * as they require a pair of {@code char}s to be represented.
     * If you are needing to support full I18N of your applications
     * </p>
     *
     * @param ch     character to repeat
     * @param repeat number of times to repeat char, negative treated as zero
     * @return String with repeated character
     */
    public static String repeat(final char ch, final int repeat) {
        final char[] buf = new char[repeat];
        for (int i = repeat - 1; i >= 0; i--) {
            buf[i] = ch;
        }
        return new String(buf);
    }

    /**
     * <p>Right pad a String with spaces (' ').</p>
     * <p>
     * <p>The String is padded to the size of {@code size}.</p>
     * <p>
     * <pre>
     * StringUtils.rightPad(null, *)   = null
     * StringUtils.rightPad("", 3)     = "   "
     * StringUtils.rightPad("bat", 3)  = "bat"
     * StringUtils.rightPad("bat", 5)  = "bat  "
     * StringUtils.rightPad("bat", 1)  = "bat"
     * StringUtils.rightPad("bat", -1) = "bat"
     * </pre>
     *
     * @param str  the String to pad out, may be null
     * @param size the size to pad to
     * @return right padded String or original String if no padding is necessary,
     * {@code null} if null String input
     */
    public static String rightPad(final String str, final int size) {
        return rightPad(str, size, ' ');
    }

    /**
     * <p>Right pad a String with a specified character.</p>
     * <p>
     * <p>The String is padded to the size of {@code size}.</p>
     * <p>
     * <pre>
     * StringUtils.rightPad(null, *, *)     = null
     * StringUtils.rightPad("", 3, 'z')     = "zzz"
     * StringUtils.rightPad("bat", 3, 'z')  = "bat"
     * StringUtils.rightPad("bat", 5, 'z')  = "batzz"
     * StringUtils.rightPad("bat", 1, 'z')  = "bat"
     * StringUtils.rightPad("bat", -1, 'z') = "bat"
     * </pre>
     *
     * @param str     the String to pad out, may be null
     * @param size    the size to pad to
     * @param padChar the character to pad with
     * @return right padded String or original String if no padding is necessary,
     * {@code null} if null String input
     * @since 2.0
     */
    public static String rightPad(final String str, final int size, final char padChar) {
        if (str == null) {
            return null;
        }
        final int pads = size - str.length();
        if (pads <= 0) {
            return str; // returns original String when possible
        }
        if (pads > PAD_LIMIT) {
            return rightPad(str, size, String.valueOf(padChar));
        }
        return str.concat(repeat(padChar, pads));
    }

    /**
     * <p>Right pad a String with a specified String.</p>
     * <p>
     * <p>The String is padded to the size of {@code size}.</p>
     * <p>
     * <pre>
     * StringUtils.rightPad(null, *, *)      = null
     * StringUtils.rightPad("", 3, "z")      = "zzz"
     * StringUtils.rightPad("bat", 3, "yz")  = "bat"
     * StringUtils.rightPad("bat", 5, "yz")  = "batyz"
     * StringUtils.rightPad("bat", 8, "yz")  = "batyzyzy"
     * StringUtils.rightPad("bat", 1, "yz")  = "bat"
     * StringUtils.rightPad("bat", -1, "yz") = "bat"
     * StringUtils.rightPad("bat", 5, null)  = "bat  "
     * StringUtils.rightPad("bat", 5, "")    = "bat  "
     * </pre>
     *
     * @param str    the String to pad out, may be null
     * @param size   the size to pad to
     * @param padStr the String to pad with, null or empty treated as single space
     * @return right padded String or original String if no padding is necessary,
     * {@code null} if null String input
     */
    public static String rightPad(final String str, final int size, String padStr) {
        if (str == null) {
            return null;
        }
        if (isEmpty(padStr)) {
            padStr = SPACE;
        }
        final int padLen = padStr.length();
        final int strLen = str.length();
        final int pads = size - strLen;
        if (pads <= 0) {
            return str; // returns original String when possible
        }
        if (padLen == 1 && pads <= PAD_LIMIT) {
            return rightPad(str, size, padStr.charAt(0));
        }

        if (pads == padLen) {
            return str.concat(padStr);
        } else if (pads < padLen) {
            return str.concat(padStr.substring(0, pads));
        } else {
            final char[] padding = new char[pads];
            final char[] padChars = padStr.toCharArray();
            for (int i = 0; i < pads; i++) {
                padding[i] = padChars[i % padLen];
            }
            return str.concat(new String(padding));
        }
    }

    /**
     * <p>Left pad a String with spaces (' ').</p>
     * <p>
     * <p>The String is padded to the size of {@code size}.</p>
     * <p>
     * <pre>
     * StringUtils.leftPad(null, *)   = null
     * StringUtils.leftPad("", 3)     = "   "
     * StringUtils.leftPad("bat", 3)  = "bat"
     * StringUtils.leftPad("bat", 5)  = "  bat"
     * StringUtils.leftPad("bat", 1)  = "bat"
     * StringUtils.leftPad("bat", -1) = "bat"
     * </pre>
     *
     * @param str  the String to pad out, may be null
     * @param size the size to pad to
     * @return left padded String or original String if no padding is necessary,
     * {@code null} if null String input
     */
    public static String leftPad(final String str, final int size) {
        return leftPad(str, size, ' ');
    }

    /**
     * <p>Left pad a String with a specified character.</p>
     * <p>
     * <p>Pad to a size of {@code size}.</p>
     * <p>
     * <pre>
     * StringUtils.leftPad(null, *, *)     = null
     * StringUtils.leftPad("", 3, 'z')     = "zzz"
     * StringUtils.leftPad("bat", 3, 'z')  = "bat"
     * StringUtils.leftPad("bat", 5, 'z')  = "zzbat"
     * StringUtils.leftPad("bat", 1, 'z')  = "bat"
     * StringUtils.leftPad("bat", -1, 'z') = "bat"
     * </pre>
     *
     * @param str     the String to pad out, may be null
     * @param size    the size to pad to
     * @param padChar the character to pad with
     * @return left padded String or original String if no padding is necessary,
     * {@code null} if null String input
     * @since 2.0
     */
    public static String leftPad(final String str, final int size, final char padChar) {
        if (str == null) {
            return null;
        }
        final int pads = size - str.length();
        if (pads <= 0) {
            return str; // returns original String when possible
        }
        if (pads > PAD_LIMIT) {
            return leftPad(str, size, String.valueOf(padChar));
        }
        return repeat(padChar, pads).concat(str);
    }

    /**
     * <p>Left pad a String with a specified String.</p>
     * <p>
     * <p>Pad to a size of {@code size}.</p>
     * <p>
     * <pre>
     * StringUtils.leftPad(null, *, *)      = null
     * StringUtils.leftPad("", 3, "z")      = "zzz"
     * StringUtils.leftPad("bat", 3, "yz")  = "bat"
     * StringUtils.leftPad("bat", 5, "yz")  = "yzbat"
     * StringUtils.leftPad("bat", 8, "yz")  = "yzyzybat"
     * StringUtils.leftPad("bat", 1, "yz")  = "bat"
     * StringUtils.leftPad("bat", -1, "yz") = "bat"
     * StringUtils.leftPad("bat", 5, null)  = "  bat"
     * StringUtils.leftPad("bat", 5, "")    = "  bat"
     * </pre>
     *
     * @param str    the String to pad out, may be null
     * @param size   the size to pad to
     * @param padStr the String to pad with, null or empty treated as single space
     * @return left padded String or original String if no padding is necessary,
     * {@code null} if null String input
     */
    public static String leftPad(final String str, final int size, String padStr) {
        if (str == null) {
            return null;
        }
        if (isEmpty(padStr)) {
            padStr = SPACE;
        }
        final int padLen = padStr.length();
        final int strLen = str.length();
        final int pads = size - strLen;
        if (pads <= 0) {
            return str; // returns original String when possible
        }
        if (padLen == 1 && pads <= PAD_LIMIT) {
            return leftPad(str, size, padStr.charAt(0));
        }

        if (pads == padLen) {
            return padStr.concat(str);
        } else if (pads < padLen) {
            return padStr.substring(0, pads).concat(str);
        } else {
            final char[] padding = new char[pads];
            final char[] padChars = padStr.toCharArray();
            for (int i = 0; i < pads; i++) {
                padding[i] = padChars[i % padLen];
            }
            return new String(padding).concat(str);
        }
    }

    /**
     * Gets a CharSequence length or {@code 0} if the CharSequence is
     * {@code null}.
     *
     * @param cs a CharSequence or {@code null}
     * @return CharSequence length or {@code 0} if the CharSequence is
     * {@code null}.
     * @since 3.0 Changed signature from length(String) to length(CharSequence)
     */
    public static int length(final CharSequence cs) {
        return cs == null ? 0 : cs.length();
    }

    // Centering
    //-----------------------------------------------------------------------

    /**
     * <p>Centers a String in a larger String of size {@code size}
     * using the space character (' ').</p>
     * <p>
     * <p>If the size is less than the String length, the String is returned.
     * A {@code null} String returns {@code null}.
     * A negative size is treated as zero.</p>
     * <p>
     * <p>Equivalent to {@code center(str, size, " ")}.</p>
     * <p>
     * <pre>
     * StringUtils.center(null, *)   = null
     * StringUtils.center("", 4)     = "    "
     * StringUtils.center("ab", -1)  = "ab"
     * StringUtils.center("ab", 4)   = " ab "
     * StringUtils.center("abcd", 2) = "abcd"
     * StringUtils.center("a", 4)    = " a  "
     * </pre>
     *
     * @param str  the String to center, may be null
     * @param size the int size of new String, negative treated as zero
     * @return centered String, {@code null} if null String input
     */
    public static String center(final String str, final int size) {
        return center(str, size, ' ');
    }

    /**
     * <p>Centers a String in a larger String of size {@code size}.
     * Uses a supplied character as the value to pad the String with.</p>
     * <p>
     * <p>If the size is less than the String length, the String is returned.
     * A {@code null} String returns {@code null}.
     * A negative size is treated as zero.</p>
     * <p>
     * <pre>
     * StringUtils.center(null, *, *)     = null
     * StringUtils.center("", 4, ' ')     = "    "
     * StringUtils.center("ab", -1, ' ')  = "ab"
     * StringUtils.center("ab", 4, ' ')   = " ab "
     * StringUtils.center("abcd", 2, ' ') = "abcd"
     * StringUtils.center("a", 4, ' ')    = " a  "
     * StringUtils.center("a", 4, 'y')    = "yayy"
     * </pre>
     *
     * @param str     the String to center, may be null
     * @param size    the int size of new String, negative treated as zero
     * @param padChar the character to pad the new String with
     * @return centered String, {@code null} if null String input
     * @since 2.0
     */
    public static String center(String str, final int size, final char padChar) {
        if (str == null || size <= 0) {
            return str;
        }
        final int strLen = str.length();
        final int pads = size - strLen;
        if (pads <= 0) {
            return str;
        }
        str = leftPad(str, strLen + pads / 2, padChar);
        str = rightPad(str, size, padChar);
        return str;
    }

    /**
     * <p>Centers a String in a larger String of size {@code size}.
     * Uses a supplied String as the value to pad the String with.</p>
     * <p>
     * <p>If the size is less than the String length, the String is returned.
     * A {@code null} String returns {@code null}.
     * A negative size is treated as zero.</p>
     * <p>
     * <pre>
     * StringUtils.center(null, *, *)     = null
     * StringUtils.center("", 4, " ")     = "    "
     * StringUtils.center("ab", -1, " ")  = "ab"
     * StringUtils.center("ab", 4, " ")   = " ab "
     * StringUtils.center("abcd", 2, " ") = "abcd"
     * StringUtils.center("a", 4, " ")    = " a  "
     * StringUtils.center("a", 4, "yz")   = "yayz"
     * StringUtils.center("abc", 7, null) = "  abc  "
     * StringUtils.center("abc", 7, "")   = "  abc  "
     * </pre>
     *
     * @param str    the String to center, may be null
     * @param size   the int size of new String, negative treated as zero
     * @param padStr the String to pad the new String with, must not be null or empty
     * @return centered String, {@code null} if null String input
     * @throws IllegalArgumentException if padStr is {@code null} or empty
     */
    public static String center(String str, final int size, String padStr) {
        if (str == null || size <= 0) {
            return str;
        }
        if (isEmpty(padStr)) {
            padStr = SPACE;
        }
        final int strLen = str.length();
        final int pads = size - strLen;
        if (pads <= 0) {
            return str;
        }
        str = leftPad(str, strLen + pads / 2, padStr);
        str = rightPad(str, size, padStr);
        return str;
    }

    // Case conversion
    //-----------------------------------------------------------------------

    /**
     * <p>Converts a String to upper case as per {@link String#toUpperCase()}.</p>
     * <p>
     * <p>A {@code null} input String returns {@code null}.</p>
     * <p>
     * <pre>
     * StringUtils.upperCase(null)  = null
     * StringUtils.upperCase("")    = ""
     * StringUtils.upperCase("aBc") = "ABC"
     * </pre>
     * <p>
     * <p><strong>Note:</strong> As described in the documentation for {@link String#toUpperCase()},
     * the result of this method is affected by the current locale.
     * For platform-independent case transformations, the method {@link #lowerCase(String, Locale)}
     * should be used with a specific locale (e.g. {@link Locale#ENGLISH}).</p>
     *
     * @param str the String to upper case, may be null
     * @return the upper cased String, {@code null} if null String input
     */
    public static String upperCase(final String str) {
        if (str == null) {
            return null;
        }
        return str.toUpperCase();
    }

    /**
     * <p>Converts a String to upper case as per {@link String#toUpperCase(Locale)}.</p>
     * <p>
     * <p>A {@code null} input String returns {@code null}.</p>
     * <p>
     * <pre>
     * StringUtils.upperCase(null, Locale.ENGLISH)  = null
     * StringUtils.upperCase("", Locale.ENGLISH)    = ""
     * StringUtils.upperCase("aBc", Locale.ENGLISH) = "ABC"
     * </pre>
     *
     * @param str    the String to upper case, may be null
     * @param locale the locale that defines the case transformation rules, must not be null
     * @return the upper cased String, {@code null} if null String input
     * @since 2.5
     */
    public static String upperCase(final String str, final Locale locale) {
        if (str == null) {
            return null;
        }
        return str.toUpperCase(locale);
    }

    /**
     * <p>Converts a String to lower case as per {@link String#toLowerCase()}.</p>
     * <p>
     * <p>A {@code null} input String returns {@code null}.</p>
     * <p>
     * <pre>
     * StringUtils.lowerCase(null)  = null
     * StringUtils.lowerCase("")    = ""
     * StringUtils.lowerCase("aBc") = "abc"
     * </pre>
     * <p>
     * <p><strong>Note:</strong> As described in the documentation for {@link String#toLowerCase()},
     * the result of this method is affected by the current locale.
     * For platform-independent case transformations, the method {@link #lowerCase(String, Locale)}
     * should be used with a specific locale (e.g. {@link Locale#ENGLISH}).</p>
     *
     * @param str the String to lower case, may be null
     * @return the lower cased String, {@code null} if null String input
     */
    public static String lowerCase(final String str) {
        if (str == null) {
            return null;
        }
        return str.toLowerCase();
    }

    /**
     * <p>Converts a String to lower case as per {@link String#toLowerCase(Locale)}.</p>
     * <p>
     * <p>A {@code null} input String returns {@code null}.</p>
     * <p>
     * <pre>
     * StringUtils.lowerCase(null, Locale.ENGLISH)  = null
     * StringUtils.lowerCase("", Locale.ENGLISH)    = ""
     * StringUtils.lowerCase("aBc", Locale.ENGLISH) = "abc"
     * </pre>
     *
     * @param str    the String to lower case, may be null
     * @param locale the locale that defines the case transformation rules, must not be null
     * @return the lower cased String, {@code null} if null String input
     * @since 2.5
     */
    public static String lowerCase(final String str, final Locale locale) {
        if (str == null) {
            return null;
        }
        return str.toLowerCase(locale);
    }

    /**
     * <p>Capitalizes a String changing the first letter to title case as
     * per {@link Character#toTitleCase(char)}. No other letters are changed.</p>
     * <p>
     * <p>
     * <pre>
     * StringUtils.capitalize(null)  = null
     * StringUtils.capitalize("")    = ""
     * StringUtils.capitalize("cat") = "Cat"
     * StringUtils.capitalize("cAt") = "CAt"
     * </pre>
     *
     * @param str the String to capitalize, may be null
     * @return the capitalized String, {@code null} if null String input
     * @see #uncapitalize(String)
     * @since 2.0
     */
    public static String capitalize(final String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return str;
        }

        char firstChar = str.charAt(0);
        if (Character.isTitleCase(firstChar)) {
            // already capitalized
            return str;
        }

        return new StringBuilder(strLen)
                .append(Character.toTitleCase(firstChar))
                .append(str.substring(1))
                .toString();
    }

    /**
     * <p>Uncapitalizes a String changing the first letter to title case as
     * per {@link Character#toLowerCase(char)}. No other letters are changed.</p>
     * <p>
     * <pre>
     * StringUtils.uncapitalize(null)  = null
     * StringUtils.uncapitalize("")    = ""
     * StringUtils.uncapitalize("Cat") = "cat"
     * StringUtils.uncapitalize("CAT") = "cAT"
     * </pre>
     *
     * @param str the String to uncapitalize, may be null
     * @return the uncapitalized String, {@code null} if null String input
     * @see #capitalize(String)
     * @since 2.0
     */
    public static String uncapitalize(final String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return str;
        }

        char firstChar = str.charAt(0);
        if (Character.isLowerCase(firstChar)) {
            // already uncapitalized
            return str;
        }

        return new StringBuilder(strLen)
                .append(Character.toLowerCase(firstChar))
                .append(str.substring(1))
                .toString();
    }

    /**
     * <p>Swaps the case of a String changing upper and title case to
     * lower case, and lower case to upper case.</p>
     * <p>
     * <ul>
     * <li>Upper case character converts to Lower case</li>
     * <li>Title case character converts to Lower case</li>
     * <li>Lower case character converts to Upper case</li>
     * </ul>
     * <p>
     * <pre>
     * StringUtils.swapCase(null)                 = null
     * StringUtils.swapCase("")                   = ""
     * StringUtils.swapCase("The dog has a BONE") = "tHE DOG HAS A bone"
     * </pre>
     * <p>
     * <p>NOTE: This method changed in Lang version 2.0.
     * It no longer performs a word based algorithm.
     * If you only use ASCII, you will notice no change.
     * That functionality is available in org.apache.commons.lang3.text.WordUtils.</p>
     *
     * @param str the String to swap case, may be null
     * @return the changed String, {@code null} if null String input
     */
    public static String swapCase(final String str) {
        if (StringUtils.isEmpty(str)) {
            return str;
        }

        final char[] buffer = str.toCharArray();

        for (int i = 0; i < buffer.length; i++) {
            final char ch = buffer[i];
            if (Character.isUpperCase(ch)) {
                buffer[i] = Character.toLowerCase(ch);
            } else if (Character.isTitleCase(ch)) {
                buffer[i] = Character.toLowerCase(ch);
            } else if (Character.isLowerCase(ch)) {
                buffer[i] = Character.toUpperCase(ch);
            }
        }
        return new String(buffer);
    }

    // Character Tests
    //-----------------------------------------------------------------------

    /**
     * <p>Checks if the CharSequence contains only Unicode letters.</p>
     * <p>
     * <p>{@code null} will return {@code false}.
     * An empty CharSequence (length()=0) will return {@code false}.</p>
     * <p>
     * <pre>
     * StringUtils.isAlpha(null)   = false
     * StringUtils.isAlpha("")     = false
     * StringUtils.isAlpha("  ")   = false
     * StringUtils.isAlpha("abc")  = true
     * StringUtils.isAlpha("ab2c") = false
     * StringUtils.isAlpha("ab-c") = false
     * </pre>
     *
     * @param cs the CharSequence to check, may be null
     * @return {@code true} if only contains letters, and is non-null
     * @since 3.0 Changed "" to return false and not true
     */
    public static boolean isAlpha(final CharSequence cs) {
        if (isEmpty(cs)) {
            return false;
        }
        final int sz = cs.length();
        for (int i = 0; i < sz; i++) {
            if (Character.isLetter(cs.charAt(i)) == false) {
                return false;
            }
        }
        return true;
    }

    /**
     * <p>Checks if the CharSequence contains only Unicode letters and
     * space (' ').</p>
     * <p>
     * <p>{@code null} will return {@code false}
     * An empty CharSequence (length()=0) will return {@code true}.</p>
     * <p>
     * <pre>
     * StringUtils.isAlphaSpace(null)   = false
     * StringUtils.isAlphaSpace("")     = true
     * StringUtils.isAlphaSpace("  ")   = true
     * StringUtils.isAlphaSpace("abc")  = true
     * StringUtils.isAlphaSpace("ab c") = true
     * StringUtils.isAlphaSpace("ab2c") = false
     * StringUtils.isAlphaSpace("ab-c") = false
     * </pre>
     *
     * @param cs the CharSequence to check, may be null
     * @return {@code true} if only contains letters and space,
     * and is non-null
     * @since 3.0 Changed signature from isAlphaSpace(String) to isAlphaSpace(CharSequence)
     */
    public static boolean isAlphaSpace(final CharSequence cs) {
        if (cs == null) {
            return false;
        }
        final int sz = cs.length();
        for (int i = 0; i < sz; i++) {
            if (Character.isLetter(cs.charAt(i)) == false && cs.charAt(i) != ' ') {
                return false;
            }
        }
        return true;
    }

    /**
     * <p>Checks if the CharSequence contains only Unicode letters or digits.</p>
     * <p>
     * <p>{@code null} will return {@code false}.
     * An empty CharSequence (length()=0) will return {@code false}.</p>
     * <p>
     * <pre>
     * StringUtils.isAlphanumeric(null)   = false
     * StringUtils.isAlphanumeric("")     = false
     * StringUtils.isAlphanumeric("  ")   = false
     * StringUtils.isAlphanumeric("abc")  = true
     * StringUtils.isAlphanumeric("ab c") = false
     * StringUtils.isAlphanumeric("ab2c") = true
     * StringUtils.isAlphanumeric("ab-c") = false
     * </pre>
     *
     * @param cs the CharSequence to check, may be null
     * @return {@code true} if only contains letters or digits,
     * and is non-null
     * @since 3.0 Changed "" to return false and not true
     */
    public static boolean isAlphanumeric(final CharSequence cs) {
        if (isEmpty(cs)) {
            return false;
        }
        final int sz = cs.length();
        for (int i = 0; i < sz; i++) {
            if (Character.isLetterOrDigit(cs.charAt(i)) == false) {
                return false;
            }
        }
        return true;
    }

    /**
     * <p>Checks if the CharSequence contains only Unicode letters, digits
     * or space ({@code ' '}).</p>
     * <p>
     * <p>{@code null} will return {@code false}.
     * An empty CharSequence (length()=0) will return {@code true}.</p>
     * <p>
     * <pre>
     * StringUtils.isAlphanumericSpace(null)   = false
     * StringUtils.isAlphanumericSpace("")     = true
     * StringUtils.isAlphanumericSpace("  ")   = true
     * StringUtils.isAlphanumericSpace("abc")  = true
     * StringUtils.isAlphanumericSpace("ab c") = true
     * StringUtils.isAlphanumericSpace("ab2c") = true
     * StringUtils.isAlphanumericSpace("ab-c") = false
     * </pre>
     *
     * @param cs the CharSequence to check, may be null
     * @return {@code true} if only contains letters, digits or space,
     * and is non-null
     * @since 3.0 Changed signature from isAlphanumericSpace(String) to isAlphanumericSpace(CharSequence)
     */
    public static boolean isAlphanumericSpace(final CharSequence cs) {
        if (cs == null) {
            return false;
        }
        final int sz = cs.length();
        for (int i = 0; i < sz; i++) {
            if (Character.isLetterOrDigit(cs.charAt(i)) == false && cs.charAt(i) != ' ') {
                return false;
            }
        }
        return true;
    }

    /**
     * <p>Checks if the CharSequence contains only Unicode digits.
     * A decimal point is not a Unicode digit and returns false.</p>
     * <p>
     * <p>{@code null} will return {@code false}.
     * An empty CharSequence (length()=0) will return {@code false}.</p>
     * <p>
     * <p>Note that the method does not allow for a leading sign, either positive or negative.
     * Also, if a String passes the numeric test, it may still generate a NumberFormatException
     * when parsed by Integer.parseInt or Long.parseLong, e.g. if the value is outside the range
     * for int or long respectively.</p>
     * <p>
     * <pre>
     * StringUtils.isNumeric(null)   = false
     * StringUtils.isNumeric("")     = false
     * StringUtils.isNumeric("  ")   = false
     * StringUtils.isNumeric("123")  = true
     * StringUtils.isNumeric("12 3") = false
     * StringUtils.isNumeric("ab2c") = false
     * StringUtils.isNumeric("12-3") = false
     * StringUtils.isNumeric("12.3") = false
     * StringUtils.isNumeric("-123") = false
     * StringUtils.isNumeric("+123") = false
     * </pre>
     *
     * @param cs the CharSequence to check, may be null
     * @return {@code true} if only contains digits, and is non-null
     * @since 3.0 Changed "" to return false and not true
     */
    public static boolean isNumeric(final CharSequence cs) {
        if (isEmpty(cs)) {
            return false;
        }
        final int sz = cs.length();
        for (int i = 0; i < sz; i++) {
            if (Character.isDigit(cs.charAt(i)) == false) {
                return false;
            }
        }
        return true;
    }

    /**
     * <p>Checks if the CharSequence contains only Unicode digits or space
     * ({@code ' '}).
     * A decimal point is not a Unicode digit and returns false.</p>
     * <p>
     * <p>{@code null} will return {@code false}.
     * An empty CharSequence (length()=0) will return {@code true}.</p>
     * <p>
     * <pre>
     * StringUtils.isNumericSpace(null)   = false
     * StringUtils.isNumericSpace("")     = true
     * StringUtils.isNumericSpace("  ")   = true
     * StringUtils.isNumericSpace("123")  = true
     * StringUtils.isNumericSpace("12 3") = true
     * StringUtils.isNumericSpace("ab2c") = false
     * StringUtils.isNumericSpace("12-3") = false
     * StringUtils.isNumericSpace("12.3") = false
     * </pre>
     *
     * @param cs the CharSequence to check, may be null
     * @return {@code true} if only contains digits or space,
     * and is non-null
     * @since 3.0 Changed signature from isNumericSpace(String) to isNumericSpace(CharSequence)
     */
    public static boolean isNumericSpace(final CharSequence cs) {
        if (cs == null) {
            return false;
        }
        final int sz = cs.length();
        for (int i = 0; i < sz; i++) {
            if (Character.isDigit(cs.charAt(i)) == false && cs.charAt(i) != ' ') {
                return false;
            }
        }
        return true;
    }

    /**
     * <p>Checks if the CharSequence contains only whitespace.</p>
     * <p>
     * <p>{@code null} will return {@code false}.
     * An empty CharSequence (length()=0) will return {@code true}.</p>
     * <p>
     * <pre>
     * StringUtils.isWhitespace(null)   = false
     * StringUtils.isWhitespace("")     = true
     * StringUtils.isWhitespace("  ")   = true
     * StringUtils.isWhitespace("abc")  = false
     * StringUtils.isWhitespace("ab2c") = false
     * StringUtils.isWhitespace("ab-c") = false
     * </pre>
     *
     * @param cs the CharSequence to check, may be null
     * @return {@code true} if only contains whitespace, and is non-null
     * @since 3.0 Changed signature from isWhitespace(String) to isWhitespace(CharSequence)
     */
    public static boolean isWhitespace(final CharSequence cs) {
        if (cs == null) {
            return false;
        }
        final int sz = cs.length();
        for (int i = 0; i < sz; i++) {
            if (Character.isWhitespace(cs.charAt(i)) == false) {
                return false;
            }
        }
        return true;
    }

    /**
     * <p>Checks if the CharSequence contains only lowercase characters.</p>
     * <p>
     * <p>{@code null} will return {@code false}.
     * An empty CharSequence (length()=0) will return {@code false}.</p>
     * <p>
     * <pre>
     * StringUtils.isAllLowerCase(null)   = false
     * StringUtils.isAllLowerCase("")     = false
     * StringUtils.isAllLowerCase("  ")   = false
     * StringUtils.isAllLowerCase("abc")  = true
     * StringUtils.isAllLowerCase("abC") = false
     * </pre>
     *
     * @param cs the CharSequence to check, may be null
     * @return {@code true} if only contains lowercase characters, and is non-null
     * @since 3.0 Changed signature from isAllLowerCase(String) to isAllLowerCase(CharSequence)
     */
    public static boolean isAllLowerCase(final CharSequence cs) {
        if (cs == null || isEmpty(cs)) {
            return false;
        }
        final int sz = cs.length();
        for (int i = 0; i < sz; i++) {
            if (Character.isLowerCase(cs.charAt(i)) == false) {
                return false;
            }
        }
        return true;
    }

    /**
     * <p>Checks if the CharSequence contains only uppercase characters.</p>
     * <p>
     * <p>{@code null} will return {@code false}.
     * An empty String (length()=0) will return {@code false}.</p>
     * <p>
     * <pre>
     * StringUtils.isAllUpperCase(null)   = false
     * StringUtils.isAllUpperCase("")     = false
     * StringUtils.isAllUpperCase("  ")   = false
     * StringUtils.isAllUpperCase("ABC")  = true
     * StringUtils.isAllUpperCase("aBC") = false
     * </pre>
     *
     * @param cs the CharSequence to check, may be null
     * @return {@code true} if only contains uppercase characters, and is non-null
     * @since 3.0 Changed signature from isAllUpperCase(String) to isAllUpperCase(CharSequence)
     */
    public static boolean isAllUpperCase(final CharSequence cs) {
        if (cs == null || isEmpty(cs)) {
            return false;
        }
        final int sz = cs.length();
        for (int i = 0; i < sz; i++) {
            if (Character.isUpperCase(cs.charAt(i)) == false) {
                return false;
            }
        }
        return true;
    }

    // Defaults
    //-----------------------------------------------------------------------

    /**
     * <p>Returns either the passed in String,
     * or if the String is {@code null}, an empty String ("").</p>
     * <p>
     * <pre>
     * StringUtils.defaultString(null)  = ""
     * StringUtils.defaultString("")    = ""
     * StringUtils.defaultString("bat") = "bat"
     * </pre>
     *
     * @param str the String to check, may be null
     * @return the passed in String, or the empty String if it
     * was {@code null}
     * @see String#valueOf(Object)
     */
    public static String defaultString(final String str) {
        return str == null ? EMPTY : str;
    }

    /**
     * <p>Returns either the passed in String, or if the String is
     * {@code null}, the value of {@code defaultStr}.</p>
     * <p>
     * <pre>
     * StringUtils.defaultString(null, "NULL")  = "NULL"
     * StringUtils.defaultString("", "NULL")    = ""
     * StringUtils.defaultString("bat", "NULL") = "bat"
     * </pre>
     *
     * @param str        the String to check, may be null
     * @param defaultStr the default String to return
     *                   if the input is {@code null}, may be null
     * @return the passed in String, or the default if it was {@code null}
     * @see String#valueOf(Object)
     */
    public static String defaultString(final String str, final String defaultStr) {
        return str == null ? defaultStr : str;
    }

    /**
     * <p>Returns either the passed in CharSequence, or if the CharSequence is
     * whitespace, empty ("") or {@code null}, the value of {@code defaultStr}.</p>
     * <p>
     * <pre>
     * StringUtils.defaultIfBlank(null, "NULL")  = "NULL"
     * StringUtils.defaultIfBlank("", "NULL")    = "NULL"
     * StringUtils.defaultIfBlank(" ", "NULL")   = "NULL"
     * StringUtils.defaultIfBlank("bat", "NULL") = "bat"
     * StringUtils.defaultIfBlank("", null)      = null
     * </pre>
     *
     * @param <T>        the specific kind of CharSequence
     * @param str        the CharSequence to check, may be null
     * @param defaultStr the default CharSequence to return
     *                   if the input is whitespace, empty ("") or {@code null}, may be null
     * @return the passed in CharSequence, or the default
     * @see StringUtils#defaultString(String, String)
     */
    public static <T extends CharSequence> T defaultIfBlank(final T str, final T defaultStr) {
        return StringUtils.isBlank(str) ? defaultStr : str;
    }

    /**
     * <p>Returns either the passed in CharSequence, or if the CharSequence is
     * empty or {@code null}, the value of {@code defaultStr}.</p>
     * <p>
     * <pre>
     * StringUtils.defaultIfEmpty(null, "NULL")  = "NULL"
     * StringUtils.defaultIfEmpty("", "NULL")    = "NULL"
     * StringUtils.defaultIfEmpty(" ", "NULL")   = " "
     * StringUtils.defaultIfEmpty("bat", "NULL") = "bat"
     * StringUtils.defaultIfEmpty("", null)      = null
     * </pre>
     *
     * @param <T>        the specific kind of CharSequence
     * @param str        the CharSequence to check, may be null
     * @param defaultStr the default CharSequence to return
     *                   if the input is empty ("") or {@code null}, may be null
     * @return the passed in CharSequence, or the default
     * @see StringUtils#defaultString(String, String)
     */
    public static <T extends CharSequence> T defaultIfEmpty(final T str, final T defaultStr) {
        return StringUtils.isEmpty(str) ? defaultStr : str;
    }

    // Reversing
    //-----------------------------------------------------------------------

    /**
     * <p>Reverses a String as per {@link StringBuilder#reverse()}.</p>
     * <p>
     * <p>A {@code null} String returns {@code null}.</p>
     * <p>
     * <pre>
     * StringUtils.reverse(null)  = null
     * StringUtils.reverse("")    = ""
     * StringUtils.reverse("bat") = "tab"
     * </pre>
     *
     * @param str the String to reverse, may be null
     * @return the reversed String, {@code null} if null String input
     */
    public static String reverse(final String str) {
        if (str == null) {
            return null;
        }
        return new StringBuilder(str).reverse().toString();
    }

    // Abbreviating
    //-----------------------------------------------------------------------

    /**
     * <p>Abbreviates a String using ellipses. This will turn
     * "Now is the time for all good men" into "Now is the time for..."</p>
     * <p>
     * <p>Specifically:</p>
     * <ul>
     * <li>If {@code str} is less than {@code maxWidth} characters
     * long, return it.</li>
     * <li>Else abbreviate it to {@code (substring(str, 0, max-3) + "...")}.</li>
     * <li>If {@code maxWidth} is less than {@code 4}, throw an
     * {@code IllegalArgumentException}.</li>
     * <li>In no case will it return a String of length greater than
     * {@code maxWidth}.</li>
     * </ul>
     * <p>
     * <pre>
     * StringUtils.abbreviate(null, *)      = null
     * StringUtils.abbreviate("", 4)        = ""
     * StringUtils.abbreviate("abcdefg", 6) = "abc..."
     * StringUtils.abbreviate("abcdefg", 7) = "abcdefg"
     * StringUtils.abbreviate("abcdefg", 8) = "abcdefg"
     * StringUtils.abbreviate("abcdefg", 4) = "a..."
     * StringUtils.abbreviate("abcdefg", 3) = IllegalArgumentException
     * </pre>
     *
     * @param str      the String to check, may be null
     * @param maxWidth maximum length of result String, must be at least 4
     * @return abbreviated String, {@code null} if null String input
     * @throws IllegalArgumentException if the width is too small
     * @since 2.0
     */
    public static String abbreviate(final String str, final int maxWidth) {
        return abbreviate(str, 0, maxWidth);
    }

    /**
     * <p>Abbreviates a String using ellipses. This will turn
     * "Now is the time for all good men" into "...is the time for..."</p>
     * <p>
     * <p>Works like {@code abbreviate(String, int)}, but allows you to specify
     * a "left edge" offset.  Note that this left edge is not necessarily going to
     * be the leftmost character in the result, or the first character following the
     * ellipses, but it will appear somewhere in the result.
     * <p>
     * <p>In no case will it return a String of length greater than
     * {@code maxWidth}.</p>
     * <p>
     * <pre>
     * StringUtils.abbreviate(null, *, *)                = null
     * StringUtils.abbreviate("", 0, 4)                  = ""
     * StringUtils.abbreviate("abcdefghijklmno", -1, 10) = "abcdefg..."
     * StringUtils.abbreviate("abcdefghijklmno", 0, 10)  = "abcdefg..."
     * StringUtils.abbreviate("abcdefghijklmno", 1, 10)  = "abcdefg..."
     * StringUtils.abbreviate("abcdefghijklmno", 4, 10)  = "abcdefg..."
     * StringUtils.abbreviate("abcdefghijklmno", 5, 10)  = "...fghi..."
     * StringUtils.abbreviate("abcdefghijklmno", 6, 10)  = "...ghij..."
     * StringUtils.abbreviate("abcdefghijklmno", 8, 10)  = "...ijklmno"
     * StringUtils.abbreviate("abcdefghijklmno", 10, 10) = "...ijklmno"
     * StringUtils.abbreviate("abcdefghijklmno", 12, 10) = "...ijklmno"
     * StringUtils.abbreviate("abcdefghij", 0, 3)        = IllegalArgumentException
     * StringUtils.abbreviate("abcdefghij", 5, 6)        = IllegalArgumentException
     * </pre>
     *
     * @param str      the String to check, may be null
     * @param offset   left edge of source String
     * @param maxWidth maximum length of result String, must be at least 4
     * @return abbreviated String, {@code null} if null String input
     * @throws IllegalArgumentException if the width is too small
     * @since 2.0
     */
    public static String abbreviate(final String str, int offset, final int maxWidth) {
        if (str == null) {
            return null;
        }
        if (maxWidth < 4) {
            throw new IllegalArgumentException("Minimum abbreviation width is 4");
        }
        if (str.length() <= maxWidth) {
            return str;
        }
        if (offset > str.length()) {
            offset = str.length();
        }
        if (str.length() - offset < maxWidth - 3) {
            offset = str.length() - (maxWidth - 3);
        }
        final String abrevMarker = "...";
        if (offset <= 4) {
            return str.substring(0, maxWidth - 3) + abrevMarker;
        }
        if (maxWidth < 7) {
            throw new IllegalArgumentException("Minimum abbreviation width with offset is 7");
        }
        if (offset + maxWidth - 3 < str.length()) {
            return abrevMarker + abbreviate(str.substring(offset), maxWidth - 3);
        }
        return abrevMarker + str.substring(str.length() - (maxWidth - 3));
    }

    /**
     * <p>Abbreviates a String to the length passed, replacing the middle characters with the supplied
     * replacement String.</p>
     * <p>
     * <p>This abbreviation only occurs if the following criteria is met:</p>
     * <ul>
     * <li>Neither the String for abbreviation nor the replacement String are null or empty </li>
     * <li>The length to truncate to is less than the length of the supplied String</li>
     * <li>The length to truncate to is greater than 0</li>
     * <li>The abbreviated String will have enough room for the length supplied replacement String
     * and the first and last characters of the supplied String for abbreviation</li>
     * </ul>
     * <p>Otherwise, the returned String will be the same as the supplied String for abbreviation.
     * </p>
     * <p>
     * <pre>
     * StringUtils.abbreviateMiddle(null, null, 0)      = null
     * StringUtils.abbreviateMiddle("abc", null, 0)      = "abc"
     * StringUtils.abbreviateMiddle("abc", ".", 0)      = "abc"
     * StringUtils.abbreviateMiddle("abc", ".", 3)      = "abc"
     * StringUtils.abbreviateMiddle("abcdef", ".", 4)     = "ab.f"
     * </pre>
     *
     * @param str    the String to abbreviate, may be null
     * @param middle the String to replace the middle characters with, may be null
     * @param length the length to abbreviate {@code str} to.
     * @return the abbreviated String if the above criteria is met, or the original String supplied for abbreviation.
     * @since 2.5
     */
    public static String abbreviateMiddle(final String str, final String middle, final int length) {
        if (isEmpty(str) || isEmpty(middle)) {
            return str;
        }

        if (length >= str.length() || length < middle.length() + 2) {
            return str;
        }

        final int targetSting = length - middle.length();
        final int startOffset = targetSting / 2 + targetSting % 2;
        final int endOffset = str.length() - targetSting / 2;

        final StringBuilder builder = new StringBuilder(length);
        builder.append(str.substring(0, startOffset));
        builder.append(middle);
        builder.append(str.substring(endOffset));

        return builder.toString();
    }

    // Difference
    //-----------------------------------------------------------------------

    /**
     * <p>Compares two Strings, and returns the portion where they differ.
     * More precisely, return the remainder of the second String,
     * starting from where it's different from the first. This means that
     * the difference between "abc" and "ab" is the empty String and not "c". </p>
     * <p>
     * <p>For example,
     * {@code difference("i am a machine", "i am a robot") -> "robot"}.</p>
     * <p>
     * <pre>
     * StringUtils.difference(null, null) = null
     * StringUtils.difference("", "") = ""
     * StringUtils.difference("", "abc") = "abc"
     * StringUtils.difference("abc", "") = ""
     * StringUtils.difference("abc", "abc") = ""
     * StringUtils.difference("abc", "ab") = ""
     * StringUtils.difference("ab", "abxyz") = "xyz"
     * StringUtils.difference("abcde", "abxyz") = "xyz"
     * StringUtils.difference("abcde", "xyz") = "xyz"
     * </pre>
     *
     * @param str1 the first String, may be null
     * @param str2 the second String, may be null
     * @return the portion of str2 where it differs from str1; returns the
     * empty String if they are equal
     * @see #indexOfDifference(CharSequence, CharSequence)
     * @since 2.0
     */
    public static String difference(final String str1, final String str2) {
        if (str1 == null) {
            return str2;
        }
        if (str2 == null) {
            return str1;
        }
        final int at = indexOfDifference(str1, str2);
        if (at == INDEX_NOT_FOUND) {
            return EMPTY;
        }
        return str2.substring(at);
    }

    /**
     * <p>Compares two CharSequences, and returns the index at which the
     * CharSequences begin to differ.</p>
     * <p>
     * <p>For example,
     * {@code indexOfDifference("i am a machine", "i am a robot") -> 7}</p>
     * <p>
     * <pre>
     * StringUtils.indexOfDifference(null, null) = -1
     * StringUtils.indexOfDifference("", "") = -1
     * StringUtils.indexOfDifference("", "abc") = 0
     * StringUtils.indexOfDifference("abc", "") = 0
     * StringUtils.indexOfDifference("abc", "abc") = -1
     * StringUtils.indexOfDifference("ab", "abxyz") = 2
     * StringUtils.indexOfDifference("abcde", "abxyz") = 2
     * StringUtils.indexOfDifference("abcde", "xyz") = 0
     * </pre>
     *
     * @param cs1 the first CharSequence, may be null
     * @param cs2 the second CharSequence, may be null
     * @return the index where cs1 and cs2 begin to differ; -1 if they are equal
     * @since 3.0 Changed signature from indexOfDifference(String, String) to
     * indexOfDifference(CharSequence, CharSequence)
     */
    public static int indexOfDifference(final CharSequence cs1, final CharSequence cs2) {
        if (cs1 == cs2) {
            return INDEX_NOT_FOUND;
        }
        if (cs1 == null || cs2 == null) {
            return 0;
        }
        int i;
        for (i = 0; i < cs1.length() && i < cs2.length(); ++i) {
            if (cs1.charAt(i) != cs2.charAt(i)) {
                break;
            }
        }
        if (i < cs2.length() || i < cs1.length()) {
            return i;
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * <p>Compares all CharSequences in an array and returns the index at which the
     * CharSequences begin to differ.</p>
     * <p>
     * <p>For example,
     * <code>indexOfDifference(new String[] {"i am a machine", "i am a robot"}) -&gt; 7</code></p>
     * <p>
     * <pre>
     * StringUtils.indexOfDifference(null) = -1
     * StringUtils.indexOfDifference(new String[] {}) = -1
     * StringUtils.indexOfDifference(new String[] {"abc"}) = -1
     * StringUtils.indexOfDifference(new String[] {null, null}) = -1
     * StringUtils.indexOfDifference(new String[] {"", ""}) = -1
     * StringUtils.indexOfDifference(new String[] {"", null}) = 0
     * StringUtils.indexOfDifference(new String[] {"abc", null, null}) = 0
     * StringUtils.indexOfDifference(new String[] {null, null, "abc"}) = 0
     * StringUtils.indexOfDifference(new String[] {"", "abc"}) = 0
     * StringUtils.indexOfDifference(new String[] {"abc", ""}) = 0
     * StringUtils.indexOfDifference(new String[] {"abc", "abc"}) = -1
     * StringUtils.indexOfDifference(new String[] {"abc", "a"}) = 1
     * StringUtils.indexOfDifference(new String[] {"ab", "abxyz"}) = 2
     * StringUtils.indexOfDifference(new String[] {"abcde", "abxyz"}) = 2
     * StringUtils.indexOfDifference(new String[] {"abcde", "xyz"}) = 0
     * StringUtils.indexOfDifference(new String[] {"xyz", "abcde"}) = 0
     * StringUtils.indexOfDifference(new String[] {"i am a machine", "i am a robot"}) = 7
     * </pre>
     *
     * @param css array of CharSequences, entries may be null
     * @return the index where the strings begin to differ; -1 if they are all equal
     * @since 3.0 Changed signature from indexOfDifference(String...) to indexOfDifference(CharSequence...)
     */
    public static int indexOfDifference(final CharSequence... css) {
        if (css == null || css.length <= 1) {
            return INDEX_NOT_FOUND;
        }
        boolean anyStringNull = false;
        boolean allStringsNull = true;
        final int arrayLen = css.length;
        int shortestStrLen = Integer.MAX_VALUE;
        int longestStrLen = 0;

        // find the min and max string lengths; this avoids checking to make
        // sure we are not exceeding the length of the string each time through
        // the bottom loop.
        for (int i = 0; i < arrayLen; i++) {
            if (css[i] == null) {
                anyStringNull = true;
                shortestStrLen = 0;
            } else {
                allStringsNull = false;
                shortestStrLen = Math.min(css[i].length(), shortestStrLen);
                longestStrLen = Math.max(css[i].length(), longestStrLen);
            }
        }

        // handle lists containing all nulls or all empty strings
        if (allStringsNull || longestStrLen == 0 && !anyStringNull) {
            return INDEX_NOT_FOUND;
        }

        // handle lists containing some nulls or some empty strings
        if (shortestStrLen == 0) {
            return 0;
        }

        // find the position with the first difference across all strings
        int firstDiff = -1;
        for (int stringPos = 0; stringPos < shortestStrLen; stringPos++) {
            final char comparisonChar = css[0].charAt(stringPos);
            for (int arrayPos = 1; arrayPos < arrayLen; arrayPos++) {
                if (css[arrayPos].charAt(stringPos) != comparisonChar) {
                    firstDiff = stringPos;
                    break;
                }
            }
            if (firstDiff != -1) {
                break;
            }
        }

        if (firstDiff == -1 && shortestStrLen != longestStrLen) {
            // we compared all of the characters up to the length of the
            // shortest string and didn't find a match, but the string lengths
            // vary, so return the length of the shortest string.
            return shortestStrLen;
        }
        return firstDiff;
    }

    /**
     * <p>Compares all Strings in an array and returns the initial sequence of
     * characters that is common to all of them.</p>
     * <p>
     * <p>For example,
     * <code>getCommonPrefix(new String[] {"i am a machine", "i am a robot"}) -&gt; "i am a "</code></p>
     * <p>
     * <pre>
     * StringUtils.getCommonPrefix(null) = ""
     * StringUtils.getCommonPrefix(new String[] {}) = ""
     * StringUtils.getCommonPrefix(new String[] {"abc"}) = "abc"
     * StringUtils.getCommonPrefix(new String[] {null, null}) = ""
     * StringUtils.getCommonPrefix(new String[] {"", ""}) = ""
     * StringUtils.getCommonPrefix(new String[] {"", null}) = ""
     * StringUtils.getCommonPrefix(new String[] {"abc", null, null}) = ""
     * StringUtils.getCommonPrefix(new String[] {null, null, "abc"}) = ""
     * StringUtils.getCommonPrefix(new String[] {"", "abc"}) = ""
     * StringUtils.getCommonPrefix(new String[] {"abc", ""}) = ""
     * StringUtils.getCommonPrefix(new String[] {"abc", "abc"}) = "abc"
     * StringUtils.getCommonPrefix(new String[] {"abc", "a"}) = "a"
     * StringUtils.getCommonPrefix(new String[] {"ab", "abxyz"}) = "ab"
     * StringUtils.getCommonPrefix(new String[] {"abcde", "abxyz"}) = "ab"
     * StringUtils.getCommonPrefix(new String[] {"abcde", "xyz"}) = ""
     * StringUtils.getCommonPrefix(new String[] {"xyz", "abcde"}) = ""
     * StringUtils.getCommonPrefix(new String[] {"i am a machine", "i am a robot"}) = "i am a "
     * </pre>
     *
     * @param strs array of String objects, entries may be null
     * @return the initial sequence of characters that are common to all Strings
     * in the array; empty String if the array is null, the elements are all null
     * or if there is no common prefix.
     * @since 2.4
     */
    public static String getCommonPrefix(final String... strs) {
        if (strs == null || strs.length == 0) {
            return EMPTY;
        }
        final int smallestIndexOfDiff = indexOfDifference(strs);
        if (smallestIndexOfDiff == INDEX_NOT_FOUND) {
            // all strings were identical
            if (strs[0] == null) {
                return EMPTY;
            }
            return strs[0];
        } else if (smallestIndexOfDiff == 0) {
            // there were no common initial characters
            return EMPTY;
        } else {
            // we found a common initial character sequence
            return strs[0].substring(0, smallestIndexOfDiff);
        }
    }

    // Misc
    //-----------------------------------------------------------------------

    /**
     * <p>Find the Levenshtein distance between two Strings.</p>
     * <p>
     * <p>This is the number of changes needed to change one String into
     * another, where each change is a single character modification (deletion,
     * insertion or substitution).</p>
     * <p>
     * <p>The previous implementation of the Levenshtein distance algorithm
     * was from <a href="http://www.merriampark.com/ld.htm">http://www.merriampark.com/ld.htm</a></p>
     * <p>
     * <p>Chas Emerick has written an implementation in Java, which avoids an OutOfMemoryError
     * which can occur when my Java implementation is used with very large strings.<br>
     * This implementation of the Levenshtein distance algorithm
     * is from <a href="http://www.merriampark.com/ldjava.htm">http://www.merriampark.com/ldjava.htm</a></p>
     * <p>
     * <pre>
     * StringUtils.getLevenshteinDistance(null, *)             = IllegalArgumentException
     * StringUtils.getLevenshteinDistance(*, null)             = IllegalArgumentException
     * StringUtils.getLevenshteinDistance("","")               = 0
     * StringUtils.getLevenshteinDistance("","a")              = 1
     * StringUtils.getLevenshteinDistance("aaapppp", "")       = 7
     * StringUtils.getLevenshteinDistance("frog", "fog")       = 1
     * StringUtils.getLevenshteinDistance("fly", "ant")        = 3
     * StringUtils.getLevenshteinDistance("elephant", "hippo") = 7
     * StringUtils.getLevenshteinDistance("hippo", "elephant") = 7
     * StringUtils.getLevenshteinDistance("hippo", "zzzzzzzz") = 8
     * StringUtils.getLevenshteinDistance("hello", "hallo")    = 1
     * </pre>
     *
     * @param s the first String, must not be null
     * @param t the second String, must not be null
     * @return result distance
     * @throws IllegalArgumentException if either String input {@code null}
     * @since 3.0 Changed signature from getLevenshteinDistance(String, String) to
     * getLevenshteinDistance(CharSequence, CharSequence)
     */
    public static int getLevenshteinDistance(CharSequence s, CharSequence t) {
        if (s == null || t == null) {
            throw new IllegalArgumentException("Strings must not be null");
        }

        /*
           The difference between this impl. and the previous is that, rather
           than creating and retaining a matrix of size s.length() + 1 by t.length() + 1,
           we maintain two single-dimensional arrays of length s.length() + 1.  The first, d,
           is the 'current working' distance array that maintains the newest distance cost
           counts as we iterate through the characters of String s.  Each time we increment
           the index of String t we are comparing, d is copied to p, the second int[].  Doing so
           allows us to retain the previous cost counts as required by the algorithm (taking
           the minimum of the cost count to the left, up one, and diagonally up and to the left
           of the current cost count being calculated).  (Note that the arrays aren't really
           copied anymore, just switched...this is clearly much better than cloning an array
           or doing a System.arraycopy() each time  through the outer loop.)

           Effectively, the difference between the two implementations is this one does not
           cause an out of memory condition when calculating the LD over two very large strings.
         */

        int n = s.length(); // length of s
        int m = t.length(); // length of t

        if (n == 0) {
            return m;
        } else if (m == 0) {
            return n;
        }

        if (n > m) {
            // swap the input strings to consume less memory
            final CharSequence tmp = s;
            s = t;
            t = tmp;
            n = m;
            m = t.length();
        }

        int p[] = new int[n + 1]; //'previous' cost array, horizontally
        int d[] = new int[n + 1]; // cost array, horizontally
        int _d[]; //placeholder to assist in swapping p and d

        // indexes into strings s and t
        int i; // iterates through s
        int j; // iterates through t

        char t_j; // jth character of t

        int cost; // cost

        for (i = 0; i <= n; i++) {
            p[i] = i;
        }

        for (j = 1; j <= m; j++) {
            t_j = t.charAt(j - 1);
            d[0] = j;

            for (i = 1; i <= n; i++) {
                cost = s.charAt(i - 1) == t_j ? 0 : 1;
                // minimum of cell to the left+1, to the top+1, diagonally left and up +cost
                d[i] = Math.min(Math.min(d[i - 1] + 1, p[i] + 1), p[i - 1] + cost);
            }

            // copy current distance counts to 'previous row' distance counts
            _d = p;
            p = d;
            d = _d;
        }

        // our last action in the above loop was to switch d and p, so p now
        // actually has the most recent cost counts
        return p[n];
    }

    /**
     * <p>Find the Levenshtein distance between two Strings if it's less than or equal to a given
     * threshold.</p>
     * <p>
     * <p>This is the number of changes needed to change one String into
     * another, where each change is a single character modification (deletion,
     * insertion or substitution).</p>
     * <p>
     * <p>This implementation follows from Algorithms on Strings, Trees and Sequences by Dan Gusfield
     * and Chas Emerick's implementation of the Levenshtein distance algorithm from
     * <a href="http://www.merriampark.com/ld.htm">http://www.merriampark.com/ld.htm</a></p>
     * <p>
     * <pre>
     * StringUtils.getLevenshteinDistance(null, *, *)             = IllegalArgumentException
     * StringUtils.getLevenshteinDistance(*, null, *)             = IllegalArgumentException
     * StringUtils.getLevenshteinDistance(*, *, -1)               = IllegalArgumentException
     * StringUtils.getLevenshteinDistance("","", 0)               = 0
     * StringUtils.getLevenshteinDistance("aaapppp", "", 8)       = 7
     * StringUtils.getLevenshteinDistance("aaapppp", "", 7)       = 7
     * StringUtils.getLevenshteinDistance("aaapppp", "", 6))      = -1
     * StringUtils.getLevenshteinDistance("elephant", "hippo", 7) = 7
     * StringUtils.getLevenshteinDistance("elephant", "hippo", 6) = -1
     * StringUtils.getLevenshteinDistance("hippo", "elephant", 7) = 7
     * StringUtils.getLevenshteinDistance("hippo", "elephant", 6) = -1
     * </pre>
     *
     * @param s         the first String, must not be null
     * @param t         the second String, must not be null
     * @param threshold the target threshold, must not be negative
     * @return result distance, or {@code -1} if the distance would be greater than the threshold
     * @throws IllegalArgumentException if either String input {@code null} or negative threshold
     */
    public static int getLevenshteinDistance(CharSequence s, CharSequence t, final int threshold) {
        if (s == null || t == null) {
            throw new IllegalArgumentException("Strings must not be null");
        }
        if (threshold < 0) {
            throw new IllegalArgumentException("Threshold must not be negative");
        }

        /*
        This implementation only computes the distance if it's less than or equal to the
        threshold value, returning -1 if it's greater.  The advantage is performance: unbounded
        distance is O(nm), but a bound of k allows us to reduce it to O(km) time by only
        computing a diagonal stripe of width 2k + 1 of the cost table.
        It is also possible to use this to compute the unbounded Levenshtein distance by starting
        the threshold at 1 and doubling each time until the distance is found; this is O(dm), where
        d is the distance.

        One subtlety comes from needing to ignore entries on the border of our stripe
        eg.
        p[] = |#|#|#|*
        d[] =  *|#|#|#|
        We must ignore the entry to the left of the leftmost member
        We must ignore the entry above the rightmost member

        Another subtlety comes from our stripe running off the matrix if the strings aren't
        of the same size.  Since string s is always swapped to be the shorter of the two,
        the stripe will always run off to the upper right instead of the lower left of the matrix.

        As a concrete example, suppose s is of length 5, t is of length 7, and our threshold is 1.
        In this case we're going to walk a stripe of length 3.  The matrix would look like so:

           1 2 3 4 5
        1 |#|#| | | |
        2 |#|#|#| | |
        3 | |#|#|#| |
        4 | | |#|#|#|
        5 | | | |#|#|
        6 | | | | |#|
        7 | | | | | |

        Note how the stripe leads off the table as there is no possible way to turn a string of length 5
        into one of length 7 in edit distance of 1.

        Additionally, this implementation decreases memory usage by using two
        single-dimensional arrays and swapping them back and forth instead of allocating
        an entire n by m matrix.  This requires a few minor changes, such as immediately returning
        when it's detected that the stripe has run off the matrix and initially filling the arrays with
        large values so that entries we don't compute are ignored.

        See Algorithms on Strings, Trees and Sequences by Dan Gusfield for some discussion.
         */

        int n = s.length(); // length of s
        int m = t.length(); // length of t

        // if one string is empty, the edit distance is necessarily the length of the other
        if (n == 0) {
            return m <= threshold ? m : -1;
        } else if (m == 0) {
            return n <= threshold ? n : -1;
        }

        if (n > m) {
            // swap the two strings to consume less memory
            final CharSequence tmp = s;
            s = t;
            t = tmp;
            n = m;
            m = t.length();
        }

        int p[] = new int[n + 1]; // 'previous' cost array, horizontally
        int d[] = new int[n + 1]; // cost array, horizontally
        int _d[]; // placeholder to assist in swapping p and d

        // fill in starting table values
        final int boundary = Math.min(n, threshold) + 1;
        for (int i = 0; i < boundary; i++) {
            p[i] = i;
        }
        // these fills ensure that the value above the rightmost entry of our
        // stripe will be ignored in following loop iterations
        Arrays.fill(p, boundary, p.length, Integer.MAX_VALUE);
        Arrays.fill(d, Integer.MAX_VALUE);

        // iterates through t
        for (int j = 1; j <= m; j++) {
            final char t_j = t.charAt(j - 1); // jth character of t
            d[0] = j;

            // compute stripe indices, constrain to array size
            final int min = Math.max(1, j - threshold);
            final int max = (j > Integer.MAX_VALUE - threshold) ? n : Math.min(n, j + threshold);

            // the stripe may lead off of the table if s and t are of different sizes
            if (min > max) {
                return -1;
            }

            // ignore entry left of leftmost
            if (min > 1) {
                d[min - 1] = Integer.MAX_VALUE;
            }

            // iterates through [min, max] in s
            for (int i = min; i <= max; i++) {
                if (s.charAt(i - 1) == t_j) {
                    // diagonally left and up
                    d[i] = p[i - 1];
                } else {
                    // 1 + minimum of cell to the left, to the top, diagonally left and up
                    d[i] = 1 + Math.min(Math.min(d[i - 1], p[i]), p[i - 1]);
                }
            }

            // copy current distance counts to 'previous row' distance counts
            _d = p;
            p = d;
            d = _d;
        }

        // if p[n] is greater than the threshold, there's no guarantee on it being the correct
        // distance
        if (p[n] <= threshold) {
            return p[n];
        }
        return -1;
    }

    /**
     * <p>Find the Jaro Winkler Distance which indicates the similarity score between two Strings.</p>
     * <p>
     * <p>The Jaro measure is the weighted sum of percentage of matched characters from each file and transposed characters.
     * Winkler increased this measure for matching initial characters.</p>
     * <p>
     * <p>This implementation is based on the Jaro Winkler similarity algorithm
     * from <a href="http://en.wikipedia.org/wiki/Jaro%E2%80%93Winkler_distance">http://en.wikipedia.org/wiki/Jaro%E2%80%93Winkler_distance</a>.</p>
     * <p>
     * <pre>
     * StringUtils.getJaroWinklerDistance(null, null)          = IllegalArgumentException
     * StringUtils.getJaroWinklerDistance("","")               = 0.0
     * StringUtils.getJaroWinklerDistance("","a")              = 0.0
     * StringUtils.getJaroWinklerDistance("aaapppp", "")       = 0.0
     * StringUtils.getJaroWinklerDistance("frog", "fog")       = 0.93
     * StringUtils.getJaroWinklerDistance("fly", "ant")        = 0.0
     * StringUtils.getJaroWinklerDistance("elephant", "hippo") = 0.44
     * StringUtils.getJaroWinklerDistance("hippo", "elephant") = 0.44
     * StringUtils.getJaroWinklerDistance("hippo", "zzzzzzzz") = 0.0
     * StringUtils.getJaroWinklerDistance("hello", "hallo")    = 0.88
     * StringUtils.getJaroWinklerDistance("ABC Corporation", "ABC Corp") = 0.91
     * StringUtils.getJaroWinklerDistance("D N H Enterprises Inc", "D &amp; H Enterprises, Inc.") = 0.93
     * StringUtils.getJaroWinklerDistance("My Gym Children's Fitness Center", "My Gym. Childrens Fitness") = 0.94
     * StringUtils.getJaroWinklerDistance("PENNSYLVANIA", "PENNCISYLVNIA")    = 0.9
     * </pre>
     *
     * @param first  the first String, must not be null
     * @param second the second String, must not be null
     * @return result distance
     * @throws IllegalArgumentException if either String input {@code null}
     * @since 3.3
     */
    public static double getJaroWinklerDistance(final CharSequence first, final CharSequence second) {
        final double DEFAULT_SCALING_FACTOR = 0.1;

        if (first == null || second == null) {
            throw new IllegalArgumentException("Strings must not be null");
        }

        final double jaro = score(first, second);
        final int cl = commonPrefixLength(first, second);
        final double matchScore = Math.round((jaro + (DEFAULT_SCALING_FACTOR * cl * (1.0 - jaro))) * 100.0) / 100.0;

        return matchScore;
    }

    /**
     * This method returns the Jaro-Winkler score for string matching.
     *
     * @param first  the first string to be matched
     * @param second the second string to be machted
     * @return matching score without scaling factor impact
     */
    private static double score(final CharSequence first, final CharSequence second) {
        String shorter;
        String longer;

        // Determine which String is longer.
        if (first.length() > second.length()) {
            longer = first.toString().toLowerCase();
            shorter = second.toString().toLowerCase();
        } else {
            longer = second.toString().toLowerCase();
            shorter = first.toString().toLowerCase();
        }

        // Calculate the half length() distance of the shorter String.
        final int halflength = (shorter.length() / 2) + 1;

        // Find the set of matching characters between the shorter and longer strings. Note that
        // the set of matching characters may be different depending on the order of the strings.
        final String m1 = getSetOfMatchingCharacterWithin(shorter, longer, halflength);
        final String m2 = getSetOfMatchingCharacterWithin(longer, shorter, halflength);

        // If one or both of the sets of common characters is empty, then
        // there is no similarity between the two strings.
        if (m1.length() == 0 || m2.length() == 0) {
            return 0.0;
        }

        // If the set of common characters is not the same size, then
        // there is no similarity between the two strings, either.
        if (m1.length() != m2.length()) {
            return 0.0;
        }

        // Calculate the number of transposition between the two sets
        // of common characters.
        final int transpositions = transpositions(m1, m2);

        // Calculate the distance.
        final double dist =
                (m1.length() / ((double) shorter.length()) +
                        m2.length() / ((double) longer.length()) +
                        (m1.length() - transpositions) / ((double) m1.length())) / 3.0;
        return dist;
    }

    /**
     * Gets a set of matching characters between two strings.
     * <p>
     * <p><Two characters from the first string and the second string are considered matching if the character's
     * respective positions are no farther than the limit value.</p>
     *
     * @param first  The first string.
     * @param second The second string.
     * @param limit  The maximum distance to consider.
     * @return A string contain the set of common characters.
     */
    private static String getSetOfMatchingCharacterWithin(final CharSequence first, final CharSequence second, final int limit) {
        final StringBuilder common = new StringBuilder();
        final StringBuilder copy = new StringBuilder(second);

        for (int i = 0; i < first.length(); i++) {
            final char ch = first.charAt(i);
            boolean found = false;

            // See if the character is within the limit positions away from the original position of that character.
            for (int j = Math.max(0, i - limit); !found && j < Math.min(i + limit, second.length()); j++) {
                if (copy.charAt(j) == ch) {
                    found = true;
                    common.append(ch);
                    copy.setCharAt(j, '*');
                }
            }
        }
        return common.toString();
    }

    /**
     * Calculates the number of transposition between two strings.
     *
     * @param first  The first string.
     * @param second The second string.
     * @return The number of transposition between the two strings.
     */
    private static int transpositions(CharSequence first, CharSequence second) {
        int transpositions = 0;
        for (int i = 0; i < first.length(); i++) {
            if (first.charAt(i) != second.charAt(i)) {
                transpositions++;
            }
        }
        return transpositions / 2;
    }

    /**
     * Calculates the number of characters from the beginning of the strings that match exactly one-to-one,
     * up to a maximum of four (4) characters.
     *
     * @param first  The first string.
     * @param second The second string.
     * @return A number between 0 and 4.
     */
    private static int commonPrefixLength(CharSequence first, CharSequence second) {
        final int result = getCommonPrefix(first.toString(), second.toString()).length();

        // Limit the result to 4.
        return result > 4 ? 4 : result;
    }


    /**
     * <p>
     * Similar to <a
     * href="http://www.w3.org/TR/xpath/#function-normalize-space">http://www.w3.org/TR/xpath/#function-normalize
     * -space</a>
     * </p>
     * <p>
     * The function returns the argument string with whitespace normalized by using
     * <code>{@link #trim(String)}</code> to remove leading and trailing whitespace
     * and then replacing sequences of whitespace characters by a single space.
     * </p>
     * In XML Whitespace characters are the same as those allowed by the <a
     * href="http://www.w3.org/TR/REC-xml/#NT-S">S</a> production, which is S ::= (#x20 | #x9 | #xD | #xA)+
     * <p>
     * Java's regexp pattern \s defines whitespace as [ \t\n\x0B\f\r]
     * <p>
     * <p>For reference:</p>
     * <ul>
     * <li>\x0B = vertical tab</li>
     * <li>\f = #xC = form feed</li>
     * <li>#x20 = space</li>
     * <li>#x9 = \t</li>
     * <li>#xA = \n</li>
     * <li>#xD = \r</li>
     * </ul>
     * <p>
     * <p>
     * The difference is that Java's whitespace includes vertical tab and form feed, which this functional will also
     * normalize. Additionally <code>{@link #trim(String)}</code> removes control characters (char &lt;= 32) from both
     * ends of this String.
     * </p>
     *
     * @param str the source String to normalize whitespaces from, may be null
     * @return the modified string with whitespace normalized, {@code null} if null String input
     * @see Pattern
     * @see #trim(String)
     * @see <a
     * href="http://www.w3.org/TR/xpath/#function-normalize-space">http://www.w3.org/TR/xpath/#function-normalize-space</a>
     * @since 3.0
     */
    public static String normalizeSpace(final String str) {
        if (str == null) {
            return null;
        }
        return WHITESPACE_PATTERN.matcher(trim(str)).replaceAll(SPACE);
    }


    /**
     * Converts a <code>byte[]</code> to a String using the specified character encoding.
     *
     * @param bytes       the byte array to read from
     * @param charsetName the encoding to use, if null then use the platform default
     * @return a new String
     * @throws UnsupportedEncodingException If the named charset is not supported
     * @throws NullPointerException         if the input is null
     * @since 3.1
     * @deprecated use {@link StringUtils#toEncodedString(byte[], Charset)} instead of String constants in your code
     */
    @Deprecated
    public static String toString(final byte[] bytes, final String charsetName) throws UnsupportedEncodingException {
        return charsetName != null ? new String(bytes, charsetName) : new String(bytes, Charset.defaultCharset());
    }

    /**
     * Converts a <code>byte[]</code> to a String using the specified character encoding.
     *
     * @param bytes   the byte array to read from
     * @param charset the encoding to use, if null then use the platform default
     * @return a new String
     * @throws NullPointerException if {@code bytes} is null
     * @since 3.3 No longer throws {@link UnsupportedEncodingException}.
     */
    public static String toEncodedString(byte[] bytes, Charset charset) {
        return new String(bytes, charset != null ? charset : Charset.defaultCharset());
    }

}