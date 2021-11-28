package org.meeuw.json.grep.matching;

import java.util.function.Predicate;

import org.meeuw.json.ParseEvent;
import org.meeuw.json.Path;

/**
 * a keys matcher only considers the value of a json path for matching.
 */
abstract class ValueMatcher implements PathMatcher {
    @Override
    final public boolean matches(ParseEvent event, String value) {
        return matches(value);
    }

    protected abstract boolean matches(String value);

    @Override
    public Predicate<Path> needsKeyCollection() {
        return (path) -> false;
    }

    @Override
    public Predicate<Path> needsObjectCollection() {
        return (path) -> false;    }

}
