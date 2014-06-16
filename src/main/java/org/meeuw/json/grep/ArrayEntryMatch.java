package org.meeuw.json.grep;

import org.meeuw.json.ArrayEntry;
import org.meeuw.json.PathEntry;

/**
* @author Michiel Meeuwissen
* @since ...
*/
class ArrayEntryMatch implements KeyPattern {
    @Override
    public boolean matches(PathEntry key) {
        return key instanceof ArrayEntry;
    }
    @Override
    public String toString() {
        return "[*]";
    }

}
