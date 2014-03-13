package org.meeuw.json.grep;


import org.junit.Test;
import org.meeuw.json.ArrayEntry;
import org.meeuw.json.KeyEntry;
import org.meeuw.json.PathEntry;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayDeque;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GrepTest {

    //@Test
    public void grep() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GrepMain grep = new GrepMain(new Grep.SinglePathMatcher(
                new Grep.PreciseMatch("a"),
                new Grep.PreciseMatch("b"),
                new Grep.PreciseMatch("b1")), out);

        //new String[] {"c", "*", "d"});
        //Grep grep = new Grep(new String[] {"b", "b1"});

        grep.read(new StringReader("{a:1, b: {b1: 5}, c: [ {d: 4, e:5 }]}"));

    }


    @Test
    public void grepSubObject() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GrepMain grep = new GrepMain(new Grep.SinglePathMatcher(
                new Grep.PreciseMatch("b"),
                new Grep.PreciseMatch("b1")), out);


        grep.read(new StringReader("{b: {b1: 5}, c:5}"));
        assertEquals("b.b1=5\n", new String(out.toByteArray()));

    }

    @Test
    public void grepSubObject2() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GrepMain grep = new GrepMain(new Grep.SinglePathMatcher(
                new Grep.PreciseMatch("b")
        ), out);


        grep.read(new StringReader("{b: {b1: 5}, c:5}"));
        assertEquals("b={...}\n", new String(out.toByteArray()));

    }

    @Test
    public void grepArray() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GrepMain grep = new GrepMain(new Grep.SinglePathMatcher(
                new Grep.PreciseMatch("c"),
                new Grep.Wildcard(),
                new Grep.PreciseMatch("b2")), out);

        grep.read(new StringReader("{c: [{b1: 1}, {b2: 2}], d:3}"));
        assertEquals("c[1].b2=2\n", new String(out.toByteArray()));


    }


    @Test
    public void grepArray2() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GrepMain grep = new GrepMain(new Grep.SinglePathMatcher(
                new Grep.PreciseMatch("c")), out);

        grep.read(new StringReader("{c: [{b1: 1}, {b2: 2}], d:3}"));
        assertEquals("c=[...]\n", new String(out.toByteArray()));


    }

    @Test
    public void grepEmpty() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GrepMain grep = new GrepMain(new Grep.SinglePathMatcher(
                new Grep.PreciseMatch("c"),
                new Grep.Wildcard(),
                new Grep.PreciseMatch("b2")), out);

        grep.read(new StringReader("{c: [{b1: 1}, {b3: 2}]}"));
        assertEquals("", new String(out.toByteArray()));


    }


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

    }

    @Test
    public void grepPathIgnoreArray() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GrepMain grep = new GrepMain(new Grep.SinglePathMatcher(true,
                new Grep.PreciseMatch("titles"),
                new Grep.PreciseMatch("value")), out);
        grep.setOutputFormat(GrepMain.Output.VALUE);

        grep.read(new StringReader("{titles: [{value: 'title1'}, {value: 'title2'}]}"));
        assertEquals("title1\ntitle2\n", new String(out.toByteArray()));


    }


    @Test
    public void grepTitle() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GrepMain grep = new GrepMain(Grep.parsePathMatcherChain("titles.*.value", false), out);
        grep.setOutputFormat(GrepMain.Output.VALUE);
        grep.read(new StringReader("{titles: [{value: 'title1'}, {value: 'title2'}]}"));
        assertEquals("title1\ntitle2\n", new String(out.toByteArray()));


    }


}
