package org.meeuw.json.grep.matching;

import java.util.List;

import org.meeuw.json.PathEntry;

/**
 * @author Michiel Meeuwissen
 * @since 0.7
 */
public class AnyDepthMatcher implements KeysPattern {
    @Override
    public int matchCounts(List<PathEntry> entries) {
        return entries.size();
    }
}
