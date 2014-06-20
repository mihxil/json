package org.meeuw.json;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Iterator;

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

    public Path parent() {
        if (size() > 1) {
            PathEntry[] parent = new PathEntry[size() - 1];
            Iterator<PathEntry> it = iterator();
            for (int i = 0; i < size() - 1; i++) {
                parent[i] = it.next();
            }
            return new Path(parent);
        } else {
            return null;
        }
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
