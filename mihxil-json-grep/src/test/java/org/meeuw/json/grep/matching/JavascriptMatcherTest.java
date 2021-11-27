package org.meeuw.json.grep.matching;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import org.meeuw.json.ParseEvent;
import org.meeuw.json.Path;

import com.fasterxml.jackson.core.JsonToken;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JavascriptMatcherTest {

    @Test
    public void testMatches() {
        Map<String, Object> node = new HashMap<>();
        node.put("a", "a");
        node.put("b", "b");
        JavascriptMatcher matcher = new JavascriptMatcher("function(doc) { return doc.a != null; }");

        ParseEvent event = new ParseEvent(JsonToken.END_OBJECT, new Path(), "]", null, node);
        assertTrue(matcher.matches(event));
    }

    @Test
    public void testMatchesFalse() {
        Map<String, Object> node = new HashMap<>();
        node.put("a", "a");
        node.put("b", "b");
        JavascriptMatcher matcher = new JavascriptMatcher("function(doc) { return doc.c != null; }");

        ParseEvent event = new ParseEvent(JsonToken.END_OBJECT, new Path(), "]", null, node);
        assertFalse(matcher.matches(event));
    }


    @Test
    public void testMatchesArray() {
        JavascriptMatcher matcher = new JavascriptMatcher("function(doc) { return doc.c != null; }");

        ParseEvent event = new ParseEvent(JsonToken.END_OBJECT, new Path(), "]", null, new Object[] {"a", "b"});
        assertFalse(matcher.matches(event));
    }
    @Test
    public void errorneousJavascript() {
        assertThatThrownBy(() -> {
                Map<String, Object> node = new HashMap<>();
                node.put("a", "a");
                node.put("b", "b");
                JavascriptMatcher matcher = new JavascriptMatcher("function(doc) { return 'foobar'; }");

                ParseEvent event = new ParseEvent(JsonToken.END_OBJECT, new Path(), "]", null,
                    node);
                matcher.matches(event);
            }).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void errorneousEvent() {
        assertThatThrownBy(() -> {
            JavascriptMatcher matcher = new JavascriptMatcher("function(doc) { return true }");

            ParseEvent event = new ParseEvent(JsonToken.END_OBJECT, new Path(), "]", null,
                null);
            matcher.matches(event);
        }).isInstanceOf(IllegalStateException.class);
    }
}
