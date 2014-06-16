package org.meeuw.json.grep.matching;

import org.meeuw.json.ParseEvent;

/**
 * @author Michiel Meeuwissen
 * @since ...
 */
class Not implements PathMatcher {

    private final PathMatcher wrapped;

    Not(PathMatcher wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public boolean matches(ParseEvent event, String value) {
        return ! wrapped.matches(event, value);
    }
}
