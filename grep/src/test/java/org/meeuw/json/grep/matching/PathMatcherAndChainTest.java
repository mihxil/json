package org.meeuw.json.grep.matching;

import org.junit.Test;
import org.meeuw.json.ArrayEntry;
import org.meeuw.json.KeyEntry;
import org.meeuw.json.Path;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Michiel Meeuwissen
 * @since 0.6
 */
public class PathMatcherAndChainTest {


	@Test
	public void needsKeyCollection() {

		PathMatcher matcher = new PathMatcherAndChain(
				new SinglePathMatcher(new PreciseMatch("c"), new ArrayEntryMatch()),
				new ObjectMatcherNot(new ObjectHasKeyMatcher("b1")));

		assertTrue(matcher.needsKeyCollection().test(new Path(new KeyEntry("c"), new ArrayEntry())));
		assertFalse(matcher.needsKeyCollection().test(new Path(new KeyEntry("c"))));


	}

}
