package org.meeuw.json.grep.matching;

import java.util.List;

import org.meeuw.json.PathEntry;

/**
 * @author Michiel Meeuwissen
 * @since 0.7
 */
public abstract class AbstractKeyPattern implements KeyPattern {
    @Override
    public int matchCounts(List<PathEntry> entries) {
        boolean matches = entries.size() > 0 && matches(entries.get(0));
        return matches ? 1 : -1;
    }

}
