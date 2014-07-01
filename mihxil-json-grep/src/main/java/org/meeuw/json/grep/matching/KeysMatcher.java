package org.meeuw.json.grep.matching;

import java.util.Deque;

import org.meeuw.json.ParseEvent;
import org.meeuw.json.Path;
import org.meeuw.json.PathEntry;
import org.meeuw.util.Predicate;

/**
 * a keys matcher only considers the keys (and indices) of a json path for matching.
 */
public abstract class KeysMatcher implements PathMatcher {
    @Override
    final public boolean matches(ParseEvent event, String value) {
        return matches(event.getPath());
    }
    protected abstract  boolean matches(Deque<PathEntry> path);

	@Override
	public Predicate<Path> needsKeyCollection() {
		return new Predicate<Path>() {
			@Override
			public boolean test(Path pathEntries) {
				return KeysMatcher.this.matches(pathEntries);
			}
		};
	}

    @Override
    public Predicate<Path> needsObjectCollection() {
        return new Predicate<Path>() {
            @Override
            public boolean test(Path pathEntries) {
                return KeysMatcher.this.matches(pathEntries);
            }
        };
    }

}
