package org.meeuw.json;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;

import java.util.*;
import java.util.function.Predicate;

/**
 * Represents a json object as an iterator of {@link ParseEvent}s.
 *
 * @author Michiel Meeuwissen
 * @since 0.4
 */
public class JsonIterator implements Iterator<ParseEvent> {

    private final Path path = new Path();
    private ParseEvent next;
    private final JsonParser jp;
    private final Deque<List<String>> keys = new ArrayDeque<>();

    private final Deque<Object> objects = new ArrayDeque<>();

	private final Predicate<Path> needsKeyCollection;

	private final Predicate<Path> needsJsonCollection;


    public  JsonIterator(JsonParser jp) {
        this(jp, p -> false, p -> false);
    }

    public JsonIterator(JsonParser jp, Predicate<Path> needsKeyCollection, Predicate<Path> needsJsonCollection) {
        this.jp = jp;
		this.needsKeyCollection = needsKeyCollection;
		this.needsJsonCollection = needsJsonCollection;
    }

    @Override
    public ParseEvent next() {
        findNext();
        if (next == null) {
            throw new NoSuchElementException();
        }
        ParseEvent result = next;
        next = null;
        return result;
    }

    @Override
    public boolean hasNext() {
        findNext();
        return next != null;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    protected void findNext() {
        if (next == null) {
            while (next == null) {
                JsonToken token = jp.nextToken();
                if (token == null) {
                    break;
                }
                List<String> eventKeys = null;
                Object eventObjects = null;

                String text = jp.getString();
                switch (token) {
                    case START_OBJECT:
                        if (needsKeyCollection.test(path)) {
                            keys.add(new ArrayList<>());
                        }
                        if (needsJsonCollection.test(path) || ! objects.isEmpty()) {
                            objects.add(new LinkedHashMap<String, Object>());
                        }
                        break;
                    case START_ARRAY:
                        if (needsJsonCollection.test(path) || !objects.isEmpty()) {
                            objects.add(new ArrayList<>());
                        }
                        break;
                    case END_ARRAY:
                        path.pollLast();
                        if (needsJsonCollection.test(path)) {
                            eventObjects = objects.peekLast();
                        }
                        break;
                    case PROPERTY_NAME:
                        String fieldName = jp.getText();
                        if (needsKeyCollection.test(path)) {
                            keys.peekLast().add(fieldName);
                        }
                        path.add(new KeyEntry(fieldName));
                        break;
                    case END_OBJECT:
                        if (needsKeyCollection.test(path)) {
                            eventKeys = keys.pollLast();
                        }
                        if (needsJsonCollection.test(path)) {
                            eventObjects = objects.peekLast();
                        }
                }

                next = new ParseEvent(token, new Path(path), text, eventKeys, eventObjects);

                if (! objects.isEmpty()) {
                    switch (token) {
                        case VALUE_STRING:
                            put(objects.peekLast(), path.peekLast().toString(), jp.getString());
                            break;
                        case VALUE_NUMBER_INT:
                        case VALUE_NUMBER_FLOAT:
                            put(objects.peekLast(), path.peekLast().toString(), jp.getNumberValue());
                            break;
                        case VALUE_TRUE:
                        case VALUE_FALSE:
                            put(objects.peekLast(), path.peekLast().toString(), jp.getBooleanValue());
                            break;
                        case VALUE_NULL:
                            put(objects.peekLast(), path.peekLast().toString(), null);
                            break;
                        case END_OBJECT:
                        case END_ARRAY:
                            Object object = objects.pollLast();
                            Object e = objects.peekLast();
                            if (e != null) {
                                put(e, path.peekLast().toString(), object);
                            }
                            break;
                    }

                }
                switch (token) {
                    case START_ARRAY:
                        path.add(new ArrayEntry());
                        break;
                    case VALUE_STRING:
                    case VALUE_NUMBER_INT:
                    case VALUE_NUMBER_FLOAT:
                    case VALUE_TRUE:
                    case VALUE_FALSE:
                    case VALUE_NULL:
                    case END_OBJECT:
                    case END_ARRAY:
                        PathEntry prev = path.peekLast();
                        if (!(prev instanceof ArrayEntry)) {
                            path.pollLast();
                        } else {
                            path.addLast(((ArrayEntry) path.pollLast()).inc());
                        }
                        break;

                }
            }

        }
    }

    private void put(Object parent, String key, Object value) {
        if (parent instanceof Map) {
            ((Map) parent).put(key, value);
        } else {
            ((List) parent).add(value);
        }
    }

}
