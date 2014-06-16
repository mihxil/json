package org.meeuw.json.grep.matching;

import org.meeuw.json.ParseEvent;

/**
 * A Patch matcher defines matches on an entire json path and value.
 */
public interface PathMatcher {

    boolean matches(ParseEvent event, String value);

    boolean needsKeyCollection();
}
