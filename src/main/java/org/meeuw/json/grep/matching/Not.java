package org.meeuw.json.grep.matching;

import org.meeuw.json.ParseEvent;

/**
 * @author Michiel Meeuwissen
 * @since ...
 */
public class Not implements PathMatcher {

    private final PathMatcher wrapped;

    public Not(PathMatcher wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public boolean matches(ParseEvent event, String value) {
        return ! wrapped.matches(event, value);
    }

    @Override
    public boolean needsKeyCollection() {
        return wrapped.needsKeyCollection();

    }
}
