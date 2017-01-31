package org.meeuw.json.grep;



import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;

import org.junit.Test;
import org.meeuw.json.grep.matching.*;
import org.meeuw.json.grep.parsing.Parser;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GrepMainTest {


    @Test
    public void grepEmpty() throws IOException {
        GrepMain grep = new GrepMain(new SinglePathMatcher(
                new PreciseMatch("c"),
                new Wildcard(),
                new PreciseMatch("b2")));

        String result = grep.read(new StringReader("{c: [{b1: 1}, {b3: 2}]}"));

        assertEquals("", result);
        assertEquals(0L, grep.getPreviousMaxRecordSize().longValue());
    }




    @Test
    public void grepPathIgnoreArray() throws IOException {
        GrepMain grep = new GrepMain(new SinglePathMatcher(true,
                new PreciseMatch("titles"),
                new PreciseMatch("value")));
        grep.setOutputFormat(GrepMain.Output.VALUE);


        String result = grep.read(new StringReader("{titles: [{value: 'title1'}, {value: 'title2'}]}"));
        assertEquals("title1\ntitle2\n", result);
        assertEquals(1L, grep.getPreviousMaxRecordSize().longValue());
    }


    @Test
    public void grepPathIgnoreArray2() throws IOException {
        GrepMain grep = new GrepMain(new SinglePathMatcher(true,
                new PreciseMatch("titles"),
                new PreciseMatch("value")));
        grep.setOutputFormat(GrepMain.Output.VALUE);

        String result = grep.read(new StringReader("{}"));
        assertEquals("", result);
    }

	@Test
	public void grepTitle() throws IOException {
		GrepMain grep = new GrepMain(Parser.parsePathMatcherChain("titles.*.value"));
		grep.setOutputFormat(GrepMain.Output.VALUE);

		String result = grep.read(new StringReader("{titles: [{value: 'title1'}, {value: 'title2'}]}"));
		assertEquals("title1\ntitle2\n", result);


	}


    @Test
    public void grepOutputFullValue() throws IOException {
        GrepMain grep = new GrepMain(Parser.parsePathMatcherChain("titles[0]"));
        grep.setOutputFormat(GrepMain.Output.FULLVALUE);

        String result = grep.read(new StringReader("{titles: [{value: 'title1'}, {value: 'title2'}]}"));
        assertEquals("{\"value\":\"title1\"}\n", result);
    }


    @Test
    public void grepOutputFullArray() throws IOException {
        GrepMain grep = new GrepMain(Parser.parsePathMatcherChain("titles"));
        grep.setOutputFormat(GrepMain.Output.FULLVALUE);

        String result = grep.read(new StringReader("{titles: [{value: 'title1'}, {value: 'title2'}]}"));
        assertEquals("[{\"value\":\"title1\"},{\"value\":\"title2\"}]\n", result);
    }

    @Test
    public void grepOutputFullValueMultiple() throws IOException {
        GrepMain grep = new GrepMain(Parser.parsePathMatcherChain("titles[0],titles"));
        grep.setOutputFormat(GrepMain.Output.FULLVALUE);

        String result = grep.read(new StringReader("{titles: [{value: 'title1'}, {value: 'title2'}]}"));
        assertEquals("{\"value\":\"title1\"}\n" +
                "[{\"value\":\"title1\"},{\"value\":\"title2\"}]\n", result);
    }



    @Test
	public void grepNotContainsKey() throws IOException {
		GrepMain grep = new GrepMain(Parser.parsePathMatcherChain("titles.* ! contains a"));
		grep.setOutputFormat(GrepMain.Output.PATHANDVALUE);

		String result = grep.read(new StringReader("{titles: [{a: 'A'}, {b: 'B'}]}"));
		assertEquals("titles[1]={...}\n", result);


	}


    @Test
    public void grepArrays() throws IOException {
        GrepMain grep = new GrepMain(Parser.parsePathMatcherChain("titles.[*]"));
        grep.setOutputFormat(GrepMain.Output.PATHANDVALUE);

        String result = grep.read(new StringReader("{titles: [{a: 'A'}, {b: 'B'}]}"));
        assertEquals("titles[0]={...}\n" +
            "titles[1]={...}\n", result);
    }

    @Test
    public void grepArrays2() throws IOException {
        GrepMain grep = new GrepMain(Parser.parsePathMatcherChain("items.[*]"));
        grep.setOutputFormat(GrepMain.Output.PATHANDVALUE);

        String result = grep.read(getClass().getResourceAsStream("/items.json"));
        assertEquals(
                "items[0]={...}\n" +
                "items[1]={...}\n", result);
    }


    @Test
    public void grepArrays3() throws IOException {
        GrepMain grep = new GrepMain(Parser.parsePathMatcherChain("items[*]"));

        String result = grep.read(getClass().getResourceAsStream("/big.json"));
        assertEquals(
                "items[0]={...}\n" +
                "items[1]={...}\n" +
                "items[2]={...}\n" +
                "items[3]={...}\n" +
                "items[4]={...}\n" +
                "items[5]={...}\n" +
                "items[6]={...}\n" +
                "items[7]={...}\n",
                result);

    }


    @Test
    public void grepJavascript1() throws IOException {
        GrepMain grep = new GrepMain(Parser.parsePathMatcherChain("c function(doc) { return doc.b1 == 1}"));
        PathMatcherAndChain matcher = (PathMatcherAndChain) grep.getMatcher();
        assertTrue(matcher.getPatterns()[0] instanceof SinglePathMatcher);
        assertEquals("c", matcher.getPatterns()[0].toString());
        assertTrue(matcher.getPatterns()[1] instanceof JavascriptMatcher);
        assertEquals("function(doc) { return doc.b1 == 1}", matcher.getPatterns()[1].toString());

        String result = grep.read(new StringReader("{c: {b1: 1, b3: 2}}"));

        assertEquals("c={...}\n", result);
    }

    @Test
    public void grepJavascript() throws IOException {
        GrepMain grep = new GrepMain(Parser.parsePathMatcherChain("items[*] function(doc) { return doc.score < 1.9;}"));

        String result = grep.read(getClass().getResourceAsStream("/big.json"));

        assertEquals(
                "items[4]={...}\n" +
                "items[5]={...}\n" +
                "items[6]={...}\n" +
                "items[7]={...}\n", result);
    }

    @Test // Tests NeedsObjectObjectMatcher...
    public void grepContains() throws IOException {
        GrepMain.Output output = GrepMain.Output.FULLVALUE;
        GrepMain grep = new GrepMain(Parser.parsePathMatcherChain("...arr[*] contains d", false, output.needsObject(), null));
        grep.setOutputFormat(output);

        String result = grep.read(new StringReader("{a:'b', y: {c:'x', arr:[{d:'y'}, {e:'z'}]}}"));

        assertEquals("{\"d\":\"y\"}\n", result);
    }

    @Test
    public void grepRecordMatcherNoSortFields() throws IOException {
        GrepMain grep = new GrepMain(Parser.parsePathMatcherChain("items[*].result.urn,items[*].result.mid"));
        grep.setRecordMatcher(Parser.parsePathMatcherChain("items[*].result"));
        grep.setSep("\t");
        grep.setOutputFormat(GrepMain.Output.PATHANDVALUE);
        grep.setSortFields(false);

        Iterator<GrepMainRecord> i = grep.iterate(getClass().getResourceAsStream("/big.json"));

        GrepMainRecord record = i.next();
        assertThat(record.toString(),
                equalTo("items[0].result.mid=WO_VPRO_422287\titems[0].result.urn=urn:vpro:media:program:31357524"));



    }

    @Test
    public void grepRecordMatcherSortFields() throws IOException {
        GrepMain grep = new GrepMain(Parser.parsePathMatcherChain("items[*].result.mid,items[*].result.urn"));
        grep.setRecordMatcher(Parser.parsePathMatcherChain("items[*].result"));
        grep.setSep("\t");
        grep.setOutputFormat(GrepMain.Output.VALUE);

        Iterator<GrepMainRecord> i = grep.iterate(getClass().getResourceAsStream("/big.json"));

        assertThat(i.next().toString(),
                equalTo("WO_VPRO_422287\turn:vpro:media:program:31357524"));
        assertThat(i.next().toString(),
                equalTo("WO_VPRO_422145\turn:vpro:media:program:31296444"));
        assertThat(i.next().toString(),
                equalTo("WO_VPRO_400672\turn:vpro:media:program:29384619"));
        assertThat(i.next().toString(),
                equalTo("WO_VPRO_373471\turn:vpro:media:program:26962067"));
        assertThat(i.next().toString(),
                equalTo("WO_VPRO_367975\turn:vpro:media:program:26707976"));
        assertThat(i.next().toString(),
                equalTo("WO_VPRO_337772\turn:vpro:media:program:24115794"));
        assertThat(i.next().toString(),
                equalTo("WO_VPRO_134633\turn:vpro:media:program:22111825"));
        assertThat(i.next().toString(),
                equalTo("WO_VPRO_075054\turn:vpro:media:program:17702841"));
        assertThat(i.hasNext(), equalTo(false));


    }


    @Test
    public void grepMax() throws IOException {
        GrepMain grep = new GrepMain(Parser.parsePathMatcherChain("items[*].result.mid,items[*].result.urn"));
        grep.setRecordMatcher(Parser.parsePathMatcherChain("items[*].result"));
        grep.setMax(4L);

        Iterator<GrepMainRecord> i = grep.iterate(getClass().getResourceAsStream("/big.json"));
        i.next();
        i.next();
        i.next();
        i.next();
        assertThat(i.hasNext(), equalTo(false));

    }


    @Test
    public void grepRegex() throws IOException {
        GrepMain grep = new GrepMain(Parser.parsePathMatcherChain("items[*].result./(mid|urn)/"));
        grep.setRecordMatcher(Parser.parsePathMatcherChain("items[*].result"));
        grep.setMax(4L);

        Iterator<GrepMainRecord> i = grep.iterate(getClass().getResourceAsStream("/big.json"));
        i.next();
        i.next();
        i.next();
        i.next();
        assertThat(i.hasNext(), equalTo(false));

    }


}
