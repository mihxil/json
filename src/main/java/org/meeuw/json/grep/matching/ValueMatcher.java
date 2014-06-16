package org.meeuw.json.grep.matching;

import org.meeuw.json.ParseEvent;

/**
 * a keys matcher only considers the value of a json path for matching.
 */
abstract class ValueMatcher implements PathMatcher {
    @Override
    final public boolean matches(ParseEvent event, String value) {
        return matches(value);
    }

    protected abstract boolean matches(String value);

}
