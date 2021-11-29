package org.meeuw.json.grep;

import java.io.IOException;
import java.io.StringWriter;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

import org.meeuw.json.Util;
import org.meeuw.json.grep.matching.*;

import com.fasterxml.jackson.core.JsonGenerator;

import static org.assertj.core.api.Assertions.assertThat;

class SedTest {

    @Test
    public void test() throws IOException {
        Sed sed = new Sed(
             new PathMatcherAndChain(
                new SinglePathMatcher(true,
                    new PreciseMatch("items"),
                    new PreciseMatch("a")
                ),
                new ValueRegexpMatcher(Pattern.compile("abc\\s*(.*)"), "$1")),
            Util.getJsonParser("{ \"items\" : [ { \"a\" : 'abc def'},  { \"a\" : 'xyz qwv'}]}"));

        StringWriter out = new StringWriter();
        try (JsonGenerator generator = Util.getJsonFactory().createGenerator(out)) {
            sed.toGenerator(generator);
        }

        assertThat(out.toString()).isEqualTo("{\"items\":[{\"a\":\"def\"},{\"a\":\"xyz qwv\"}]}");

        assertThat(sed.toString()).isEqualTo("Sed[matcher=items.a AND value~abc\\s*(.*)]");
    }

}
