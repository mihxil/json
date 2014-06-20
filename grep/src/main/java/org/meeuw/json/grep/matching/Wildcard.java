package org.meeuw.json.grep.matching;

import org.meeuw.json.PathEntry;

/**
 * A wild card matches always.
 */
public class Wildcard implements  KeyPattern {

    @Override
    public boolean matches(PathEntry key) {
        return true;
    }

    @Override
    public String toString() {
        return "*";
    }
}
