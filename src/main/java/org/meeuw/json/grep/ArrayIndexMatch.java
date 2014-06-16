package org.meeuw.json.grep;

import org.meeuw.json.ArrayEntry;
import org.meeuw.json.PathEntry;

/**
 * a precise key pattern matches only if the key exactly equals to a certain value.
 */
class ArrayIndexMatch extends ArrayEntryMatch {
    private final int index;

    public ArrayIndexMatch(int index) {
        this.index = index;
    }

    @Override
    public boolean matches(PathEntry key) {
        if (super.matches(key)) {
            return ((ArrayEntry) key).getIndex() == this.index;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return String.valueOf(index);
    }
}
