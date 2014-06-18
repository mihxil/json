package org.meeuw.json.grep.matching;

import org.meeuw.json.ParseEvent;
import org.meeuw.json.Path;
import org.meeuw.util.Predicate;

import java.util.AbstractList;
import java.util.Arrays;

/**
 * A Patch matcher defines matches on an entire json path and value.
 */
public interface PathMatcher {

    boolean matches(ParseEvent event, String value);

    Predicate<Path> needsKeyCollection();

}
