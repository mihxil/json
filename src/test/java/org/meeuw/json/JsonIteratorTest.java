package org.meeuw.json;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * @author Michiel Meeuwissen
 * @since 0.4
 */
public class JsonIteratorTest {

    @Test
    public void test() throws IOException {
        JsonIterator iterator = new JsonIterator(Util.getJsonParser("{'a':'b'}"));
        assertEquals("", iterator.next().getPath().toString());
        assertEquals("a", iterator.next().getPath().toString());
        assertEquals("a", iterator.next().getPath().toString());
        assertEquals("", iterator.next().getPath().toString());


    }

}
