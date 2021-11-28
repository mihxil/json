package org.meeuw.json;

import java.io.*;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import org.meeuw.main.AbstractMainTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.meeuw.main.Assertions.assertExitCode;

/**
 * @author Michiel Meeuwissen
 * @since 1.0
 */
public class FormatterTest {

    @Test
    public void format() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Formatter formatter = new Formatter(out);

        formatter.read(
                new StringReader("{a:1, b:2}"));
        assertEquals("{\n" +
                "  \"a\" : 1,\n" +
                "  \"b\" : 2\n" +
                "}", out.toString());
    }

    public static class Main extends AbstractMainTest {

        @Test
        public void main() {
            System.setIn(new ByteArrayInputStream("{'a': 'B', 'array': [1, 2.0, true, false], 'object': { 'x': null }}".getBytes(StandardCharsets.UTF_8)));

            assertExitCode(() -> {
                Formatter.main(new String[]{});
            }).isNormal();

            assertThat(outContent.toString()).isEqualTo("{\n" +
                "  \"a\" : \"B\",\n" +
                "  \"array\" : [ 1, 2.0, true, false ],\n" +
                "  \"object\" : {\n" +
                "    \"x\" : null\n" +
                "  }\n" +
                "}");
        }

        @Test
        public void help() {
            assertExitCode(() -> {
                Formatter.main(new String[]{"-help"});
            }).isNormal();
            assertThat(outContent.toString()).startsWith("jsonformat");

        }
        @Test
        public void version() throws IOException {
            assertExitCode(() -> {
                Formatter.main(new String[]{"-version"});
            }).isNormal();
            assertThat(outContent.toString()).startsWith(String.valueOf(MainUtil.version()));
        }
    }

}
