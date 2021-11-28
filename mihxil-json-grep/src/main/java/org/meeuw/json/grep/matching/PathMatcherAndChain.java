package org.meeuw.json.grep.matching;

import java.util.Arrays;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.meeuw.json.ParseEvent;
import org.meeuw.json.Path;

/**
 * @author Michiel Meeuwissen
 */
public class PathMatcherAndChain implements PathMatcher {
    private final PathMatcher[] matchers;

    public PathMatcherAndChain(PathMatcher... matchers) {
        this.matchers = matchers;
    }

    @Override
    public MatchResult matches(ParseEvent event, String value) {
        for (PathMatcher matcher : matchers) {
            MatchResult matches = matcher.matches(event, value);
            if (! matches.getAsBoolean()) {
                return MatchResult.NO;
            } else {
                event = matches.getEvent();
            }
        }
        return new MatchResult(event, true);
    }

    @Override
    public Predicate<Path> needsKeyCollection() {
		return (path) -> Arrays.stream(matchers).allMatch(m -> m.needsKeyCollection().test(path));
    }

    @Override
    public Predicate<Path> needsObjectCollection() {
        return (path) -> Arrays.stream(matchers).allMatch(m -> m.needsObjectCollection().test(path));
    }

    public PathMatcher[] getPatterns() {
        return matchers;
    }

    @Override
    public String toString() {
        return Arrays.stream(matchers).map(Object::toString).collect(Collectors.joining(" AND "));
    }
}
