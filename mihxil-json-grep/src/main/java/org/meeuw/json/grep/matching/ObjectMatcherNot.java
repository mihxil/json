package org.meeuw.json.grep.matching;

import org.meeuw.json.ParseEvent;
import org.meeuw.json.Path;
import org.meeuw.util.Predicate;

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
	protected boolean matches(ParseEvent event) {
		return ! wrapped.matches(event);
	}

	@Override
	public Predicate<Path> needsKeyCollection() {
		return wrapped.needsKeyCollection();
	}

	@Override
	public String toString() {
		return "!" + wrapped.toString();
	}
}
