package org.meeuw.json.grep.matching;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;
import org.meeuw.json.ArrayEntry;
import org.meeuw.json.KeyEntry;
import org.meeuw.json.Path;
import org.meeuw.json.PathEntry;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;


/**
 * @author Michiel Meeuwissen
 * @since 0.4
 */
public class SinglePathMatcherTest {

    @Test
    public void grepSinglePathPatcherTest() throws IOException {
        SinglePathMatcher matcher = new SinglePathMatcher(true,
                new PreciseMatch("titles"),
                new PreciseMatch("value"));
        assertFalse(matcher.matches(Arrays.asList(new KeyEntry("titles"), new ArrayEntry(2))));
        assertFalse(matcher.matches(Arrays.asList(new KeyEntry("foo"), new ArrayEntry(2))));
        assertTrue(matcher.matches(Arrays.<PathEntry>asList(new KeyEntry("titles"), new KeyEntry("value"))));
        assertFalse(matcher.matches(Arrays.<PathEntry>asList(new KeyEntry("foo"), new KeyEntry("value"))));
        assertTrue(matcher.matches(Arrays.asList(new KeyEntry("titles"), new ArrayEntry(2), new KeyEntry("value"))));
        assertFalse(matcher.matches(Arrays.asList(new KeyEntry("titles"), new ArrayEntry(2), new KeyEntry("foo"))));
        assertFalse(matcher.matches(Collections.<PathEntry>emptyList()));


    }

    @Test
    public void emptyPath() throws IOException {
        SinglePathMatcher matcher = new SinglePathMatcher(true,
                new PreciseMatch("titles"),
                new PreciseMatch("value"));
        assertFalse(matcher.matches(Collections.<PathEntry>emptyList()));
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
    public void testAnyDepthMatcher() throws IOException {
        SinglePathMatcher matcher = new SinglePathMatcher(false,
                new AnyDepthMatcher(),
                new PreciseMatch("value"));
        assertFalse(matcher.matches(Arrays.asList(new KeyEntry("titles"), new ArrayEntry(2))));
        assertFalse(matcher.matches(Arrays.asList(new KeyEntry("foo"), new ArrayEntry(2))));
        assertTrue(matcher.matches(Arrays.<PathEntry>asList(new KeyEntry("titles"), new KeyEntry("value"))));
        assertTrue(matcher.matches(Arrays.<PathEntry>asList(new KeyEntry("foo"), new KeyEntry("value"))));
        assertTrue(matcher.matches(Arrays.<PathEntry>asList(new KeyEntry("a"), new KeyEntry("b"), new KeyEntry("value"))));
        assertTrue(matcher.matches(Arrays.asList(new KeyEntry("titles"), new ArrayEntry(2), new KeyEntry("value"))));
        assertFalse(matcher.matches(Arrays.asList(new KeyEntry("titles"), new ArrayEntry(2), new KeyEntry("foo"))));
        assertFalse(matcher.matches(Collections.<PathEntry>emptyList()));



    }
}
