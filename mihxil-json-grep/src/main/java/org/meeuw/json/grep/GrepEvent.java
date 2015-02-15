package org.meeuw.json.grep;

import java.io.StringWriter;

import org.meeuw.json.ParseEvent;
import org.meeuw.json.Path;
import org.meeuw.json.Util;

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
        this.event = event;
        this.type = type;
    }

    public Path getPath() {
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

    public String  getNode() {
        switch (event.getToken()) {
            case END_OBJECT:
            case END_ARRAY: {
                StringWriter writer = new StringWriter();
                Util.write(event.getNode(), writer);
                return writer.toString();
            }
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
