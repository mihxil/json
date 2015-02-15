package org.meeuw.json.grep.parsing;

import org.junit.Test;
import org.meeuw.json.grep.matching.*;

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

	@Test
	public void contains() {
		PathMatcherAndChain result = (PathMatcherAndChain) Parser.parsePathMatcher("a[*].b contains c", false);
		assertTrue(result.getPatterns()[0] instanceof SinglePathMatcher);
		assertEquals("a[*].b", result.getPatterns()[0].toString());
		assertTrue(result.getPatterns()[1] instanceof ObjectHasKeyMatcher);
		assertEquals(" contains c", result.getPatterns()[1].toString());
	}

	@Test
	public void notcontains() {
		PathMatcherAndChain result = (PathMatcherAndChain) Parser.parsePathMatcher("a[*].b ! contains c", false);
		assertTrue(result.getPatterns()[0] instanceof SinglePathMatcher);
		assertEquals("a[*].b", result.getPatterns()[0].toString());
		assertTrue(result.getPatterns()[1] instanceof ObjectMatcherNot);
		assertEquals("! contains c", result.getPatterns()[1].toString());
	}


    @Test
    public void javascript() {
        PathMatcherAndChain result = (PathMatcherAndChain) Parser.parsePathMatcherChain("a[*].b function(doc) { return doc.b1 == 1}", false);
        assertTrue(result.getPatterns()[0] instanceof SinglePathMatcher);
        assertEquals("a[*].b", result.getPatterns()[0].toString());
        assertTrue(result.getPatterns()[1] instanceof JavascriptMatcher);
        assertEquals("function(doc) { return doc.b1 == 1}", result.getPatterns()[1].toString());
    }

    @Test
    public void javascript2() {
        PathMatcherAndChain result = (PathMatcherAndChain) Parser.parsePathMatcherChain("c function(doc) {}", false);
        assertTrue(result.getPatterns()[0] instanceof SinglePathMatcher);
        assertEquals("c", result.getPatterns()[0].toString());
        assertTrue(result.getPatterns()[1] instanceof JavascriptMatcher);
        assertEquals("function(doc) {}", result.getPatterns()[1].toString());
    }

    @Test
    public void anyDepth() {
        SinglePathMatcher result = Parser.parseKeysMatcher("...b", false);
        assertTrue(result.getPatterns()[0] instanceof AnyDepthMatcher);
        assertTrue(result.getPatterns()[1] instanceof PreciseMatch);


    }

}
