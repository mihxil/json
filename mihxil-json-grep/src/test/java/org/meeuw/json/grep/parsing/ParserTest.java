package org.meeuw.json.grep.parsing;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.junit.jupiter.api.Test;

import org.meeuw.json.KeyEntry;
import org.meeuw.json.Path;
import org.meeuw.json.grep.matching.*;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 * @since 0.4
 */
public class ParserTest {

    @Test
    public void constructor() throws IllegalAccessException, InvocationTargetException, InstantiationException {
        final Class<?> cls = Parser.class;
        final Constructor<?> c = cls.getDeclaredConstructors()[0];
        assertThat(c.isAccessible()).isFalse();
        c.setAccessible(true);
        c.newInstance((Object[]) null);
    }

    @Test
    public void arrayIndex(){
        SinglePathMatcher result = Parser.parseKeysMatcher("a[0].b", false, false);
        assertThat(result.getPatterns()[0]).isInstanceOf(PreciseMatch.class).hasToString("a");
        assertThat(result.getPatterns()[1]).isInstanceOf(ArrayIndexMatch.class).hasToString("0");
        assertThat(result.getPatterns()[2]).isInstanceOf(PreciseMatch.class).hasToString("b");
    }

    @Test
    public void arrayIndex2() {
        SinglePathMatcher result = Parser.parseKeysMatcher("a.[0].b", false, false);
        assertThat(result.getPatterns()[0]).isInstanceOf(PreciseMatch.class).hasToString("a");
        assertThat(result.getPatterns()[1]).isInstanceOf(ArrayIndexMatch.class).hasToString("0");
        assertThat(result.getPatterns()[2]).isInstanceOf(PreciseMatch.class).hasToString("b");
    }

    @Test
    public void wildcard() {
        SinglePathMatcher result = Parser.parseKeysMatcher("a.*.b", false, false);
        assertThat(result.getPatterns()[0]).isInstanceOf(PreciseMatch.class).hasToString("a");
        assertThat(result.getPatterns()[1]).isInstanceOf(Wildcard.class).hasToString("*");
        assertThat(result.getPatterns()[2]).isInstanceOf(PreciseMatch.class).hasToString("b");
    }

    @Test
    public void wildcardArrayIndex() {
        SinglePathMatcher result = Parser.parseKeysMatcher("a[*].b", false, false);
        assertThat(result.getPatterns()[0]).isInstanceOf(PreciseMatch.class).hasToString("a");
        assertThat(result.getPatterns()[1]).isInstanceOf(ArrayEntryMatch.class).hasToString("[*]");
        assertThat(result.getPatterns()[2]).isInstanceOf(PreciseMatch.class).hasToString("b");
    }

    @Test
    public void value() {
        PathMatcherAndChain result = (PathMatcherAndChain) Parser.parsePathMatcher("a[*].b=c", false, false);
        assertThat(result.getPatterns()[0]).isInstanceOf(SinglePathMatcher.class).hasToString("a[*].b");
        assertThat(result.getPatterns()[1]).isInstanceOf(ScalarEqualsMatcher.class).hasToString("c");
    }

    @Test
    public void regexp() {
        PathMatcherAndChain result = (PathMatcherAndChain) Parser.parsePathMatcher("a[*].b~.*", false, false);
        assertThat(result.getPatterns()[0]).isInstanceOf(SinglePathMatcher.class).hasToString("a[*].b");
        assertThat(result.getPatterns()[1]).isInstanceOf(ScalarRegexpMatcher.class).hasToString("value~.*");
    }

    @Test
    public void regexp2() {
        PathMatcherAndChain result = (PathMatcherAndChain) Parser.parsePathMatcher("a[*].b~(a|b)", false, false);
        assertThat(result.getPatterns()[0]).isInstanceOf(SinglePathMatcher.class).hasToString("a[*].b");
        assertThat(result.getPatterns()[1]).isInstanceOf(ScalarRegexpMatcher.class).hasToString("value~(a|b)");
    }

	@Test
	public void contains() {
		PathMatcherAndChain result = (PathMatcherAndChain) Parser.parsePathMatcher("a[*].b contains c", false, false);
		assertThat(result.getPatterns()[0]).isInstanceOf(SinglePathMatcher.class).hasToString("a[*].b");
		assertThat(result.getPatterns()[1]).isInstanceOf(ObjectHasKeyMatcher.class).hasToString("contains c");
	}

	@Test
	public void notcontains() {
		PathMatcherAndChain result = (PathMatcherAndChain) Parser.parsePathMatcher("a[*].b ! contains c", false, false);
		assertThat(result.getPatterns()[0]).isInstanceOf(SinglePathMatcher.class).hasToString("a[*].b");
		assertThat(result.getPatterns()[1]).isInstanceOf(ObjectMatcherNot.class).hasToString("! contains c");
	}


    @Test
    public void javascript() {
        PathMatcherAndChain result = (PathMatcherAndChain) Parser.parsePathMatcherChain("a[*].b function(doc) { return doc.b1 == 1}");
        assertThat(result.getPatterns()[0]).isInstanceOf(SinglePathMatcher.class).hasToString("a[*].b");
        assertThat(result.getPatterns()[1]).isInstanceOf(JavascriptMatcher.class)
            .hasToString("function(doc) { return doc.b1 == 1}");
    }

    @Test
    public void javascript2() {
        PathMatcherAndChain result = (PathMatcherAndChain) Parser.parsePathMatcherChain("c function(doc) {}");
        assertThat(result.getPatterns()[0]).isInstanceOf(SinglePathMatcher.class).hasToString("c");
        assertThat(result.getPatterns()[1]).isInstanceOf(JavascriptMatcher.class).hasToString("function(doc) {}");
    }

    @Test
    public void anyDepth() {
        SinglePathMatcher result = Parser.parseKeysMatcher("...b", false);
        assertThat(result.getPatterns()[0]).isInstanceOf(AnyDepthMatcher.class);
        assertThat(result.getPatterns()[1]).isInstanceOf(PreciseMatch.class);
    }

    @Test
    public void multiple() {
        PathMatcherOrChain result = (PathMatcherOrChain) Parser.parsePathMatcherChain("a.b,a");
        assertThat(result.getMatchers()[0]).isInstanceOf(SinglePathMatcher.class);
        assertThat(result.getMatchers()[0].needsObjectCollection().test(new Path(new KeyEntry("a"), new KeyEntry("b")))).isTrue();
        assertThat(result.getMatchers()[0].needsObjectCollection().test(new Path(new KeyEntry("a")))).isFalse();
        assertThat(result.getMatchers()[1]).isInstanceOf(SinglePathMatcher.class);
        assertThat(result.getMatchers()[1].needsObjectCollection().test(new Path(new KeyEntry("a"), new KeyEntry("b")))).isFalse();
        assertThat(result.getMatchers()[1].needsObjectCollection().test(new Path(new KeyEntry("a")))).isTrue();
        assertThat(result.needsObjectCollection().test(new Path(new KeyEntry("a"), new KeyEntry("b")))).isTrue();
        assertThat(result.needsObjectCollection().test(new Path(new KeyEntry("a")))).isTrue();
    }


    @Test
    public void regexpKey() {
        SinglePathMatcher result = Parser.parseKeysMatcher("/[ab]/", false);
        assertThat(result.getPatterns()[0]).isInstanceOf(RegexpKeyMatch.class);
    }


    @Test
    public void regexppath() {
        SinglePathMatcher result = Parser.parseKeysMatcher("/[ab]/.b", false);
        assertThat(result.getPatterns()[0]).isInstanceOf(RegexpKeyMatch.class);
        assertThat(result.getPatterns()[1]).isInstanceOf(PreciseMatch.class);
    }

}
