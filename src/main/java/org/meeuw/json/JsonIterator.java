package org.meeuw.json;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import java.io.IOException;
import java.util.*;

/**
 * @author Michiel Meeuwissen
 * @since 0.4
 */
public class JsonIterator implements Iterator<ParseEvent> {
    final Path path = new Path();
    ParseEvent next;
    final JsonParser jp;
    final Deque<List<String>> keys;


    public  JsonIterator(JsonParser jp) {
        this(jp, false);
    }

    public JsonIterator(JsonParser jp, boolean needsKeyCollection) {
        this.jp = jp;
        keys = needsKeyCollection ? new ArrayDeque<List<String>>() : null;

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
                    String text = jp.getText();
                    switch (token) {
                        case START_OBJECT:
                            if (keys != null) {
                                keys.add(new ArrayList<String>());
                            }
                            break;
                        case END_ARRAY:
                            path.pollLast();
                            //PathEntry prev = path.peekLast();
                            break;
                        case FIELD_NAME:
                            String fieldName = jp.getText();
                            path.add(new KeyEntry(fieldName));
                            if (keys != null) {
                                keys.peekLast().add(fieldName);
                            }
                            break;
                        case END_OBJECT:
                            if (keys != null) {
                                eventKeys = keys.pollLast();
                            }

                    }

                    next = new ParseEvent(token, new Path(path), text, eventKeys);


                    switch(token) {
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
                            if (! (prev instanceof ArrayEntry)) {
                                path.pollLast();
                            } else {
                                path.addLast(((ArrayEntry) path.pollLast()).inc());
                            }
                            break;

                    }
                    switch (token) {
                        case END_ARRAY:
                            PathEntry prev = path.peekLast();
                            if (prev instanceof ArrayEntry) {
                                path.addLast(((ArrayEntry) path.pollLast()).inc());
                                break;
                            }
                        case VALUE_STRING:
                        case VALUE_NUMBER_INT:
                        case VALUE_NUMBER_FLOAT:
                        case VALUE_TRUE:
                        case VALUE_FALSE:
                        case VALUE_NULL:
//                            path.removeLast();
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
