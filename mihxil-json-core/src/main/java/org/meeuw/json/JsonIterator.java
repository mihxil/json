package org.meeuw.json;

import java.io.IOException;
import java.util.*;

import org.meeuw.util.Predicate;
import org.meeuw.util.Predicates;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

/**
 * @author Michiel Meeuwissen
 * @since 0.4
 */
public class JsonIterator implements Iterator<ParseEvent> {
    private final Path path = new Path();
    private ParseEvent next;
    private final JsonParser jp;
    private final Deque<List<String>> keys = new ArrayDeque<List<String>>();

    private final Deque<Map<String, Object>> objects = new ArrayDeque<Map<String, Object>>();

	private final Predicate<Path> needsKeyCollection;

	private final Predicate<Path> needsJsonCollection;


    public  JsonIterator(JsonParser jp) {
        this(jp, Predicates.<Path>alwaysFalse(), Predicates.<Path>alwaysFalse());
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
            try {
                while (next == null) {
                    JsonToken token = jp.nextToken();
                    if (token == null) {
                        break;
                    }
                    List<String> eventKeys = null;
                    Map<String, Object> eventObjects = null;

                    String text = jp.getText();
                    switch (token) {
                        case START_OBJECT:
                            if (needsKeyCollection.test(path)) {
                                keys.add(new ArrayList<String>());
                            }
                            if (needsJsonCollection.test(path)) {
                                objects.add(new HashMap<String, Object>());
                            }
                            break;
                        case END_ARRAY:
                            path.pollLast();
                            //PathEntry prev = path.peekLast();
                            break;
                        case FIELD_NAME:
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
                                eventObjects = objects.pollLast();
                            }
                    }

                    next = new ParseEvent(token, new Path(path), text, eventKeys, eventObjects);

                    Path parent = path.parent();
                    if (parent != null && needsJsonCollection.test(parent) && ! objects.isEmpty()) {
                        switch (token) {
                            case VALUE_STRING:
                                objects.peekLast().put(path.peekLast().toString(), jp.getText());
                                break;
                            case VALUE_NUMBER_INT:
                            case VALUE_NUMBER_FLOAT:
                                objects.peekLast().put(path.peekLast().toString(), jp.getNumberValue());
                                break;
                            case VALUE_TRUE:
                            case VALUE_FALSE:
                                objects.peekLast().put(path.peekLast().toString(), jp.getBooleanValue());
                                break;
                            case VALUE_NULL:
                                objects.peekLast().put(path.peekLast().toString(), null);
                                break;
                            case END_OBJECT:
                                Map<String, Object> object = objects.pollLast();
                                Map<String, Object> e = objects.peekLast();
                                if (e != null) {
                                    e.put(path.peekLast().toString(), object);
                                }
                                break;
                            case END_ARRAY:
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
            } catch (JsonParseException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
