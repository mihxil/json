package org.meeuw.json;

import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.TreeNode;

import java.util.List;

/**
 * @author Michiel Meeuwissen
 * @since 0.4
 */
public class ParseEvent {

    private final JsonToken token;
    private final Path path;
    private final String value;
    private final List<String> keys;
	private final TreeNode node;


    public ParseEvent(JsonToken token, Path path, String value) {
        this.token = token;
        this.path = path;
        this.value = value;
        this.keys = null;
		this.node = null;
    }

    public ParseEvent(JsonToken token, Path path, String value, List<String> keys) {
        this.token = token;
        this.path = path;
        this.value = value;
        this.keys = keys;
		this.node = null;
    }

	public ParseEvent(JsonToken token, Path path, String value, TreeNode node) {
		this.token = token;
		this.path = path;
		this.value = value;
		this.keys = null;
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


    @Override
    public String toString() {
        return token + " " + path + "=" + value;
    }

}
