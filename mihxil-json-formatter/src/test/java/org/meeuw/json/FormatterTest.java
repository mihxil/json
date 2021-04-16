package org.meeuw.json;

import java.io.*;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

}
