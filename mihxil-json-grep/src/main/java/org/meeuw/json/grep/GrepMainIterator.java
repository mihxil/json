package org.meeuw.json.grep;

import java.util.Iterator;

/**
 * @author Michiel Meeuwissen
 * @since 0.8
 */
public interface GrepMainIterator extends Iterator<GrepMainRecord> {
    long getMaxRecordSize();
}
