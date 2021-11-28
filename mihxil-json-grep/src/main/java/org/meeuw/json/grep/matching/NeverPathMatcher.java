package org.meeuw.json.grep.matching;

import java.util.function.Predicate;

import org.meeuw.json.ParseEvent;
import org.meeuw.json.Path;

/**
 * The matcher that matches never.
 */
public class NeverPathMatcher implements PathMatcher {

    @Override
    public MatchResult matches(ParseEvent event, String value) {
        return MatchResult.NO;
    }

    @Override
    public Predicate<Path> needsKeyCollection() {
        return (path) -> false;
    }

    @Override
    public Predicate<Path> needsObjectCollection() {
        return (path) -> false;
    }
}
