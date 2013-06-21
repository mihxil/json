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
        ParseEvent event = iterator.next();
        System.out.println(event.getPath());
        assertEquals("", event.getPath().toString());
        event = iterator.next();
        System.out.println(event.getPath());
        System.out.println(iterator.next().getPath());
        System.out.println(iterator.next().getPath());
        System.out.println(iterator.next().getPath());
    }

}
