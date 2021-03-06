package org.meeuw.json.grep.matching;

import java.util.function.Predicate;

import org.meeuw.json.ParseEvent;
import org.meeuw.json.Path;
import org.meeuw.util.Predicates;

/**
 * The matcher that matches never.
 */
public class NeverPathMatcher implements PathMatcher {

    @Override
    public boolean matches(ParseEvent event, String value) {
        return false;
    }

    @Override
    public Predicate<Path> needsKeyCollection() {
        return Predicates.alwaysFalse();
    }

    @Override
    public Predicate<Path> needsObjectCollection() {
        return Predicates.alwaysFalse();
    }
}
