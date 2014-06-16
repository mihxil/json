package org.meeuw.json.grep;

import org.meeuw.json.PathEntry;

import java.util.Deque;

/**
 * The matcher that matches never.
 */
class NeverPathMatcher implements PathMatcher {

    @Override
    public boolean matches(Deque<PathEntry> path, String value) {
        return false;
    }
}
