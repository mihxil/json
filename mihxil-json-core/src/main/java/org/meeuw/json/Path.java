package org.meeuw.json;

import java.util.ArrayDeque;
import java.util.Arrays;

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
	public Path(PathEntry... entries) {
		super();
		addAll(Arrays.asList(entries));
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
