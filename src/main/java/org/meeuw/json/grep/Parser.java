package org.meeuw.json.grep;

import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * @author Michiel Meeuwissen
 * @since 0.4
 */
public class Parser {

    // Parse methods for the command line

    public static Grep.PathMatcher parsePathMatcherChain(String arg, boolean ignoreArrays) {
        String[] split = arg.split(",");
        if (split.length == 1) {
            return parsePathMatcher(arg, ignoreArrays);
        }
        ArrayList<Grep.PathMatcher> list = new ArrayList<Grep.PathMatcher>(split.length);
        for (String s : split) {
            list.add(parsePathMatcher(s, ignoreArrays));
        }
        return new Grep.PathMatcherOrChain(list.toArray(new Grep.PathMatcher[list.size()]));

    }

    protected static Grep.PathMatcher parsePathMatcher(String arg, boolean ignoreArrays) {
        String[] split = arg.split("~", 2);
        if (split.length == 2) {
            return new Grep.PathMatcherAndChain(
                    parseKeysMatcher(split[0], ignoreArrays),
                    new Grep.ValueRegexpMatcher(Pattern.compile(split[1])));
        }
        split = arg.split("=", 2);
        if (split.length == 2) {
            return new Grep.PathMatcherAndChain(
                    parseKeysMatcher(split[0], ignoreArrays),
                    new Grep.ValueEqualsMatcher(split[1]));
        }
        // >, <, operators...

        return parseKeysMatcher(split[0], ignoreArrays);

    }

    public static Grep.SinglePathMatcher parseKeysMatcher(String arg, boolean ignoreArrays) {
        String[] split = arg.split("[\\.\\[]+");
        ArrayList<Grep.KeyPattern> list = new ArrayList<Grep.KeyPattern>(split.length);
        for (String s : split) {
            list.add(parseKeyPattern(s));
        }
        return new Grep.SinglePathMatcher(ignoreArrays, list.toArray(new Grep.KeyPattern[list.size()]));
    }

    protected static Grep.KeyPattern parseKeyPattern(String arg) {
        if ("*".equals(arg)) {
            return new Grep.Wildcard();
        }
        if ("*]".equals(arg)) {
            return new Grep.ArrayEntryMatch();
        }
        if (arg.endsWith("]")) {
            return new Grep.ArrayIndexMatch(Integer.parseInt(arg.substring(0, arg.length() - 1)));
        } else {
            return new Grep.PreciseMatch(arg);
        }
    }


}
