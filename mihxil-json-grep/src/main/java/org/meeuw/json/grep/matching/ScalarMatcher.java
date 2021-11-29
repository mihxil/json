package org.meeuw.json.grep.matching;

import java.util.function.Predicate;

import org.meeuw.json.ParseEvent;
import org.meeuw.json.Path;

/**
 * a scalar matcher only considers the value of a json path for matching.
 */
abstract class ScalarMatcher implements PathMatcher {


    @Override
    public Predicate<Path> needsKeyCollection() {
        return (path) -> false;
    }

    @Override
    public Predicate<Path> needsObjectCollection() {
        return (path) -> false;
    }

    @Override
    public final MatchResult matches(ParseEvent event) {
        if (! event.getToken().isScalarValue()) {
            // not a leaf
            return MatchResult.NO;
        } else {
            return matchesScalar(event);
        }
    }

    abstract MatchResult matchesScalar(ParseEvent event);



}
