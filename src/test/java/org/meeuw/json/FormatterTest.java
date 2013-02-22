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
		Formatter formatter = new Formatter();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		formatter.format(
				new StringReader("{a:1, b:2}"),
				out);
		assertEquals("{\n" +
				"  \"a\" : 1,\n" +
				"  \"b\" : 2\n" +
				"}", out.toString());
	}

}
