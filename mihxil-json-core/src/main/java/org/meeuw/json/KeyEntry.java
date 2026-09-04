package org.meeuw.json;

/**
* @author Michiel Meeuwissen
* @since ...
*/
public class KeyEntry implements PathEntry {
    final String key;

    public KeyEntry(String key) {
        this.key = key;
    }
    @Override
    public String toString() {
        return key;
    }

    @Override
    public void appendTo(StringBuilder builder) {
        if (!builder.isEmpty()) {
            builder.append('.');
        }
        builder.append(key);
    }
}
