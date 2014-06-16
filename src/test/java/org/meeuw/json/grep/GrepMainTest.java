package org.meeuw.json.grep;


import org.junit.Test;
import org.meeuw.json.grep.matching.PreciseMatch;
import org.meeuw.json.grep.matching.SinglePathMatcher;
import org.meeuw.json.grep.matching.Wildcard;
import org.meeuw.json.grep.parsing.Parser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;

import static org.junit.Assert.assertEquals;

public class GrepMainTest {


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
