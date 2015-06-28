package org.meeuw.json.grep.parsing;

import java.util.ArrayList;
import java.util.regex.Pattern;

import org.meeuw.json.grep.matching.*;

/**
 * @author Michiel Meeuwissen
 * @since 0.4
 */
public class Parser {

    private Parser() {}

    // Parse methods for the command line

    public static PathMatcher parsePathMatcherChain(String arg) {
        return parsePathMatcherChain(arg, false, false);
    }


    public static PathMatcher parsePathMatcherChain(String arg, boolean ignoreArrays, boolean needsObject) {
        String[] split = arg.split(",");
        if (split.length == 1) {
            return parsePathMatcher(arg, ignoreArrays, needsObject);
        }
        ArrayList<PathMatcher> list = new ArrayList<>(split.length);
        for (String s : split) {
            list.add(parsePathMatcher(s, ignoreArrays, needsObject));
        }
        return new PathMatcherOrChain(list.toArray(new PathMatcher[list.size()]));

    }

    protected static PathMatcher parsePathMatcher(String arg, boolean ignoreArrays, boolean needsObject) {
        String[] split = arg.split("~", 2);
        if (split.length == 2) {
            return new PathMatcherAndChain(
                    parseKeysMatcher(split[0], ignoreArrays),
                    new ValueRegexpMatcher(Pattern.compile(split[1])));
        }

		split = arg.split("\\s+!\\s*contains\\s+", 2);
		if (split.length == 2) {
			return new PathMatcherAndChain(
					parseKeysMatcher(split[0], ignoreArrays),
					NeedsObjectObjectMatcher.get(new ObjectMatcherNot(new ObjectHasKeyMatcher(split[1])), needsObject));
		}
		split = arg.split("\\s+contains\\s+", 2);
		if (split.length == 2) {
			return new PathMatcherAndChain(
					parseKeysMatcher(split[0], ignoreArrays),
					NeedsObjectObjectMatcher.get(new ObjectHasKeyMatcher(split[1]), needsObject));
		}
        split = arg.split("\\s+function\\(", 2);
        if (split.length == 2) {
            return new PathMatcherAndChain(
                    parseKeysMatcher(split[0], ignoreArrays),
                    new JavascriptMatcher("function(" + split[1]));
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
        String[] split = arg.split("[\\.\\[]");
        ArrayList<KeysPattern> list = new ArrayList<>(split.length);
        boolean foundEmpty = false;
        for (String s : split) {
            if (s.isEmpty()) {
                if (foundEmpty) {
                    list.add(new AnyDepthMatcher());
                    foundEmpty = false;
                } else {
                    foundEmpty = true;
                }

            } else {
                foundEmpty = false;
                list.add(parseKeyPattern(s));
            }
        }
        return new SinglePathMatcher(ignoreArrays, list.toArray(new KeysPattern[list.size()]));
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
        }
        if (arg.startsWith("~")) {
            return new RegexpKeyMatch(Pattern.compile(arg.substring(1)));
        }
        return new PreciseMatch(arg);

    }


}
