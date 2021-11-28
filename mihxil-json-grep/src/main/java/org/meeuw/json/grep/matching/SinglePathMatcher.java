package org.meeuw.json.grep.matching;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.meeuw.json.ArrayEntry;
import org.meeuw.json.PathEntry;

/**
 * A single path matches precisely one 'path'. For multiple matches we'd wrap them in {@link PathMatcherOrChain} or {@link PathMatcherAndChain}
 */
public class SinglePathMatcher extends KeysMatcher {
    private final KeysPattern[] pathPattern;

    private final boolean ignoreArrays;

    public SinglePathMatcher(KeysPattern... pathPattern) {
        this(false, pathPattern);
    }

    public SinglePathMatcher(boolean ignoreArrays, KeysPattern... pathPattern) {
        this.ignoreArrays = ignoreArrays;
        this.pathPattern = pathPattern;
    }

    @Override
    public boolean matches(final List<PathEntry> path) {

        if (ignoreArrays) {
            List<PathEntry> withoutArrays =
                path.stream().filter(e -> ! (e instanceof ArrayEntry)).collect(Collectors.toList());
            return matches(Arrays.asList(pathPattern), withoutArrays);
        } else {
            return matches(Arrays.asList(pathPattern), path);
        }
    }


    private boolean matches(List<KeysPattern> patterns, List<PathEntry> entries) {
        if (patterns.size() > 0) {
            KeysPattern first = patterns.get(0);
            int matchCounts = first.matchCounts(entries);
            if (matchCounts >= 0) {
                for (int i = 0; i < matchCounts; i++) {
                    if (i == entries.size() - 1) {
                        return patterns.size() == 1;
                    }
                    if (matches(patterns.subList(1, patterns.size()), entries.subList(i + 1, entries.size()))) {
                        return true;
                    }
                }
                return false;
            } else {
                return false;
            }

        } else {
            return entries.size() == 0;
        }
    }


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (KeysPattern p : pathPattern) {
            if (builder.length() > 0 && ! (p instanceof ArrayEntryMatch)) {
                builder.append(".");
            }
            builder.append(p);
        }
        return builder.toString();
    }

    public KeysPattern[] getPatterns() {
        return pathPattern;
    }
}
