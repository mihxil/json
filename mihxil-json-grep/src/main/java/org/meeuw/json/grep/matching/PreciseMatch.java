package org.meeuw.json.grep.matching;

import org.meeuw.json.PathEntry;

/**
 * a precise key pattern matches only if the key exactly equals to a certain value.
 */
public class PreciseMatch extends AbstractKeyPattern  {
    private final String key;
    private final boolean ignoreCase;


    public PreciseMatch(String key) {
        this(key, false);
    }

    public PreciseMatch(String key, boolean ignoreCase) {
        this.key = key;
        this.ignoreCase = ignoreCase;
    }

    @Override
    public boolean matches(PathEntry key) {
        if (ignoreCase) {
            return this.key.equalsIgnoreCase(key.toString());
        } else {
            return this.key.equals(key.toString());
        }
    }

    @Override
    public String toString() {
        return key;
    }
}
