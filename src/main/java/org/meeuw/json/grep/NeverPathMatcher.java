package org.meeuw.json.grep;

import org.meeuw.json.ParseEvent;

/**
 * The matcher that matches never.
 */
class NeverPathMatcher implements PathMatcher {

    @Override
    public boolean matches(ParseEvent event, String value) {
        return false;
    }
}
