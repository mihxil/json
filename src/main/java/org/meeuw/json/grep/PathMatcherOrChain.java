package org.meeuw.json.grep;

import org.meeuw.json.ParseEvent;

/**
* @author Michiel Meeuwissen
* @since ...
*/
class PathMatcherOrChain implements PathMatcher {
    private final PathMatcher[] matchers;

    public PathMatcherOrChain(PathMatcher... matchers) {
        this.matchers = matchers;
    }

    @Override
    public boolean matches(ParseEvent event, String value) {
        for (PathMatcher matcher : matchers) {
            if (matcher.matches(event, value)) {
                return true;
            }
        }
        return false;
    }
}
