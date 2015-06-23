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
    public int matchWeight(ParseEvent event, String value) {
        int count = 0;
        for (PathMatcher matcher : matchers) {
            count++;
            if (matcher.matches(event, value)) {
                return count;
            }
        }
        return 0;
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
