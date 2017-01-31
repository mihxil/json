package org.meeuw.json.include;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Iterator;

import org.junit.Test;
import org.meeuw.json.Util;
import org.meeuw.json.grep.GrepMainRecord;
import org.meeuw.json.grep.matching.PathMatcher;
import org.meeuw.json.grep.parsing.Parser;

import static org.junit.Assert.*;

/**
 * @author Michiel Meeuwissen
 * @since 0.9
 */
public class JsonIncludeTest {
    
    @Test
    public void test() throws IOException {
        JsonInclude including = new JsonInclude(
                Parser.parsePathMatcherChain("bla"),
                Util.getJsonParser(new StringReader("{ 'bla': 'xxx' }")),
                (ps) -> Util.getJsonParser(new StringReader("{ 'bla': 'xxx' }")));
                

    }

}