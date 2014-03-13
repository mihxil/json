package org.meeuw.json.grep;

import com.fasterxml.jackson.core.JsonParser;
import org.meeuw.json.ArrayEntry;
import org.meeuw.json.JsonIterator;
import org.meeuw.json.ParseEvent;
import org.meeuw.json.PathEntry;

import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;

/**
 * jsongrep. To search in json streams. You can match on the keys
 * E.g. 'rows.*.id' will result in only all 'id' values in a json structure.
 * Try the commandline option -help for an overview of all features.
 */
public class Grep implements Iterator<GrepEvent> {

    // settings
    private final PathMatcher matcher;

    private PathMatcher recordMatcher = new NeverPathMatcher();

    final JsonIterator wrapped;
    ParseEvent next = null;

    public Grep(PathMatcher matcher, JsonParser jp) {
        this.matcher = matcher;
        this.wrapped = new JsonIterator(jp);
    }
    @Override
    public boolean hasNext() {
        findNext();
        return next != null;
    }
    @Override
    public GrepEvent next() {
        findNext();
        if (next == null) {
            throw new NoSuchElementException();
        }
        ParseEvent result = next;
        next = null;
        return new GrepEvent(result);
    }
    @Override
    public void remove() {
        wrapped.remove();
    }

    protected void findNext() {
        if( next == null) {
            while (wrapped.hasNext() && next == null) {
                ParseEvent event = wrapped.next();
                switch (event.getToken()) {
                    case VALUE_STRING:
                    case VALUE_NUMBER_INT:
                    case VALUE_NUMBER_FLOAT:
                    case VALUE_TRUE:
                    case VALUE_FALSE:
                    case VALUE_NULL:
                    case END_ARRAY:
                    case END_OBJECT:
                        String value = event.getValue();
                        if (recordMatcher.matches(event.getPath(), value)) {
                            //output.print(recordsep);
                            //needsSeperator = false;
                        }
                        if (matcher.matches(event.getPath(), value)) {
                            //if (needsSeperator) {
                            //output.print(sep);
                            //}
                            next = event;
                        }
                }
            }
        }
    }




    public PathMatcher getRecordMatcher() {
        return recordMatcher;
    }

    public void setRecordMatcher(PathMatcher recordMatcher) {
        this.recordMatcher = recordMatcher;
    }

    /**
     * A key pattern matches one key in a json object.
     */
    protected static interface KeyPattern {
        boolean matches(PathEntry key);
    }

    /**
     * a precise key pattern matches only if the key exactly equals to a certain value.
     */
    protected static class PreciseMatch implements  KeyPattern {
        private final String key;

        public PreciseMatch(String key) {
            this.key = key;
        }

        @Override
        public boolean matches(PathEntry key) {
            return this.key.equals(key.toString());
        }
        @Override
        public String toString() {
            return key;
        }
    }

    /**
     * A wild card matches always.
     */
    protected static class Wildcard implements  KeyPattern {

        @Override
        public boolean matches(PathEntry key) {
            return true;
        }

        @Override
        public String toString() {
            return "*";
        }
    }

    /**
     * A Patch matcher defines matches on an entire json path and value.
     */
    public static interface PathMatcher {

        boolean matches(Deque<PathEntry> path, String value);
    }

    /**
     * a keys matcher only considers the keys (and indices) of a json path for matching.
     */
    protected static abstract class KeysMatcher implements PathMatcher {
        @Override
        final public boolean matches(Deque<PathEntry> path, String value) {
            return matches(path);
        }
        protected abstract  boolean matches(Deque<PathEntry> path);

    }

    /**
     * a keys matcher only considers the value of a json path for matching.
     */
    protected static abstract class ValueMatcher implements PathMatcher {
        @Override
        final public boolean matches(Deque<PathEntry> path, String value) {
            return matches(value);
        }

        protected abstract boolean matches(String value);

    }

    /**
     * Matches the value with a regular expression.
     */
    protected static class ValueRegexpMatcher extends ValueMatcher {
        private final Pattern pattern;

        public ValueRegexpMatcher(Pattern pattern) {
            this.pattern = pattern;
        }

        @Override
        protected boolean matches(String value) {
            return pattern.matcher(value).matches();
        }
    }

    public static class ValueEqualsMatcher extends ValueMatcher {
        private final String test;

        public ValueEqualsMatcher(String test) {
            this.test = test;
        }

        @Override
        protected boolean matches(String value) {
            return test.equals(value);
        }
    }


    /**
     * A single path matches precisely one 'path'. For multiple matches we'd wrap them in {@link PathMatcherOrChain} or {@link PathMatcherAndChain}
     */
    protected static class SinglePathMatcher extends KeysMatcher {
        private final KeyPattern[] pathPattern;

        private boolean ignoreArrays;

        public SinglePathMatcher(KeyPattern... pathPattern) {
            this(false, pathPattern);
        }

        public SinglePathMatcher(boolean ignoreArrays, KeyPattern... pathPattern) {
            this.ignoreArrays = ignoreArrays;
            this.pathPattern = pathPattern;
        }

        @Override
        public boolean matches(Deque<PathEntry> path) {
            if (!ignoreArrays && path.size() != pathPattern.length) {
                return false;
            }
            int i = 0;
            for (PathEntry e : path) {
                if (ignoreArrays && e instanceof ArrayEntry) {
                    continue;
                }
                if (! pathPattern[i++].matches(e)) {
                    return false;
                }
            }
            return i == pathPattern.length;
        }
    }

    protected static class PathMatcherOrChain implements PathMatcher {
        private final PathMatcher[] matchers;

        public PathMatcherOrChain(PathMatcher... matchers) {
            this.matchers = matchers;
        }

        @Override
        public boolean matches(Deque<PathEntry> path, String value) {
            for (PathMatcher matcher : matchers) {
                if (matcher.matches(path, value)) {
                    return true;
                }
            }
            return false;
        }
    }

    public static class PathMatcherAndChain implements PathMatcher {
        private final PathMatcher[] matchers;

        public PathMatcherAndChain(PathMatcher... matchers) {
            this.matchers = matchers;
        }

        @Override
        public boolean matches(Deque<PathEntry> path, String value) {
            for (PathMatcher matcher : matchers) {
                if (! matcher.matches(path, value)) {
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * The matcher that matches never.
     */
    protected  static class NeverPathMatcher implements PathMatcher {

        @Override
        public boolean matches(Deque<PathEntry> path, String value) {
            return false;
        }
    }


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

    public static PathMatcher parseKeysMatcher(String arg, boolean ignoreArrays) {
        String[] split = arg.split("\\.");
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
        return new PreciseMatch(arg);
    }





}
