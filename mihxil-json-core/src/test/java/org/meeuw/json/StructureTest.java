package org.meeuw.json;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**

 */
public class StructureTest {

    @Test
    public void test() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Structure structure = new Structure(out);
        structure.read(new StringReader("{b: {b1: 5}, c:5, d: [3, 2, {x: 'blabla'}, {y: 'bloebloe'}], e: 'EEE'}"));

        String[] result = (new String(out.toByteArray())).split("\n");
        assertEquals("b.b1",  result[0]);
        assertEquals("c",     result[1]);
        assertEquals("d[0]",   result[2]);
        assertEquals("d[1]",   result[3]);
        assertEquals("d[2].x", result[4]);
        assertEquals("d[3].y", result[5]);
        assertEquals("e",     result[6]);


    }

    @Test
    public void testArray() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Structure structure = new Structure(out);
        structure.read(new StringReader("{d: [{x: 'blabla'}, {y: 'bloebloe'}], e: 'bla'}"));

        String[] result = (new String(out.toByteArray())).split("\n");
        assertEquals("d[0].x", result[0]);
        assertEquals("d[1].y", result[1]);
        assertEquals("e",      result[2]);
    }

    @Test
    public void testArrayInArray() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Structure structure = new Structure(out);
        structure.read(new StringReader("{ \"items\" : [ { \"a\":['A']}]}"));
        String[] result = (new String(out.toByteArray())).split("\n");
        assertEquals("items[0].a[0]", result[0]);
    }
}
