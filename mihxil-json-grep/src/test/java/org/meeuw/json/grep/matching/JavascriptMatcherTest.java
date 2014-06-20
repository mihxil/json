package org.meeuw.json.grep.matching;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.meeuw.json.ParseEvent;
import org.meeuw.json.Path;

import com.fasterxml.jackson.core.JsonToken;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class JavascriptMatcherTest {

    @Test
    public void testMatches() throws Exception {
        Map<String, Object> node = new HashMap<String, Object>();
        node.put("a", "a");
        node.put("b", "b");
        JavascriptMatcher matcher = new JavascriptMatcher("function(doc) { return doc.a != null; }");

        ParseEvent event = new ParseEvent(JsonToken.END_OBJECT, new Path(), "]", null, node);
        assertTrue(matcher.matches(event));
    }

    @Test
    public void testMatchesFalse() throws Exception {
        Map<String, Object> node = new HashMap<String, Object>();
        node.put("a", "a");
        node.put("b", "b");
        JavascriptMatcher matcher = new JavascriptMatcher("function(doc) { return doc.c != null; }");

        ParseEvent event = new ParseEvent(JsonToken.END_OBJECT, new Path(), "]", null, node);
        assertFalse(matcher.matches(event));
    }
}
