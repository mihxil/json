package org.meeuw.json.grep.matching;

import java.util.function.Predicate;

import org.meeuw.json.ParseEvent;
import org.meeuw.json.Path;

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
    public MatchResult matches(ParseEvent event, String value) {
        return new MatchResult(event, ! wrapped.matches(event, value).getAsBoolean());
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
