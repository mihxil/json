package org.meeuw.json.grep.matching;

import org.meeuw.json.ArrayEntry;
import org.meeuw.json.PathEntry;

/**
* @author Michiel Meeuwissen
* @since ...
*/
public class ArrayEntryMatch extends AbstractKeyPattern {
    @Override
    public boolean matches(PathEntry key) {
        return key instanceof ArrayEntry;
    }
    @Override
    public String toString() {
        return "[*]";
    }

}
