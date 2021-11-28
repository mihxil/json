package org.meeuw.json;

import lombok.Getter;
import lombok.With;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonToken;

/**
 * @author Michiel Meeuwissen
 * @since 0.4
 */
public class ParseEvent {

    @Getter
    private final JsonToken token;
    @Getter
    private final Path path;
    @With
    @Getter
    private final String value;
    private final List<String> keys;
    @Getter
	private final Object node;


    public ParseEvent(JsonToken token, Path path, String value) {
        this(token, path, value, null);
    }

    public ParseEvent(JsonToken token, Path path, String value, List<String> keys) {
        this(token, path, value, keys, null);
    }

	public ParseEvent(JsonToken token, Path path, String value, List<String> keys, Object node) {
		this.token = token;
		this.path = path;
		this.value = value;
		this.keys = keys;
		this.node = node;
	}


    public List<String> getKeys() {
        return keys == null ? null : Collections.unmodifiableList(keys);
    }

    @Override
    public String toString() {
        return token + " " + path + "=" + value;
    }

    public void toGenerator(JsonGenerator generator) throws IOException {
          switch(getToken()) {
            case START_OBJECT:
                generator.writeStartObject();
                break;
            case END_OBJECT:
                generator.writeEndObject();
                break;
            case START_ARRAY:
                generator.writeStartArray();
                break;
            case END_ARRAY:
                generator.writeEndArray();
                break;
            case FIELD_NAME:
                generator.writeFieldName(getValue());
                break;
            case VALUE_EMBEDDED_OBJECT:
                // don't know
                generator.writeObject(getValue());
                break;
            case VALUE_STRING:
                generator.writeString(getValue());
                break;
            case VALUE_NUMBER_INT:
                generator.writeNumber(getValue());
                break;
            case VALUE_NUMBER_FLOAT:
                generator.writeNumber(getValue()); //.getValueAsDouble());
                break;
            case VALUE_TRUE:
                generator.writeBoolean(true);
                break;
            case VALUE_FALSE:
                generator.writeBoolean(false);
                break;
            case VALUE_NULL:
                generator.writeNull();
                break;

        }
    }

}
