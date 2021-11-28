package org.meeuw.json.grep.matching;

import java.util.function.Predicate;

import org.meeuw.json.ParseEvent;
import org.meeuw.json.Path;

/**
 * @author Michiel Meeuwissen
 * @since ...
 */
public class ObjectHasKeyMatcher extends ObjectMatcher {

    private final String key;

    public ObjectHasKeyMatcher(String key) {
        this.key = key;
    }

    @Override
    protected boolean matches(ParseEvent event) {
        return event.getKeys().contains(key);
    }

    @Override
    public Predicate<Path> needsKeyCollection() {
        return (path) -> true;
    }

    @Override
    public Predicate<Path> needsObjectCollection() {
        return (path) -> false;
    }

	@Override
	public String toString() {
		return "contains " + key;
	}

}
