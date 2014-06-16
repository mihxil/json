package org.meeuw.json.grep.matching;

import org.meeuw.json.ParseEvent;
import org.meeuw.json.PathEntry;

import java.util.Deque;

/**
 * a keys matcher only considers the keys (and indices) of a json path for matching.
 */
public abstract class KeysMatcher implements PathMatcher {
    @Override
    final public boolean matches(ParseEvent event, String value) {
        return matches(event.getPath());
    }
    protected abstract  boolean matches(Deque<PathEntry> path);

    @Override
    public boolean needsKeyCollection() {
        return false;
    }

}
