package org.meeuw.json.grep;

import org.meeuw.json.PathEntry;

import java.util.Deque;

/**
* @author Michiel Meeuwissen
* @since ...
*/
public class PathMatcherAndChain implements PathMatcher {
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
    public PathMatcher[] getPatterns() {
        return matchers;
    }
}
