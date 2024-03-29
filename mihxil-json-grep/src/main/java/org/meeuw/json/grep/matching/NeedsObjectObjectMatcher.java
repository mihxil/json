package org.meeuw.json.grep.matching;

import java.util.function.Predicate;

import org.meeuw.json.ParseEvent;
import org.meeuw.json.Path;

/**
 * @author Michiel Meeuwissen
 * @since 0.7
 */
public class NeedsObjectObjectMatcher extends ObjectMatcher {

    private final ObjectMatcher wrapped;

    private NeedsObjectObjectMatcher(ObjectMatcher wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    protected MatchResult matchesObject(ParseEvent event) {
        return wrapped.matches(event);
    }

    @Override
    public Predicate<Path> needsKeyCollection() {
        return wrapped.needsKeyCollection();
    }

    @Override
    public Predicate<Path> needsObjectCollection() {
        return (path) -> true;
    }

    public static ObjectMatcher get(ObjectMatcher objectMatcher, boolean needsObject) {
        return needsObject ? new NeedsObjectObjectMatcher(objectMatcher) : objectMatcher;
    }
}
