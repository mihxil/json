package org.meeuw.json.grep.matching;

import java.util.Arrays;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.meeuw.json.ParseEvent;
import org.meeuw.json.Path;

/**
* @author Michiel Meeuwissen
*/
public class PathMatcherOrChain implements PathMatcher {
    private final PathMatcher[] matchers;

    public PathMatcherOrChain(PathMatcher... matchers) {
        this.matchers = matchers;
    }

    @Override
    public MatchResult matches(ParseEvent event) {
        int count = 0;
        for (PathMatcher matcher : matchers) {
            count++;
            MatchResult matches = matcher.matches(event);
            if (matches.getAsBoolean()) {
                return new MatchResult(matches.getEvent(), count);
            }
        }
        return new MatchResult(event, false);
    }


    @Override
    public Predicate<Path> needsKeyCollection() {
        return (path) -> Arrays.stream(matchers).anyMatch(m -> m.needsKeyCollection().test(path));
	}

    @Override
    public Predicate<Path> needsObjectCollection() {
        return (path) -> Arrays.stream(matchers).anyMatch(m -> m.needsObjectCollection().test(path));
    }

    public PathMatcher[] getMatchers() {
        return matchers;
    }

    @Override
    public String toString() {
        return Arrays.stream(matchers).map(Object::toString).collect(Collectors.joining(" OR "));
    }

}
