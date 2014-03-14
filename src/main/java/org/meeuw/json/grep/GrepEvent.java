package org.meeuw.json.grep;

import org.meeuw.json.ParseEvent;
import org.meeuw.json.PathEntry;

import java.util.Deque;

/**
 * @author Michiel Meeuwissen
 * @since 0.4
 */
public class GrepEvent {
    private final ParseEvent event;
    public static enum Type {
        VALUE,
        RECORD
    }

    private final Type type;
    public GrepEvent(ParseEvent result) {
        this.event = result;
        this.type = Type.VALUE;
    }
    public GrepEvent(ParseEvent event, Type type) {
        this.event = null;
        this.type = type;
    }

    public Deque<PathEntry> getPath() {
        return event.getPath();
    }

    public String getValue() {
        switch(event.getToken()) {
            case END_OBJECT:
                return "{...}";
            case END_ARRAY:
                return "[...]";
            default:
                return event.getValue();
        }
    }

    public Type getType() {
        return type;
    }

    @Override
    public String toString() {
        return getPath() + "=" + getValue();
    }


}
