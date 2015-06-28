package org.meeuw.json.grep.matching;

import java.util.Arrays;
import java.util.function.Predicate;

import org.meeuw.json.ParseEvent;
import org.meeuw.json.Path;
import org.meeuw.util.Predicates;

/**
* @author Michiel Meeuwissen
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

    @Override
    public Predicate<Path> needsKeyCollection() {
		return Predicates.and(PathMatchers.needsKeyCollection(matchers));
    }

    @Override
    public Predicate<Path> needsObjectCollection() {
        return Predicates.and(PathMatchers.needsObjectCollection(matchers));
    }

    public PathMatcher[] getPatterns() {
        return matchers;
    }

    @Override
    public String toString() {
        return String.valueOf(Arrays.asList(matchers));
    }
}
