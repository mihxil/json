package org.meeuw.json.grep.matching;

import java.util.Deque;
import java.util.List;

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
        boolean result = matches(event.getPath());
        return result;
    }
    protected abstract  boolean matches(List<PathEntry> path);

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
