package org.meeuw.json.grep;

import org.meeuw.json.ArrayEntry;
import org.meeuw.json.PathEntry;

import java.util.Deque;

/**
 * A single path matches precisely one 'path'. For multiple matches we'd wrap them in {@link PathMatcherOrChain} or {@link PathMatcherAndChain}
 */
class SinglePathMatcher extends KeysMatcher {
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
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (KeyPattern p : pathPattern) {
            if (builder.length() > 0 && ! (p instanceof ArrayEntryMatch)) builder.append(".");
            builder.append(String.valueOf(p));
        }
        return builder.toString();
    }
    public KeyPattern[] getPatterns() {
        return pathPattern;
    }
}
