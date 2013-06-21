package org.meeuw.json;

import com.fasterxml.jackson.core.JsonToken;

import java.util.Deque;

/**
 * @author Michiel Meeuwissen
 * @since 0.4
 */
public class ParseEvent {

    private final JsonToken token;
    private final Deque<PathEntry> path;
    private final String value;


    public ParseEvent(JsonToken token, Deque<PathEntry> path, String value) {
        this.token = token;
        this.path = path;
        this.value = value;
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

}
