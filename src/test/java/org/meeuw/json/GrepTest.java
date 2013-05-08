package org.meeuw.json;


import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;

import static org.junit.Assert.assertEquals;

public class GrepTest {

    //@Test
    public void grep() throws IOException {
        Grep grep = new Grep(new Grep.SinglePathMatcher(
                new Grep.PreciseMatch("a"),
                new Grep.PreciseMatch("b"),
                new Grep.PreciseMatch("b1")), System.out);
        //new String[] {"c", "*", "d"});
        //Grep grep = new Grep(new String[] {"b", "b1"});

        grep.read(new StringReader("{a:1, b: {b1: 5}, c: [ {d: 4, e:5 }]}"));

    }


    @Test
    public void grepSubObject() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Grep grep = new Grep(new Grep.SinglePathMatcher(
                new Grep.PreciseMatch("b"),
                new Grep.PreciseMatch("b1")), out);


        grep.read(new StringReader("{b: {b1: 5}, c:5}"));
        assertEquals("b.b1=5\n", new String(out.toByteArray()));

    }

    @Test
    public void grepArray() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Grep grep = new Grep(new Grep.SinglePathMatcher(
                new Grep.PreciseMatch("c"),
                new Grep.Wildcard(),
                new Grep.PreciseMatch("b2")), out);

        grep.read(new StringReader("{c: [{b1: 1}, {b2: 2}]}"));
        assertEquals("c[1].b2=2\n", new String(out.toByteArray()));


    }
}
