package org.meeuw.json.grep.matching;

import java.util.List;
import java.util.function.Predicate;

import org.meeuw.json.ParseEvent;
import org.meeuw.json.Path;
import org.meeuw.json.PathEntry;

/**
 * a keys matcher only considers the keys (and indices) of a json path for matching.
 */
public abstract class KeysMatcher implements PathMatcher {
    @Override
    final public MatchResult matches(ParseEvent event) {
        boolean result = matches(event.getPath());
        return new MatchResult(event, result);
    }
    protected abstract  boolean matches(List<PathEntry> path);

	@Override
	public Predicate<Path> needsKeyCollection() {
		return KeysMatcher.this::matches;
	}

    @Override
    public Predicate<Path> needsObjectCollection() {
        return KeysMatcher.this::matches;
    }

}
