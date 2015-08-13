package org.meeuw.json.grep.matching;

import java.util.function.Predicate;

import org.meeuw.json.Path;

/**
 * @author Michiel Meeuwissen
 * @since 0.6
 */
public class PathMatchers {


	public static Predicate<Path>[] needsKeyCollection(PathMatcher... matchers) {
		Predicate<Path>[] result = new Predicate[matchers.length];
		for (int i = 0; i < matchers.length; i++) {
			result[i] = matchers[i].needsKeyCollection();
		}
		return result;
	}

    public static Predicate<Path>[] needsObjectCollection(PathMatcher... matchers) {
        Predicate<Path>[] result = new Predicate[matchers.length];
        for (int i = 0; i < matchers.length; i++) {
            result[i] = matchers[i].needsObjectCollection();
        }
        return result;
    }
}
