package org.meeuw.json.grep.matching;

import org.meeuw.json.PathEntry;

/**
 * A key pattern matches one key in a json object.
 */
public interface KeyPattern {
    boolean matches(PathEntry key);
}
