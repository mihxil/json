package org.meeuw.json.grep.matching;

import org.junit.jupiter.api.Test;
import org.meeuw.json.*;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    @Test
    public void needsObjectCollection() {

        PathMatcher matcher = new PathMatcherAndChain(
                new SinglePathMatcher(new PreciseMatch("c"), new PreciseMatch("d")),
                new JavascriptMatcher("function(doc) {return true}"));

        assertFalse(matcher.needsObjectCollection().test(new Path(new KeyEntry("c"))));
        assertTrue(matcher.needsObjectCollection().test(new Path(new KeyEntry("c"), new KeyEntry("d"))));


    }

}
