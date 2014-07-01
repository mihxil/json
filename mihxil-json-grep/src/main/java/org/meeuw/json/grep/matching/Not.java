package org.meeuw.json.grep.matching;

import org.meeuw.json.ParseEvent;
import org.meeuw.json.Path;
import org.meeuw.util.Predicate;

/**
 * @author Michiel Meeuwissen
 * @since 0.6
 */
public class Not implements PathMatcher {

    private final PathMatcher wrapped;

    public Not(PathMatcher wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public boolean matches(ParseEvent event, String value) {
        return ! wrapped.matches(event, value);
    }

    @Override
    public Predicate<Path> needsKeyCollection() {
        return wrapped.needsKeyCollection();

    }

    @Override
    public Predicate<Path> needsObjectCollection() {
        return wrapped.needsObjectCollection();

    }
}
