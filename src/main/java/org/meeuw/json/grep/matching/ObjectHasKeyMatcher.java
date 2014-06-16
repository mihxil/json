package org.meeuw.json.grep.matching;

import org.meeuw.json.ParseEvent;

/**
 * @author Michiel Meeuwissen
 * @since ...
 */
class ObjectHasKeyMatcher extends ObjectMatcher {

    private final String key;

    public ObjectHasKeyMatcher(String key) {
        this.key = key;
    }

    @Override
    protected boolean matches(ParseEvent event) {
        return event.getKeys().contains(key);

    }
}
