package org.meeuw.json.grep;

import lombok.Getter;

import org.meeuw.json.ParseEvent;
import org.meeuw.json.Path;

/**
 * A grep event is a light wrapper around a {@link ParseEvent}, but it adds two fields:
 * 'the type' of the event, i.e how wel it matched ({@link #getWeight()} and why it matched {@link #getType()}
 * @author Michiel Meeuwissen
 * @since 0.4
 */
@Getter
public class GrepEvent {
    private final ParseEvent event;

    public enum Type {
        /**
         * The event occurred because it matched for value
         */
        VALUE,
        /**
         * The event occurred because it matched for a record separation
         */
        RECORD
    }

    private final Type type;

    /**
     * How well this matched. 0 is no match.
     */
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

    /**
     * Returns {@link ParseEvent#valueOrNodeAsConciseString()} of the associated {@link #getEvent()}
     */
    public String valueOrNodeAsConciseString() {
        return event.valueOrNodeAsConciseString();
    }

    /**
     * Returns the {@link ParseEvent#valueOrNodeAsString()} associated with this event as a string.
     */
    public String valueOrNodeAsString() {
        return event.valueOrNodeAsString();
    }

    @Override
    public String toString() {
        return getPath() + "=" + valueOrNodeAsConciseString();
    }

}
