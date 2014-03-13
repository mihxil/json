package org.meeuw.json;

import java.util.ArrayDeque;

/**
* @author Michiel Meeuwissen
* @since 0.4
*/
public class Path extends ArrayDeque<PathEntry> {

    public Path() {

    }
    public Path(Path copy) {
        super(copy);
    }
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (PathEntry pe : this) {
            pe.appendTo(builder);
        }
        return builder.toString();
    }
}
