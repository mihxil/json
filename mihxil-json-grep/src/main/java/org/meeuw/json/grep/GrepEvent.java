package org.meeuw.json.grep;

import lombok.Getter;

import java.io.StringWriter;

import org.meeuw.json.ParseEvent;
import org.meeuw.json.Path;
import org.meeuw.json.Util;

/**
 * @author Michiel Meeuwissen
 * @since 0.4
 */
@Getter
public class GrepEvent {
    private final ParseEvent event;
    public enum Type {
        VALUE,
        RECORD
    }

    private final Type type;
    private final int weight;

    public GrepEvent(ParseEvent result, int weight) {
        this.event = result;
        this.type = Type.VALUE;
        this.weight = weight;
    }
    public GrepEvent(ParseEvent event, Type type, int weight) {
        this.event = event;
        this.type = type;
        this.weight = weight;
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

    public String getNode() {
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

    @Override
    public String toString() {
        return getPath() + "=" + getValue();
    }

}
