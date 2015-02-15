package org.meeuw.json.grep;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;

import org.junit.Ignore;
import org.junit.Test;
import org.meeuw.json.grep.matching.*;
import org.meeuw.json.grep.parsing.Parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
		GrepMain grep = new GrepMain(Parser.parsePathMatcherChain("titles.*.value"), out);
		grep.setOutputFormat(GrepMain.Output.VALUE);
		grep.read(new StringReader("{titles: [{value: 'title1'}, {value: 'title2'}]}"));
		assertEquals("title1\ntitle2\n", new String(out.toByteArray()));


	}


    @Test
    public void grepOutputFullValue() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GrepMain grep = new GrepMain(Parser.parsePathMatcherChain("titles[0]"), out);
        grep.setOutputFormat(GrepMain.Output.FULLVALUE);
        grep.read(new StringReader("{titles: [{value: 'title1'}, {value: 'title2'}]}"));
        assertEquals("{\"value\":\"title1\"}\n", new String(out.toByteArray()));
    }


    @Test
    public void grepOutputFullArray() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GrepMain grep = new GrepMain(Parser.parsePathMatcherChain("titles"), out);
        grep.setOutputFormat(GrepMain.Output.FULLVALUE);
        grep.read(new StringReader("{titles: [{value: 'title1'}, {value: 'title2'}]}"));
        assertEquals("[{\"value\":\"title1\"},{\"value\":\"title2\"}]\n", new String(out.toByteArray()));
    }


    @Test
	public void grepNotContainsKey() throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		GrepMain grep = new GrepMain(Parser.parsePathMatcherChain("titles.* ! contains a"), out);
		grep.setOutputFormat(GrepMain.Output.PATHANDVALUE);
		grep.read(new StringReader("{titles: [{a: 'A'}, {b: 'B'}]}"));
		assertEquals("titles[1]={...}\n", new String(out.toByteArray()));


	}


    @Test
    public void grepArrays() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GrepMain grep = new GrepMain(Parser.parsePathMatcherChain("titles.[*]"), out);
        grep.setOutputFormat(GrepMain.Output.PATHANDVALUE);
        grep.read(new StringReader("{titles: [{a: 'A'}, {b: 'B'}]}"));
        assertEquals("titles[0]={...}\n" +
            "titles[1]={...}\n", new String(out.toByteArray()));


    }

    @Test
    public void grepArrays2() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GrepMain grep = new GrepMain(Parser.parsePathMatcherChain("items.[*]"), out);
        grep.setOutputFormat(GrepMain.Output.PATHANDVALUE);
        grep.read(getClass().getResourceAsStream("/items.json"));
        assertEquals("items[0]={...}\n" +
            "items[1]={...}\n", new String(out.toByteArray()));
    }


    @Test
    public void grepArrays3() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GrepMain grep = new GrepMain(Parser.parsePathMatcherChain("items[*]"), out);
        grep.read(getClass().getResourceAsStream("/big.json"));

        assertEquals("items[0]={...}\n" +
            "items[1]={...}\n" +
            "items[2]={...}\n" +
            "items[3]={...}\n" +
            "items[4]={...}\n" +
            "items[5]={...}\n" +
            "items[6]={...}\n" +
            "items[7]={...}\n", new String(out.toByteArray()));
    }


    @Test
    public void grepJavascript1() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GrepMain grep = new GrepMain(Parser.parsePathMatcherChain("c function(doc) { return doc.b1 == 1}"),
                out);
        PathMatcherAndChain matcher = (PathMatcherAndChain) grep.getMatcher();
        assertTrue(matcher.getPatterns()[0] instanceof SinglePathMatcher);
        assertEquals("c", matcher.getPatterns()[0].toString());
        assertTrue(matcher.getPatterns()[1] instanceof JavascriptMatcher);
        assertEquals("function(doc) { return doc.b1 == 1}", matcher.getPatterns()[1].toString());
        grep.read(new StringReader("{c: {b1: 1, b3: 2}}"));

        assertEquals("c={...}\n", new String(out.toByteArray()));
    }

    @Test
    public void grepJavascript() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GrepMain grep = new GrepMain(Parser.parsePathMatcherChain("items[*] function(doc) { return doc.score < 1.9;}"),
                out);
        grep.read(getClass().getResourceAsStream("/big.json"));

        assertEquals(
                "items[4]={...}\n" +
                "items[5]={...}\n" +
                "items[6]={...}\n" +
                "items[7]={...}\n", new String(out.toByteArray()));
    }

}
