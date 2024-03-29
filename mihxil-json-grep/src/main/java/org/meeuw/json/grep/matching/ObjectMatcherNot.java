package org.meeuw.json.grep.matching;

import java.util.function.Predicate;

import org.meeuw.json.ParseEvent;
import org.meeuw.json.Path;

/**
 * @author Michiel Meeuwissen
 * @since 1.8
 */
public class ObjectMatcherNot extends ObjectMatcher {

	private final ObjectMatcher wrapped;

	public ObjectMatcherNot(ObjectMatcher wrapped) {
		this.wrapped = wrapped;
	}

	@Override
	protected MatchResult matchesObject(ParseEvent event) {
		return new MatchResult(event, ! wrapped.matches(event).getAsBoolean());
	}

	@Override
	public Predicate<Path> needsKeyCollection() {
		return wrapped.needsKeyCollection();
	}

    @Override
    public Predicate<Path> needsObjectCollection() {
        return wrapped.needsObjectCollection();
    }

	@Override
	public String toString() {
		return "! " + wrapped.toString();
	}
}
