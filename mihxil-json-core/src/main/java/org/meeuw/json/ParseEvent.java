package org.meeuw.json;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonToken;

/**
 * @author Michiel Meeuwissen
 * @since 0.4
 */
public class ParseEvent {

    private final JsonToken token;
    private final Path path;
    private final String value;
    private final List<String> keys;
	private final Map<String, Object> node;


    public ParseEvent(JsonToken token, Path path, String value) {
        this(token, path, value, null);
    }

    public ParseEvent(JsonToken token, Path path, String value, List<String> keys) {
        this(token, path, value, keys, null);
    }

	public ParseEvent(JsonToken token, Path path, String value, List<String> keys, Map<String, Object> node) {
		this.token = token;
		this.path = path;
		this.value = value;
		this.keys = keys;
		this.node = node;
	}

    public String getValue() {
        return value;
    }

    public JsonToken getToken() {
        return token;
    }

    public Path getPath() {
        return path;
    }

    public List<String> getKeys() {
        return keys;
    }

	public Map<String, Object> getNode() {
		return node;
	}


    @Override
    public String toString() {
        return token + " " + path + "=" + value;
    }

}
