package org.meeuw.json.grep;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

import org.meeuw.json.Util;
import org.meeuw.json.grep.matching.*;

import com.fasterxml.jackson.core.JsonGenerator;

import static org.assertj.core.api.Assertions.assertThat;

class SedTest {

    @Test
    public void testRegexp() throws IOException {
        Sed sed = new Sed(
             new PathMatcherAndChain(
                new SinglePathMatcher(true,
                    new PreciseMatch("items"),
                    new PreciseMatch("a")
                ),
                new ScalarRegexpMatcher(Pattern.compile("abc\\s*(.*)"), "$1")),
            Util.getJsonParser("{ \"items\" : [ { \"a\" : 'abc def'},  { \"a\" : 'xyz qwv'}]}"));

        StringWriter out = new StringWriter();
        try (JsonGenerator generator = Util.getJsonFactory().createGenerator(out)) {
            sed.toGenerator(generator);
        }

        assertThat(out.toString()).isEqualTo("{\"items\":[{\"a\":\"def\"},{\"a\":\"xyz qwv\"}]}");

        assertThat(sed.toString()).isEqualTo("Sed[matcher=items.a AND value~abc\\s*(.*)]");
    }


    @Test
    public void testEquals() throws IOException {
        Sed sed = new Sed(
             new PathMatcherAndChain(
                new SinglePathMatcher(true,
                    new PreciseMatch("items"),
                    new PreciseMatch("a")
                ),
                new ScalarEqualsMatcher("abc def", "foobar")),
            Util.getJsonParser("{ \"items\" : [ { \"a\" : 'abc def'},  { \"a\" : 'xyz qwv'}]}"));

        StringWriter out = new StringWriter();
        try (JsonGenerator generator = Util.getJsonFactory().createGenerator(out)) {
            sed.toGenerator(generator);
        }

        assertThat(out.toString()).isEqualTo("{\"items\":[{\"a\":\"foobar\"},{\"a\":\"xyz qwv\"}]}");

        assertThat(sed.toString()).isEqualTo("Sed[matcher=items.a AND abc def]");
    }

    @Test
    public void testReplace() throws IOException {
        Sed sed = new Sed(
             new PathMatcherAndChain(
                new SinglePathMatcher(true,
                    new PreciseMatch("items"),
                    new PreciseMatch("a")
                ),
                new ReplaceScalarMatcher("foobar")),
            Util.getJsonParser("{ \"items\" : [ { \"a\" : 'abc def'},  { \"a\" : 'xyz qwv'}]}"));

        StringWriter out = new StringWriter();
        try (JsonGenerator generator = Util.getJsonFactory().createGenerator(out)) {
            sed.toGenerator(generator);
        }

        assertThat(out.toString()).isEqualTo("{\"items\":[{\"a\":\"foobar\"},{\"a\":\"foobar\"}]}");

        assertThat(sed.toString()).isEqualTo("Sed[matcher=items.a AND replace:foobar]");
    }

    @Test
    public void swagger() throws IOException {
        String input = "{apiVersion: \"3.0\",\n" +
            "swaggerVersion: \"1.2\",\n" +
            "basePath: \"/${api.basePath}\"}";
        PathMatcher matcher = new PathMatcherOrChain(
            new PathMatcherAndChain(
                new SinglePathMatcher(new PreciseMatch("basePath")),
                new ScalarRegexpMatcher(Pattern.compile("[/]?\\$\\{api\\.basePath}"), "/v3/api")
            ),
            new PathMatcherAndChain(
                new SinglePathMatcher(new PreciseMatch("host")),
                new ScalarEqualsMatcher("${api.host}", "foobar:80")
            )
        );
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try (OutputStream output =  Sed.transform(out, matcher)) {
            output.write(input.getBytes(StandardCharsets.UTF_8));
        }

        assertThat(out.toString()).isEqualTo("{\"apiVersion\":\"3.0\",\"swaggerVersion\":\"1.2\",\"basePath\":\"/v3/api\"}");

    }

}
