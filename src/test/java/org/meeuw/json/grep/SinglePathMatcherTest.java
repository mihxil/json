package org.meeuw.json.grep;

import org.junit.Test;
import org.meeuw.json.ArrayEntry;
import org.meeuw.json.KeyEntry;
import org.meeuw.json.Path;
import org.meeuw.json.PathEntry;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


/**
 * @author Michiel Meeuwissen
 * @since 0.4
 */
public class SinglePathMatcherTest {

    @Test
    public void grepSinglePathPatcherTest() throws IOException {
        Grep.SinglePathMatcher matcher = new Grep.SinglePathMatcher(true,
                new Grep.PreciseMatch("titles"),
                new Grep.PreciseMatch("value"));
        assertFalse(matcher.matches(new ArrayDeque<PathEntry>(Arrays.asList(new KeyEntry("titles"), new ArrayEntry(2)))));
        assertFalse(matcher.matches(new ArrayDeque<PathEntry>(Arrays.asList(new KeyEntry("foo"), new ArrayEntry(2)))));
        assertTrue(matcher.matches(new ArrayDeque<PathEntry>(Arrays.asList(new KeyEntry("titles"), new KeyEntry("value")))));
        assertFalse(matcher.matches(new ArrayDeque<PathEntry>(Arrays.asList(new KeyEntry("foo"), new KeyEntry("value")))));
        assertTrue(matcher.matches(new ArrayDeque<PathEntry>(Arrays.asList(new KeyEntry("titles"), new ArrayEntry(2), new KeyEntry("value")))));
        assertFalse(matcher.matches(new ArrayDeque<PathEntry>(Arrays.asList(new KeyEntry("titles"), new ArrayEntry(2), new KeyEntry("foo")))));
        assertFalse(matcher.matches(new ArrayDeque<PathEntry>()));


    }

    @Test
    public void emptyPath() throws IOException {
        Grep.SinglePathMatcher matcher = new Grep.SinglePathMatcher(true,
                new Grep.PreciseMatch("titles"),
                new Grep.PreciseMatch("value"));
        assertFalse(matcher.matches(new ArrayDeque<PathEntry>()));


    }

    @Test
    public void ignoreArraysNoMatch() {
        Grep.SinglePathMatcher singlePathMatcher = new Grep.SinglePathMatcher(true,
                new Grep.PreciseMatch("titles"),
                new Grep.PreciseMatch("value"));

        Deque<PathEntry> path = new Path();
        path.add(new KeyEntry("titles"));
        path.add(new ArrayEntry(1));

        assertFalse(singlePathMatcher.matches(path));
    }

    @Test
    public void ignoreArraysMatch() {
        Grep.SinglePathMatcher singlePathMatcher = new Grep.SinglePathMatcher(true,
                new Grep.PreciseMatch("titles"),
                new Grep.PreciseMatch("value"));

        Deque<PathEntry> path = new Path();
        path.add(new KeyEntry("titles"));
        path.add(new ArrayEntry(1));
        path.add(new KeyEntry("value"));

        assertTrue(singlePathMatcher.matches(path));
    }
}
