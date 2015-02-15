package org.meeuw.json.grep.matching;

import org.meeuw.json.ParseEvent;
import org.meeuw.json.Path;
import org.meeuw.util.Predicate;
import org.meeuw.util.Predicates;

/**
* @author Michiel Meeuwissen
* @since ...
*/
public class PathMatcherOrChain implements PathMatcher {
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

    @Override
    public Predicate<Path> needsKeyCollection() {
		return Predicates.or(PathMatchers.needsKeyCollection(matchers));
	}

    @Override
    public Predicate<Path> needsObjectCollection() {
        return Predicates.or(PathMatchers.needsObjectCollection(matchers));
    }

    public PathMatcher[] getMatchers() {
        return matchers;
    }
}
