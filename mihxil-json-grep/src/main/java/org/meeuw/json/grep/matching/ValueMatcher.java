package org.meeuw.json.grep.matching;

import org.meeuw.json.ParseEvent;
import org.meeuw.json.Path;
import org.meeuw.util.Predicate;
import org.meeuw.util.Predicates;

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
        return Predicates.alwaysFalse();
    }

    @Override
    public Predicate<Path> needsObjectCollection() {
        return Predicates.alwaysFalse();
    }

}
