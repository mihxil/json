package org.meeuw.json.grep;

import java.io.PrintStream;
import java.util.*;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonParser;

/**
 * @author Michiel Meeuwissen
 * @since 0.7
 */
class GrepMainIterator implements Iterator<GrepMainRecord> {

    private final GrepMain grepMain;
    private final Grep grep;
    private final GrepMainRecord next;
    private Boolean hasNext = null;

    GrepMainIterator(GrepMain grepMain, JsonParser in) {
        this.grepMain = grepMain;
        this.grep = new Grep(grepMain.getMatcher(), in);
        this.grep.setRecordMatcher(grepMain.getRecordMatcher());
        this.next = new GrepMainRecord(grepMain.getSep());

    }


    @Override
    public boolean hasNext() {
        findNext();
        return hasNext;
    }

    @Override
    public GrepMainRecord next() {
        findNext();
        if (! hasNext) {
            throw new NoSuchElementException();
        }
        hasNext = null;
        return next;
    }

    private final StringBuilder builder = new StringBuilder();

    private void findNext() {
        if (hasNext == null) {
            next.fields.clear();
            while (grep.hasNext()) {
                GrepEvent match = grep.next();
                switch (match.getType()) {
                    case VALUE:
                        builder.setLength(0);
                        grepMain.outputFormat.toBuilder(builder, match);
                        break;
                    case RECORD:
                        if (next.fields.size() > 0) {
                            sort(next.fields);
                            hasNext = true;
                            return;
                        }
                        continue;

                }
                next.fields.add(new GrepMainRecord.Field(match.getWeight(), builder.toString()));
            }
            if (next.fields.size() > 0) {
                sort(next.fields);
                hasNext = true;
            } else {
                hasNext = false;
            }
        }

    }

    private List<GrepMainRecord.Field> sort(List<GrepMainRecord.Field> fields) {
        if (grepMain.recordMatcher != null && grepMain.sortFields) {
            Collections.sort(fields);
        }
        return fields;
    }
}


