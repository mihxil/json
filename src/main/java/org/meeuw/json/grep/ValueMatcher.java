package org.meeuw.json.grep;

import org.meeuw.json.PathEntry;

import java.util.Deque;

/**
 * a keys matcher only considers the value of a json path for matching.
 */
abstract class ValueMatcher implements PathMatcher {
    @Override
    final public boolean matches(Deque<PathEntry> path, String value) {
        return matches(value);
    }

    protected abstract boolean matches(String value);

}
