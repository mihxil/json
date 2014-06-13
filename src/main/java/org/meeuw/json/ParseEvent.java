package org.meeuw.json;

import com.fasterxml.jackson.core.JsonToken;

import java.util.Deque;
import java.util.List;

/**
 * @author Michiel Meeuwissen
 * @since 0.4
 */
public class ParseEvent {

    private final JsonToken token;
    private final Deque<PathEntry> path;
    private final String value;
    private final List<String> keys;


    public ParseEvent(JsonToken token, Deque<PathEntry> path, String value) {
        this.token = token;
        this.path = path;
        this.value = value;
        this.keys = null;
    }

    public ParseEvent(JsonToken token, Deque<PathEntry> path, String value, List<String> keys) {
        this.token = token;
        this.path = path;
        this.value = value;
        this.keys = keys;
    }

    public String getValue() {
        return value;
    }

    public JsonToken getToken() {
        return token;
    }

    public Deque<PathEntry> getPath() {
        return path;
    }

    public List<String> getKeys() {
        return keys;
    }


    @Override
    public String toString() {
        return token + " " + path + "=" + value;
    }

}
