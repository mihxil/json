package org.meeuw.json.grep;

import org.meeuw.json.PathEntry;

/**
 * A wild card matches always.
 */
class Wildcard implements  KeyPattern {

    @Override
    public boolean matches(PathEntry key) {
        return true;
    }

    @Override
    public String toString() {
        return "*";
    }
}
