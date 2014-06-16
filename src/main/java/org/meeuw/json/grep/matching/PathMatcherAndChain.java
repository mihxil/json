package org.meeuw.json.grep.matching;

import org.meeuw.json.ParseEvent;

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
    public boolean matches(ParseEvent event, String value) {
        for (PathMatcher matcher : matchers) {
            if (! matcher.matches(event, value)) {
                return false;
            }
        }
        return true;
    }
    public PathMatcher[] getPatterns() {
        return matchers;
    }
}
