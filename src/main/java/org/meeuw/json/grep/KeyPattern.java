package org.meeuw.json.grep;

import org.meeuw.json.PathEntry;

/**
 * A key pattern matches one key in a json object.
 */
interface KeyPattern {
    boolean matches(PathEntry key);
}
