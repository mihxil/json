package org.meeuw.json;

/**
 * Representation of one entry in the current 'path'.
 */
public interface PathEntry {

    void appendTo(StringBuilder builder);

}
