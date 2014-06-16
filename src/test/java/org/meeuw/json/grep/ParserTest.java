package org.meeuw.json.grep;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Michiel Meeuwissen
 * @since 0.4
 */
public class ParserTest {

    @Test
    public void arrayIndex(){
        SinglePathMatcher result = Parser.parseKeysMatcher("a[0].b", false);
        assertTrue(result.getPatterns()[0] instanceof PreciseMatch);
        assertEquals("a", result.getPatterns()[0].toString());
        assertTrue(result.getPatterns()[1] instanceof ArrayIndexMatch);
        assertEquals("0", result.getPatterns()[1].toString());
        assertTrue(result.getPatterns()[2] instanceof PreciseMatch);
        assertEquals("b", result.getPatterns()[2].toString());
    }

    @Test
    public void arrayIndex2() {
        SinglePathMatcher result = Parser.parseKeysMatcher("a.[0].b", false);
        assertTrue(result.getPatterns()[0] instanceof PreciseMatch);
        assertEquals("a", result.getPatterns()[0].toString());
        assertTrue(result.getPatterns()[1] instanceof ArrayIndexMatch);
        assertEquals("0", result.getPatterns()[1].toString());
        assertTrue(result.getPatterns()[2] instanceof PreciseMatch);
        assertEquals("b", result.getPatterns()[2].toString());
    }

    @Test
    public void wildcard() {
        SinglePathMatcher result = Parser.parseKeysMatcher("a.*.b", false);
        assertTrue(result.getPatterns()[0] instanceof PreciseMatch);
        assertEquals("a", result.getPatterns()[0].toString());
        assertTrue(result.getPatterns()[1] instanceof Wildcard);
        assertEquals("*", result.getPatterns()[1].toString());
        assertTrue(result.getPatterns()[2] instanceof PreciseMatch);
        assertEquals("b", result.getPatterns()[2].toString());
    }

    @Test
    public void wildcardArrayIndex() {
        SinglePathMatcher result = Parser.parseKeysMatcher("a[*].b", false);
        assertTrue(result.getPatterns()[0] instanceof PreciseMatch);
        assertEquals("a", result.getPatterns()[0].toString());
        assertTrue(result.getPatterns()[1] instanceof ArrayEntryMatch);
        assertEquals("[*]", result.getPatterns()[1].toString());
        assertTrue(result.getPatterns()[2] instanceof PreciseMatch);
        assertEquals("b", result.getPatterns()[2].toString());
    }

    @Test
    public void value() {
        PathMatcherAndChain result = (PathMatcherAndChain) Parser.parsePathMatcher("a[*].b=c", false);
        assertTrue(result.getPatterns()[0] instanceof SinglePathMatcher);
        assertEquals("a[*].b", result.getPatterns()[0].toString());
        assertTrue(result.getPatterns()[1] instanceof ValueEqualsMatcher);
        assertEquals("c", result.getPatterns()[1].toString());
    }

    @Test
    public void regexp() {
        PathMatcherAndChain result = (PathMatcherAndChain) Parser.parsePathMatcher("a[*].b~.*", false);
        assertTrue(result.getPatterns()[0] instanceof SinglePathMatcher);
        assertEquals("a[*].b", result.getPatterns()[0].toString());
        assertTrue(result.getPatterns()[1] instanceof ValueRegexpMatcher);
        assertEquals(".*", result.getPatterns()[1].toString());
    }




}
