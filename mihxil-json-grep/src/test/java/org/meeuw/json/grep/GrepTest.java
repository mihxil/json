package org.meeuw.json.grep;


import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.junit.jupiter.api.Test;

import org.meeuw.json.Util;
import org.meeuw.json.grep.matching.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class GrepTest {

    @Test
    public void grepSubObject() throws IOException {
        PathMatcher matcher = new SinglePathMatcher(
                new PreciseMatch("b"),
                new PreciseMatch("b1"));
        Grep grep = new Grep(matcher, Util.getJsonParser("{a:1, b: {b1: 5}, c: [ {d: 4, e:5 }]}"));
        assertEquals("b.b1=5", grep.next().toString());
        assertFalse(grep.hasNext());
        assertThatThrownBy(grep::next).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    public void grepSubObjectWithMapper() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        JsonNode node = mapper.reader().readTree("{'a':1, 'b': {'b1': 5}, 'c': [ {'d': 4, 'e':5 }]}");
        PathMatcher matcher = new SinglePathMatcher(
            new PreciseMatch("b"),
            new PreciseMatch("b1"));
        // grepping directly in a JsonNode (rather than on 'node.traverse()') gives back events
        // whose node is the actual, live node in the tree, not a disconnected copy.
        Grep grep = new Grep(matcher, node);
        GrepEvent event = grep.next();
        assertEquals("b.b1=5", event.toString());
        assertFalse(grep.hasNext());
        assertThatThrownBy(grep::next).isInstanceOf(NoSuchElementException.class);

        assertThat(event.getEvent().getNode()).isSameAs(node.get("b").get("b1"));
    }

    @Test
    public void grepSubObjectWithMapperCanUpdate() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        JsonNode node = mapper.reader().readTree("{'a':1, 'b': {'b1': 5}, 'c': [ {'d': 4, 'e':5 }]}");
        Grep grep = new Grep(new SinglePathMatcher(new PreciseMatch("b")), node);

        GrepEvent event = grep.next();
        assertEquals("b={...}", event.toString());
        assertFalse(grep.hasNext());

        // the matched node is 'node's own live "b" object, so mutating it updates 'node' itself
        ObjectNode b = (ObjectNode) event.getEvent().getNode();
        b.put("b1", 6);
        assertEquals(6, node.get("b").get("b1").asInt());
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
        Grep grep = new Grep(new ObjectMatcherNot(
                new ObjectHasKeyMatcher("b2")), Util.getJsonParser("{c: [{b1: 1}, {b3: 2}]}"));
        assertEquals("c[0]={...}", grep.next().toString());
		assertEquals("c[1]={...}", grep.next().toString());
		assertEquals("={...}", grep.next().toString());
		assertFalse(grep.hasNext());
    }


	@Test
	public void grepKeyNoKeyMatch() throws IOException {
		Grep grep = new Grep(new PathMatcherAndChain(
                new SinglePathMatcher(new PreciseMatch("c"), new ArrayEntryMatch()),
                new ObjectMatcherNot(new ObjectHasKeyMatcher("b1"))), Util.getJsonParser("{c: [{b1: 1}, {b3: 2}]}"));
		assertEquals("c[1]={...}", grep.next().toString());
		assertFalse(grep.hasNext());
	}

    @Test
    public void grepJavascriptMatch() throws IOException {
        Grep grep = new Grep(new PathMatcherAndChain(
                new SinglePathMatcher(new PreciseMatch("c")),
                new JavascriptMatcher("function(doc) {return doc.b1 == 1;}")),
                Util.getJsonParser("{c: {b1: 1, b3: 2}}"));
        assertEquals("c={...}", grep.next().toString());
        assertFalse(grep.hasNext());
    }

    @Test
    public void grepJavascriptNotMatch() throws IOException {
        Grep grep = new Grep(new PathMatcherAndChain(
                new SinglePathMatcher(new PreciseMatch("c")),
                new JavascriptMatcher("function(doc) {return doc.b1 == 2;}")),
                Util.getJsonParser("{c: {b1: 1, b3: 2}}"));
        assertFalse(grep.hasNext());
    }


    @Test
    public void grepJavascriptArrayMatch() throws IOException {
        Grep grep = new Grep(new PathMatcherAndChain(
                new SinglePathMatcher(new PreciseMatch("c"), new ArrayEntryMatch()),
                new JavascriptMatcher("function(doc) {return doc.b1 == 1;}")),
                Util.getJsonParser("{c: [{b1: 1, b3: {}}, {b1: 2, b3: {}}]}"));
        assertEquals("c[0]={...}", grep.next().toString());
        assertFalse(grep.hasNext());
    }

    @Test
    public void grepArrayMatch() throws IOException {
        Grep grep = new Grep(
                new SinglePathMatcher(
                        new PreciseMatch("items"),
                        new ArrayEntryMatch()),
                Util.getJsonParser("{ \"items\" : [ { \"a\" : [], \"result\" :\"value\" }]}\n"));
        assertEquals("items[0]={...}", grep.next().toString());
        assertFalse(grep.hasNext());
    }

    @Test
    public void nonpe() throws IOException {
        Grep grep = new Grep(
                new SinglePathMatcher(
                    new PreciseMatch("items"),
                    new ArrayEntryMatch(),
                    new PreciseMatch("result")),
            Util.getJsonParser("{\"items\" : [{ \"result\" : {\"a\" : {}, \"b\" : 1 }} ]}"));
        assertEquals("items[0].result={...}", grep.next().toString());
    }

    @Test
    public void recordMatcher() throws IOException {
        Grep grep = new Grep(
                null,
                Util.getJsonParser("{\"items\" : [5, 6]}")
        );
        grep.setRecordMatcher(new SinglePathMatcher(new PreciseMatch("items"), new ArrayEntryMatch()));
        assertEquals(GrepEvent.Type.RECORD, grep.next().getType());
        assertEquals(GrepEvent.Type.RECORD, grep.next().getType());
        assertFalse(grep.hasNext());
    }


    @Test
    public void grepFromArray() throws IOException {
        Grep grep = new Grep(
            new PathMatcherAndChain(
                new SinglePathMatcher(
                    new ArrayEntryMatch(),
                    new PreciseMatch("a"))
            ),
            Util.getJsonParser(
                "[ { \"a\" : \"b\"}, { \"a\" : \"c\"} ]"));

        grep.setRecordMatcher(new SinglePathMatcher(new ArrayEntryMatch()));
        assertEquals("[0].a=b", grep.next().toString());
        assertThat(grep.hasNext()).isTrue();
        assertEquals("[0]={...}", grep.next().toString());
        //assertThat(grep.hasNext()).isFalse();

        assertThat(grep.toString()).isEqualTo("Grep[matcher=[*].a, recordMatcher=[*]]");
    }

    @Test
    public void valueRegexp() throws IOException {
        Grep grep = new Grep(
            new PathMatcherAndChain(
                new SinglePathMatcher(true,
                    new PreciseMatch("items"),
                    new PreciseMatch("a")
                ),
                new ScalarRegexpMatcher(Pattern.compile("abc\\s*(.*)"), "$1")),
            Util.getJsonParser("{ \"items\" : [ { \"a\" : 'abc def'},  { \"a\" : 'xyz qwv'}]}"));

        assertEquals("items[0].a=def", grep.next().toString());
        assertFalse(grep.hasNext());
    }


}
