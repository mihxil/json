package org.meeuw.json.include;

import java.io.IOException;
import java.io.StringReader;

import org.junit.Test;
import org.meeuw.json.JsonIterator;
import org.meeuw.json.Util;
import org.meeuw.json.grep.matching.PathMatcher;
import org.meeuw.json.grep.parsing.Parser;

/**
 * @author Michiel Meeuwissen
 * @since 0.9
 */
public class JsonIncludeTest {

    @Test
    public void test() throws IOException {
        PathMatcher pathMatcher = Parser.parsePathMatcherChain("bla");
        JsonIterator iterator =   new JsonIterator(Util.getJsonParser(new StringReader("{ 'bla': 'xxx' }")));


        JsonInclude including = JsonInclude.builder()
            .matcher(pathMatcher)
            .replacement((ps) -> Util.getJsonParser(new StringReader("{ 'bla': 'xxx' }")))
            .build();


    }

}
