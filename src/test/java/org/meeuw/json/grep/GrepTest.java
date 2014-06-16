package org.meeuw.json.grep;


import org.junit.Test;
import org.meeuw.json.grep.matching.*;
import org.meeuw.json.grep.parsing.Parser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;

import static org.junit.Assert.assertEquals;

public class GrepTest {

    @Test
    public void grep() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GrepMain grep = new GrepMain(new SinglePathMatcher(
                new PreciseMatch("a"),
                new PreciseMatch("b"),
                new PreciseMatch("b1")), out);

        //new String[] {"c", "*", "d"});
        //Grep grep = new Grep(new String[] {"b", "b1"});

        grep.read(new StringReader("{a:1, b: {b1: 5}, c: [ {d: 4, e:5 }]}"));

    }


    @Test
    public void grepSubObject() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GrepMain grep = new GrepMain(new SinglePathMatcher(
                new PreciseMatch("b"),
                new PreciseMatch("b1")), out);


        grep.read(new StringReader("{b: {b1: 5}, c:5}"));
        assertEquals("b.b1=5\n", new String(out.toByteArray()));

    }

    @Test
    public void grepSubObject2() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GrepMain grep = new GrepMain(new SinglePathMatcher(
                new PreciseMatch("b")
        ), out);


        grep.read(new StringReader("{b: {b1: 5}, c:5}"));
        assertEquals("b={...}\n", new String(out.toByteArray()));

    }

    @Test
    public void grepSubObject3() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GrepMain grep = new GrepMain(new SinglePathMatcher(
                new PreciseMatch("b")
        ), out);


        grep.read(new StringReader("{b: {b1: 5}}"));
        assertEquals("b={...}\n", new String(out.toByteArray()));

    }

    @Test
    public void grepValue() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GrepMain grep = new GrepMain(new SinglePathMatcher(
                new PreciseMatch("c")
        ), out);


        grep.read(new StringReader("{b: {b1: 5}, c:5}"));
        assertEquals("c=5\n", new String(out.toByteArray()));

    }

    @Test
    public void grepArray() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GrepMain grep = new GrepMain(new SinglePathMatcher(
                new PreciseMatch("c"),
                new Wildcard(),
                new PreciseMatch("b2")), out);

        grep.read(new StringReader("{c: [{b1: 1}, {b2: 2}], d:3}"));
        assertEquals("c[1].b2=2\n", new String(out.toByteArray()));


    }


    @Test
    public void grepArray2() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GrepMain grep = new GrepMain(new SinglePathMatcher(
                new PreciseMatch("c"),
                new ArrayIndexMatch(0)), out);

        grep.read(new StringReader("{c: ['een']}"));
        assertEquals("c[0]=een\n", new String(out.toByteArray()));


    }


    @Test
    public void grepArrayResult() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GrepMain grep = new GrepMain(new SinglePathMatcher(
                new PreciseMatch("c")), out);

        grep.read(new StringReader("{c: [{b1: 1}, {b2: 2}], d:3}"));
        assertEquals("c=[...]\n", new String(out.toByteArray()));


    }

    @Test
    public void grepEmpty() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GrepMain grep = new GrepMain(new SinglePathMatcher(
                new PreciseMatch("c"),
                new Wildcard(),
                new PreciseMatch("b2")), out);

        grep.read(new StringReader("{c: [{b1: 1}, {b3: 2}]}"));
        assertEquals("", new String(out.toByteArray()));


    }




    @Test
    public void grepPathIgnoreArray() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GrepMain grep = new GrepMain(new SinglePathMatcher(true,
                new PreciseMatch("titles"),
                new PreciseMatch("value")), out);
        grep.setOutputFormat(GrepMain.Output.VALUE);

        grep.read(new StringReader("{titles: [{value: 'title1'}, {value: 'title2'}]}"));
        assertEquals("title1\ntitle2\n", new String(out.toByteArray()));


    }


    @Test
    public void grepPathIgnoreArray2() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GrepMain grep = new GrepMain(new SinglePathMatcher(true,
                new PreciseMatch("titles"),
                new PreciseMatch("value")), out);
        grep.setOutputFormat(GrepMain.Output.VALUE);

        grep.read(new StringReader("{}"));
        assertEquals("", new String(out.toByteArray()));


    }


    @Test
    public void grepTitle() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GrepMain grep = new GrepMain(Parser.parsePathMatcherChain("titles.*.value", false), out);
        grep.setOutputFormat(GrepMain.Output.VALUE);
        grep.read(new StringReader("{titles: [{value: 'title1'}, {value: 'title2'}]}"));
        assertEquals("title1\ntitle2\n", new String(out.toByteArray()));


    }




}
