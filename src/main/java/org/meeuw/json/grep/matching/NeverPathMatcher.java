package org.meeuw.json.grep.matching;

import org.meeuw.json.ParseEvent;

/**
 * The matcher that matches never.
 */
public class NeverPathMatcher implements PathMatcher {

    @Override
    public boolean matches(ParseEvent event, String value) {
        return false;
    }

    @Override
    public boolean needsKeyCollection() {
        return false;

    }
}
