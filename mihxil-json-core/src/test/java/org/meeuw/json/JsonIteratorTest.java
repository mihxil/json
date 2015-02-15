package org.meeuw.json;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.meeuw.util.Predicate;
import org.meeuw.util.Predicates;

import com.fasterxml.jackson.core.JsonToken;

import static org.junit.Assert.*;

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

    @Test
    public void test2() throws IOException {
        JsonIterator iterator = new JsonIterator(Util.getJsonParser("{'b':[]}"));
        assertEvent(iterator.next(), JsonToken.START_OBJECT, 0);
        assertEvent(iterator.next(), JsonToken.FIELD_NAME, 1, "b");
        assertEvent(iterator.next(), JsonToken.START_ARRAY, 1);
        assertEvent(iterator.next(), JsonToken.END_ARRAY, 1);
        assertEvent(iterator.next(), JsonToken.END_OBJECT, 0);
    }

    @Test
    public void testArray() throws IOException {
        JsonIterator iterator = new JsonIterator(Util.getJsonParser("[3, 'twee', null]"));

        assertEvent(iterator.next(), JsonToken.START_ARRAY, 0);
        assertEvent(iterator.next(), JsonToken.VALUE_NUMBER_INT, 1, "3");
        assertEvent(iterator.next(), JsonToken.VALUE_STRING, 1, "twee");
        assertEvent(iterator.next(), JsonToken.VALUE_NULL, 1, "null");
        assertEvent(iterator.next(), JsonToken.END_ARRAY, 0);
    }

    @Test
    public void testNestedObject() throws IOException {
        JsonIterator iterator = new JsonIterator(Util.getJsonParser("{a: {}, c:5}"));

        assertEvent(iterator.next(), JsonToken.START_OBJECT, 0);
        assertEvent(iterator.next(), JsonToken.FIELD_NAME, 1, "a");
        assertEvent(iterator.next(), JsonToken.START_OBJECT, 1);
        assertEvent(iterator.next(), JsonToken.END_OBJECT, 1);
        assertEvent(iterator.next(), JsonToken.FIELD_NAME, 1, "c");
        assertEvent(iterator.next(), JsonToken.VALUE_NUMBER_INT, 1, "5");
        assertEvent(iterator.next(), JsonToken.END_OBJECT, 0);
        assertFalse(iterator.hasNext());
    }

    @Test
    public void test3() throws IOException {
        JsonIterator iterator = new JsonIterator(Util.getJsonParser("{b: {b1: 5}, c:5, d: [3, 2, {x: 'blabla'}, {y: 'bloebloe'}], e: 'EEE'}"));

        assertEvent(iterator.next(), JsonToken.START_OBJECT, 0);
        assertEvent(iterator.next(), JsonToken.FIELD_NAME, 1, "b");
        assertEvent(iterator.next(), JsonToken.START_OBJECT, 1);
        assertEvent(iterator.next(), JsonToken.FIELD_NAME, 2, "b1");
        assertEvent(iterator.next(), JsonToken.VALUE_NUMBER_INT, 2, "5");
        assertEvent(iterator.next(), JsonToken.END_OBJECT, 1);
        assertEvent(iterator.next(), JsonToken.FIELD_NAME, 1, "c");
        assertEvent(iterator.next(), JsonToken.VALUE_NUMBER_INT, 1, "5");
        assertEvent(iterator.next(), JsonToken.FIELD_NAME, 1, "d");
        assertEvent(iterator.next(), JsonToken.START_ARRAY, 1);
        assertEvent(iterator.next(), JsonToken.VALUE_NUMBER_INT, 2, "3");
        assertEvent(iterator.next(), JsonToken.VALUE_NUMBER_INT, 2, "2");
        assertEvent(iterator.next(), JsonToken.START_OBJECT, 2);
        assertEvent(iterator.next(), JsonToken.FIELD_NAME, 3, "x");
        assertEvent(iterator.next(), JsonToken.VALUE_STRING, 3, "blabla");
        assertEvent(iterator.next(), JsonToken.END_OBJECT, 2);
        assertEvent(iterator.next(), JsonToken.START_OBJECT, 2);
        assertEvent(iterator.next(), JsonToken.FIELD_NAME, 3, "y");
        assertEvent(iterator.next(), JsonToken.VALUE_STRING, 3, "bloebloe");
        assertEvent(iterator.next(), JsonToken.END_OBJECT, 2);
        assertEvent(iterator.next(), JsonToken.END_ARRAY, 1);
        assertEvent(iterator.next(), JsonToken.FIELD_NAME, 1, "e");
        assertEvent(iterator.next(), JsonToken.VALUE_STRING, 1, "EEE");
        assertEvent(iterator.next(), JsonToken.END_OBJECT, 0);
        assertFalse(iterator.hasNext());
    }

    @Test
    public void collectKeys() throws IOException {
        JsonIterator iterator = new JsonIterator(Util.getJsonParser("{a: 1, b: {c: 1, d: 2}}"), new Predicate<Path>() {
			@Override
			public boolean test(Path path) {
				return 	path.size() == 1 && path.peekLast().toString().equals("b");
			}
		}, Predicates.<Path>alwaysFalse());
        assertEvent(iterator.next(), JsonToken.START_OBJECT, 0);
		assertEvent(iterator.next(), JsonToken.FIELD_NAME, 1, "a");
		assertEvent(iterator.next(), JsonToken.VALUE_NUMBER_INT, 1, "1");
        assertEvent(iterator.next(), JsonToken.FIELD_NAME, 1, "b");
        assertEvent(iterator.next(), JsonToken.START_OBJECT, 1);
		assertEvent(iterator.next(), JsonToken.FIELD_NAME, 2, "c");
		assertEvent(iterator.next(), JsonToken.VALUE_NUMBER_INT, 2, "1");
		assertEvent(iterator.next(), JsonToken.FIELD_NAME, 2, "d");
		assertEvent(iterator.next(), JsonToken.VALUE_NUMBER_INT, 2, "2");
		ParseEvent last = iterator.next();
		assertEvent(last, JsonToken.END_OBJECT, 1);
		assertEquals(Arrays.asList("c", "d"), last.getKeys());
		ParseEvent lastout = iterator.next();
		assertEvent(lastout, JsonToken.END_OBJECT, 0);
		assertEquals(null, lastout.getKeys());
        assertFalse(iterator.hasNext());
    }


	@Test
	public void collectObjects() throws IOException {
		JsonIterator iterator = new JsonIterator(Util.getJsonParser("{a: 1, b: {c: 1, d: {}}}"),
				Predicates.<Path>alwaysFalse(),
				new Predicate<Path>() {
                    @Override
                    public boolean test(Path path) {
                        return path != null && path.size() == 1 && path.peekLast().toString().equals("b");
                    }
                });
		assertEvent(iterator.next(), JsonToken.START_OBJECT, 0);
		assertEvent(iterator.next(), JsonToken.FIELD_NAME, 1, "a");
		assertEvent(iterator.next(), JsonToken.VALUE_NUMBER_INT, 1, "1");
		assertEvent(iterator.next(), JsonToken.FIELD_NAME, 1, "b");
		assertEvent(iterator.next(), JsonToken.START_OBJECT, 1);
		assertEvent(iterator.next(), JsonToken.FIELD_NAME, 2, "c");
		assertEvent(iterator.next(), JsonToken.VALUE_NUMBER_INT, 2, "1");
		assertEvent(iterator.next(), JsonToken.FIELD_NAME, 2, "d");
        assertEvent(iterator.next(), JsonToken.START_OBJECT, 2);
        assertEvent(iterator.next(), JsonToken.END_OBJECT, 2);
		ParseEvent last = iterator.next();
		assertEvent(last, JsonToken.END_OBJECT, 1);
        Map<String, Object> expected = new HashMap<String, Object>();
        expected.put("c", Integer.valueOf(1));
        expected.put("d", new HashMap<String, Object>());
		assertEquals(expected, last.getNode());
		ParseEvent lastout = iterator.next();
		assertEvent(lastout, JsonToken.END_OBJECT, 0);
		assertEquals(null, lastout.getKeys());
		assertFalse(iterator.hasNext());
	}

    @Test
    public void collectArray() throws IOException {
        JsonIterator iterator = new JsonIterator(Util.getJsonParser("{a: 1, b: [1, 2.0, 3]}"),
                Predicates.<Path>alwaysFalse(),
                new Predicate<Path>() {
                    @Override
                    public boolean test(Path path) {
                        return path != null && path.size() == 1 && path.peekLast().toString().equals("b");
                    }
                });
        assertEvent(iterator.next(), JsonToken.START_OBJECT, 0);
        assertEvent(iterator.next(), JsonToken.FIELD_NAME, 1, "a");
        assertEvent(iterator.next(), JsonToken.VALUE_NUMBER_INT, 1, "1");
        assertEvent(iterator.next(), JsonToken.FIELD_NAME, 1, "b");
        assertEvent(iterator.next(), JsonToken.START_ARRAY, 1);
        assertEvent(iterator.next(), JsonToken.VALUE_NUMBER_INT, 2, "1");
        assertEvent(iterator.next(), JsonToken.VALUE_NUMBER_FLOAT, 2, "2.0");
        assertEvent(iterator.next(), JsonToken.VALUE_NUMBER_INT, 2, "3");
        ParseEvent last = iterator.next();
        assertEvent(last, JsonToken.END_ARRAY, 1);
        List<Object> expected = Arrays.<Object>asList(1, 2.0, 3);
        assertEquals(expected, last.getNode());
        ParseEvent lastout = iterator.next();
        assertEvent(lastout, JsonToken.END_OBJECT, 0);
        assertEquals(null, lastout.getKeys());
        assertFalse(iterator.hasNext());
    }

    @Test
    public void collectObjectsMultiple() throws IOException {
        JsonIterator iterator = new JsonIterator(Util.getJsonParser("{a: 1, b: {c: 1, d: ['x', 'y']}}"),
                Predicates.<Path>alwaysFalse(),
                new Predicate<Path>() {
                    @Override
                    public boolean test(Path path) {
                        return path != null &&
                                ( (path.size() == 1 && path.peekLast().toString().equals("b")) ||
                                  (path.size() == 2 && path.peekLast().toString().equals("d"))
                                );
                    }
                });
        assertEvent(iterator.next(), JsonToken.START_OBJECT, 0);
        assertEvent(iterator.next(), JsonToken.FIELD_NAME, 1, "a");
        assertEvent(iterator.next(), JsonToken.VALUE_NUMBER_INT, 1, "1");
        assertEvent(iterator.next(), JsonToken.FIELD_NAME, 1, "b");
        assertEvent(iterator.next(), JsonToken.START_OBJECT, 1);
        assertEvent(iterator.next(), JsonToken.FIELD_NAME, 2, "c");
        assertEvent(iterator.next(), JsonToken.VALUE_NUMBER_INT, 2, "1");
        assertEvent(iterator.next(), JsonToken.FIELD_NAME, 2, "d");
        assertEvent(iterator.next(), JsonToken.START_ARRAY, 2);
        assertEvent(iterator.next(), JsonToken.VALUE_STRING, 3, "x");
        assertEvent(iterator.next(), JsonToken.VALUE_STRING, 3, "y");
        ParseEvent endArray = iterator.next();
        assertEvent(endArray, JsonToken.END_ARRAY, 2);
        assertEquals(Arrays.asList("x", "y"), endArray.getNode());
        ParseEvent endObject = iterator.next();
        assertEvent(endObject, JsonToken.END_OBJECT, 1);
        assertEquals("{c=1, d=[x, y]}", endObject.getNode().toString());
        assertEvent(iterator.next(), JsonToken.END_OBJECT, 0);
    }


    @Test
    public void array() throws IOException {
        JsonIterator iterator = new JsonIterator(Util.getJsonParser("{ \"items\":[{\"a\":'x'}]}"));
        assertEvent(iterator.next(), JsonToken.START_OBJECT, 0);
        assertEvent(iterator.next(), JsonToken.FIELD_NAME, 1, "items");
        assertEvent(iterator.next(), JsonToken.START_ARRAY, 1);
        assertEvent(iterator.next(), JsonToken.START_OBJECT, 2);
        assertEvent(iterator.next(), JsonToken.FIELD_NAME, 3, "a");
        assertEvent(iterator.next(), JsonToken.VALUE_STRING, 3, "x");
        assertEvent(iterator.next(), JsonToken.END_OBJECT, 2);
        assertEvent(iterator.next(), JsonToken.END_ARRAY, 1);
        assertEvent(iterator.next(), JsonToken.END_OBJECT, 0);
        assertFalse(iterator.hasNext());
    }
    @Test
    public void arrayInArray() throws IOException {
        JsonIterator iterator = new JsonIterator(Util.getJsonParser("{ \"items\":[{\"a\":[], \"b\": 'B'}]}"));
        assertEvent(iterator.next(), JsonToken.START_OBJECT, 0);
        assertEvent(iterator.next(), JsonToken.FIELD_NAME, 1, "items");
        assertEvent(iterator.next(), JsonToken.START_ARRAY, 1);
        assertEvent(iterator.next(), JsonToken.START_OBJECT, 2);
        assertEvent(iterator.next(), JsonToken.FIELD_NAME, 3, "a");
        assertEvent(iterator.next(), JsonToken.START_ARRAY, 3);
        assertEvent(iterator.next(), JsonToken.END_ARRAY, 3);
        assertEvent(iterator.next(), JsonToken.FIELD_NAME, 3, "b");
        assertEvent(iterator.next(), JsonToken.VALUE_STRING, 3, "B");
        assertEvent(iterator.next(), JsonToken.END_OBJECT, 2);
        assertEvent(iterator.next(), JsonToken.END_ARRAY, 1);
        assertEvent(iterator.next(), JsonToken.END_OBJECT, 0);
        assertFalse(iterator.hasNext());
    }

    @Test
    public void npe() throws IOException {
        JsonIterator iterator = new JsonIterator(
                Util.getJsonParser("{\"items\" : [{ \"result\" : {\"a\" : {}, \"b\" : 1 }} ]}"), Predicates.<Path>alwaysFalse(), Predicates.<Path>alwaysFalse());
        assertEvent(iterator.next(), JsonToken.START_OBJECT, 0);
        assertEvent(iterator.next(), JsonToken.FIELD_NAME, 1, "items");
        assertEvent(iterator.next(), JsonToken.START_ARRAY, 1);
        assertEvent(iterator.next(), JsonToken.START_OBJECT, 2);
        assertEvent(iterator.next(), JsonToken.FIELD_NAME, 3, "result");
        assertEvent(iterator.next(), JsonToken.START_OBJECT, 3);
        assertEvent(iterator.next(), JsonToken.FIELD_NAME, 4, "a");
        assertEvent(iterator.next(), JsonToken.START_OBJECT, 4);
        assertEvent(iterator.next(), JsonToken.END_OBJECT, 4);
        assertEvent(iterator.next(), JsonToken.FIELD_NAME, 4, "b");
        assertEvent(iterator.next(), JsonToken.VALUE_NUMBER_INT, 4);
        assertEvent(iterator.next(), JsonToken.END_OBJECT, 3);
        assertEvent(iterator.next(), JsonToken.END_OBJECT, 2);
        assertEvent(iterator.next(), JsonToken.END_ARRAY, 1);
        assertEvent(iterator.next(), JsonToken.END_OBJECT, 0);
        assertFalse(iterator.hasNext());
        assertFalse(iterator.hasNext());

    }

    protected void assertEvent(ParseEvent event, JsonToken token, int depth, String value) {
        assertTrue(event.getToken() == token);
        assertEquals(depth, event.getPath().size());
        assertEquals(value, event.getValue());
    }

    protected void assertEvent(ParseEvent event, JsonToken token, int depth) {
        assertEquals(token, event.getToken());
        assertEquals(depth, event.getPath().size());

    }
}
