package org.meeuw.json.grep;

import org.meeuw.json.ParseEvent;

/**
 * A Patch matcher defines matches on an entire json path and value.
 */
interface PathMatcher {

    boolean matches(ParseEvent event, String value);
}
