package org.meeuw.json.grep;

import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * @author Michiel Meeuwissen
 * @since 0.4
 */
public class Parser {

    // Parse methods for the command line

    public static PathMatcher parsePathMatcherChain(String arg, boolean ignoreArrays) {
        String[] split = arg.split(",");
        if (split.length == 1) {
            return parsePathMatcher(arg, ignoreArrays);
        }
        ArrayList<PathMatcher> list = new ArrayList<PathMatcher>(split.length);
        for (String s : split) {
            list.add(parsePathMatcher(s, ignoreArrays));
        }
        return new PathMatcherOrChain(list.toArray(new PathMatcher[list.size()]));

    }

    protected static PathMatcher parsePathMatcher(String arg, boolean ignoreArrays) {
        String[] split = arg.split("~", 2);
        if (split.length == 2) {
            return new PathMatcherAndChain(
                    parseKeysMatcher(split[0], ignoreArrays),
                    new ValueRegexpMatcher(Pattern.compile(split[1])));
        }
        split = arg.split("=", 2);
        if (split.length == 2) {
            return new PathMatcherAndChain(
                    parseKeysMatcher(split[0], ignoreArrays),
                    new ValueEqualsMatcher(split[1]));
        }
        // >, <, operators...

        return parseKeysMatcher(split[0], ignoreArrays);

    }

    public static SinglePathMatcher parseKeysMatcher(String arg, boolean ignoreArrays) {
        String[] split = arg.split("[\\.\\[]+");
        ArrayList<KeyPattern> list = new ArrayList<KeyPattern>(split.length);
        for (String s : split) {
            list.add(parseKeyPattern(s));
        }
        return new SinglePathMatcher(ignoreArrays, list.toArray(new KeyPattern[list.size()]));
    }

    protected static KeyPattern parseKeyPattern(String arg) {
        if ("*".equals(arg)) {
            return new Wildcard();
        }
        if ("*]".equals(arg)) {
            return new ArrayEntryMatch();
        }
        if (arg.endsWith("]")) {
            return new ArrayIndexMatch(Integer.parseInt(arg.substring(0, arg.length() - 1)));
        } else {
            return new PreciseMatch(arg);
        }
    }


}
