package org.meeuw.json.grep;

import org.meeuw.json.PathEntry;

import java.util.Deque;

/**
 * A Patch matcher defines matches on an entire json path and value.
 */
interface PathMatcher {

    boolean matches(Deque<PathEntry> path, String value);
}
