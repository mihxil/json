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
        Grep.SinglePathMatcher result = Parser.parseKeysMatcher("a[0].b", false);
        assertTrue(result.getPatterns()[0] instanceof Grep.PreciseMatch);
        assertEquals("a", result.getPatterns()[0].toString());
        assertTrue(result.getPatterns()[1] instanceof Grep.ArrayIndexMatch);
        assertEquals("0", result.getPatterns()[1].toString());
        assertTrue(result.getPatterns()[2] instanceof Grep.PreciseMatch);
        assertEquals("b", result.getPatterns()[2].toString());
    }

    @Test
    public void arrayIndex2() {
        Grep.SinglePathMatcher result = Parser.parseKeysMatcher("a.[0].b", false);
        assertTrue(result.getPatterns()[0] instanceof Grep.PreciseMatch);
        assertEquals("a", result.getPatterns()[0].toString());
        assertTrue(result.getPatterns()[1] instanceof Grep.ArrayIndexMatch);
        assertEquals("0", result.getPatterns()[1].toString());
        assertTrue(result.getPatterns()[2] instanceof Grep.PreciseMatch);
        assertEquals("b", result.getPatterns()[2].toString());
    }

    @Test
    public void wildcard() {
        Grep.SinglePathMatcher result = Parser.parseKeysMatcher("a.*.b", false);
        assertTrue(result.getPatterns()[0] instanceof Grep.PreciseMatch);
        assertEquals("a", result.getPatterns()[0].toString());
        assertTrue(result.getPatterns()[1] instanceof Grep.Wildcard);
        assertEquals("*", result.getPatterns()[1].toString());
        assertTrue(result.getPatterns()[2] instanceof Grep.PreciseMatch);
        assertEquals("b", result.getPatterns()[2].toString());
    }




}
