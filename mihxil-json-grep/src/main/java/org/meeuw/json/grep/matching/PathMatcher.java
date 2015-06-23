package org.meeuw.json.grep.matching;

import org.meeuw.json.ParseEvent;
import org.meeuw.json.Path;
import org.meeuw.util.Predicate;

/**
 * A Patch matcher defines matches on an entire json path and value.
 */
public interface PathMatcher {

    default boolean matches(ParseEvent event, String value) {
        return matchWeight(event, value) > 0;
    }

    default int matchWeight(ParseEvent event, String value) {
        return matches(event, value) ? 1 : 0;
    }

    Predicate<Path> needsKeyCollection();

    Predicate<Path> needsObjectCollection();

}
