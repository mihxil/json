package org.meeuw.json;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;

import static org.junit.Assert.assertEquals;

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
