package org.meeuw.json;

import java.util.*;

import com.fasterxml.jackson.core.JsonPointer;

/**
* @author Michiel Meeuwissen
* @since 0.4
*/
public class Path extends AbstractList<PathEntry> {

    private final List<PathEntry> backing = new ArrayList<PathEntry>();

    public Path() {

    }

    @Override
    public int size() {
        return backing.size();
    }

    @Override
    public PathEntry get(int index) {
        return backing.get(index);
    }

    @Override
    public PathEntry set(int i, PathEntry e) {
        return backing.set(i, e);
    }
    @Override
    public void add(int i, PathEntry e) {
        backing.add(i, e);
    }
    @Override
    public PathEntry remove(int i) {
        return backing.remove(i);
    }
    public Path(Path copy) {
        backing.addAll(copy.backing);
    }
	public Path(PathEntry... entries) {
        backing.addAll(Arrays.asList(entries));
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

    public PathEntry peekLast() {
        return backing.size() == 0 ? null : backing.get(backing.size() - 1);


    }

    public PathEntry pollLast() {
        return backing.size() == 0 ? null : backing.remove(backing.size() - 1);
    }

    public void addLast(ArrayEntry inc) {
        backing.add(inc);
    }

    /**
     * Represents this path as a {@link JsonPointer}, so it can be used to look up the corresponding
     * {@code JsonNode} in the tree it originated from (e.g. via {@code JsonNode#at(JsonPointer)}).
     */
    public JsonPointer toJsonPointer() {
        StringBuilder builder = new StringBuilder();
        for (PathEntry pe : this) {
            builder.append('/');
            if (pe instanceof ArrayEntry) {
                builder.append(((ArrayEntry) pe).getIndex());
            } else {
                builder.append(pe.toString().replace("~", "~0").replace("/", "~1"));
            }
        }
        return JsonPointer.compile(builder.toString());
    }
}
