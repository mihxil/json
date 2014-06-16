package org.meeuw.json.grep;

import org.meeuw.json.PathEntry;

/**
 * a precise key pattern matches only if the key exactly equals to a certain value.
 */
class PreciseMatch implements  KeyPattern {
    private final String key;

    public PreciseMatch(String key) {
        this.key = key;
    }

    @Override
    public boolean matches(PathEntry key) {
        return this.key.equals(key.toString());
    }
    @Override
    public String toString() {
        return key;
    }
}
