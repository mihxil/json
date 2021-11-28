package org.meeuw.json.grep.matching;

import java.util.Collections;

import org.junit.jupiter.api.Test;

import org.meeuw.json.*;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;


/**
 * @author Michiel Meeuwissen
 * @since 0.4
 */
public class SinglePathMatcherTest {

    @Test
    public void grepSinglePathPatcherTest() {
        SinglePathMatcher matcher = new SinglePathMatcher(true,
                new PreciseMatch("titles"),
                new PreciseMatch("value"));
        assertFalse(matcher.matches(asList(new KeyEntry("titles"), new ArrayEntry(2))));
        assertFalse(matcher.matches(asList(new KeyEntry("foo"), new ArrayEntry(2))));
        assertTrue(matcher.matches(asList(new KeyEntry("titles"), new KeyEntry("value"))));
        assertFalse(matcher.matches(asList(new KeyEntry("foo"), new KeyEntry("value"))));
        assertTrue(matcher.matches(asList(new KeyEntry("titles"), new ArrayEntry(2), new KeyEntry("value"))));
        assertFalse(matcher.matches(asList(new KeyEntry("titles"), new ArrayEntry(2), new KeyEntry("foo"))));
        assertFalse(matcher.matches(Collections.emptyList()));

        assertThat(matcher.toString()).isEqualTo("titles.value");
    }

    @Test
    public void emptyPath() {
        SinglePathMatcher matcher = new SinglePathMatcher(true,
                new PreciseMatch("titles"),
                new PreciseMatch("value"));
        assertFalse(matcher.matches(Collections.emptyList()));
    }

    @Test
    public void ignoreArraysNoMatch() {
        SinglePathMatcher singlePathMatcher = new SinglePathMatcher(true,
                new PreciseMatch("titles"),
                new PreciseMatch("value"));

        Path path = new Path();
        path.add(new KeyEntry("titles"));
        path.add(new ArrayEntry(1));

        assertFalse(singlePathMatcher.matches(path));
    }

    @Test
    public void ignoreArraysMatch() {
        SinglePathMatcher singlePathMatcher = new SinglePathMatcher(true,
                new PreciseMatch("titles"),
                new PreciseMatch("value"));

        Path path = new Path();
        path.add(new KeyEntry("titles"));
        path.add(new ArrayEntry(1));
        path.add(new KeyEntry("value"));

        assertTrue(singlePathMatcher.matches(path));
    }

	@Test
	public void needsKeyCollection() {
		PathMatcher matcher = new SinglePathMatcher(new PreciseMatch("c"), new ArrayEntryMatch());

		assertTrue(matcher.needsKeyCollection().test(new Path(new KeyEntry("c"), new ArrayEntry())));
		assertFalse(matcher.needsKeyCollection().test(new Path(new KeyEntry("c"))));
	}

    @Test
    public void testWithSkip() {

        PathMatcher matcher = new SinglePathMatcher(mock(KeysPattern.class), new ArrayEntryMatch());

    }

    @Test
    public void testAnyDepthMatcher() {
        SinglePathMatcher matcher = new SinglePathMatcher(false,
                new AnyDepthMatcher(),
                new PreciseMatch("value"));
        assertFalse(matcher.matches(asList(new KeyEntry("titles"), new ArrayEntry(2))));
        assertFalse(matcher.matches(asList(new KeyEntry("foo"), new ArrayEntry(2))));
        assertTrue(matcher.matches(asList(new KeyEntry("titles"), new KeyEntry("value"))));
        assertTrue(matcher.matches(asList(new KeyEntry("foo"), new KeyEntry("value"))));
        assertTrue(matcher.matches(asList(new KeyEntry("a"), new KeyEntry("b"), new KeyEntry("value"))));
        assertTrue(matcher.matches(asList(new KeyEntry("titles"), new ArrayEntry(2), new KeyEntry("value"))));
        assertFalse(matcher.matches(asList(new KeyEntry("titles"), new ArrayEntry(2), new KeyEntry("foo"))));
        assertFalse(matcher.matches(Collections.emptyList()));
    }
}
