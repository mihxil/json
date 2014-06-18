package org.meeuw.json.grep;


import org.junit.Test;
import org.meeuw.json.Util;
import org.meeuw.json.grep.matching.*;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class GrepTest {

    @Test
    public void grepSubObject() throws IOException {
        PathMatcher matcher = new SinglePathMatcher(
                new PreciseMatch("b"),
                new PreciseMatch("b1"));
        Grep grep = new Grep(matcher, Util.getJsonParser("{a:1, b: {b1: 5}, c: [ {d: 4, e:5 }]}"));
        assertEquals("b.b1=5", grep.next().toString());
        assertFalse(grep.hasNext());

    }


    @Test
    public void grepSubObject2() throws IOException {
        Grep grep = new Grep(new SinglePathMatcher(
                new PreciseMatch("b")
        ), Util.getJsonParser("{b: {b1: 5}, c:5}"));


        assertEquals("b={...}", grep.next().toString());
        assertFalse(grep.hasNext());
    }

    @Test
    public void grepSubObject3() throws IOException {
        Grep grep = new Grep(new SinglePathMatcher(
                new PreciseMatch("b")
        ), Util.getJsonParser("{b: {b1: 5}}"));

        assertEquals("b={...}", grep.next().toString());
        assertFalse(grep.hasNext());
    }

    @Test
    public void grepValue() throws IOException {
        Grep grep = new Grep(new SinglePathMatcher(
                new PreciseMatch("c")
        ), Util.getJsonParser("{b: {b1: 5}, c:5}"));

        assertEquals("c=5", grep.next().toString());
        assertFalse(grep.hasNext());
    }

    @Test
    public void grepArray() throws IOException {
        Grep grep = new Grep(new SinglePathMatcher(
                new PreciseMatch("c"),
                new Wildcard(),
                new PreciseMatch("b2")), Util.getJsonParser("{c: [{b1: 1}, {b2: 2}], d:3}"));

        assertEquals("c[1].b2=2", grep.next().toString());
        assertFalse(grep.hasNext());
    }


    @Test
    public void grepArray2() throws IOException {
        Grep grep = new Grep(new SinglePathMatcher(
                new PreciseMatch("c"),
                new ArrayIndexMatch(0)), Util.getJsonParser("{c: ['een']}"));

        assertEquals("c[0]=een", grep.next().toString());
        assertFalse(grep.hasNext());
    }


    @Test
    public void grepArrayResult() throws IOException {
        Grep grep = new Grep(new SinglePathMatcher(
                new PreciseMatch("c")), Util.getJsonParser("{c: [{b1: 1}, {b2: 2}], d:3}"));

        assertEquals("c=[...]", grep.next().toString());
        assertFalse(grep.hasNext());
    }

    @Test
    public void grepEmpty() throws IOException {
        Grep grep = new Grep(new SinglePathMatcher(
                new PreciseMatch("c"),
                new Wildcard(),
                new PreciseMatch("b2")), Util.getJsonParser("{c: [{b1: 1}, {b3: 2}]}"));

        assertFalse(grep.hasNext());
    }


    @Test
    public void grepKeyMatchNoMatch() throws IOException {
        Grep grep = new Grep(new ObjectHasKeyMatcher("a"), Util.getJsonParser("{c: [{b1: 1}, {b3: 2}]}"));

        assertFalse(grep.hasNext());
    }


    @Test
    public void grepKeyMatch() throws IOException {
        Grep grep = new Grep(new ObjectHasKeyMatcher("b1"), Util.getJsonParser("{c: [{b1: 1}, {b3: 2}]}"));
        assertEquals("c[0]={...}", grep.next().toString());
        assertFalse(grep.hasNext());
    }


    @Test
    public void grepNoKeyMatch() throws IOException {
        Grep grep = new Grep(new ObjectMatcherNot(new ObjectHasKeyMatcher("b2")), Util.getJsonParser("{c: [{b1: 1}, {b3: 2}]}"));
        assertEquals("c[0]={...}", grep.next().toString());
		assertEquals("c[1]={...}", grep.next().toString());
		assertEquals("={...}", grep.next().toString());
		assertFalse(grep.hasNext());
    }


}
