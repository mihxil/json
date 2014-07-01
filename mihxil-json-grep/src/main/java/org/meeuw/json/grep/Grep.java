package org.meeuw.json.grep;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.meeuw.json.JsonIterator;
import org.meeuw.json.ParseEvent;
import org.meeuw.json.grep.matching.NeverPathMatcher;
import org.meeuw.json.grep.matching.PathMatcher;

import com.fasterxml.jackson.core.JsonParser;

/**
 * jsongrep. To search in json streams. You can match on the keys
 * E.g. 'rows.*.id' will result in only all 'id' values in a json structure.
 * Try the commandline option -help for an overview of all features.
 */
public class Grep implements Iterator<GrepEvent> {

    // settings
    private final PathMatcher matcher;

    private PathMatcher recordMatcher = new NeverPathMatcher();

    final JsonIterator wrapped;
    private final List<GrepEvent> next = new ArrayList<GrepEvent>();

    public Grep(PathMatcher matcher, JsonParser jp) {
        this.matcher = matcher;
        this.wrapped = new JsonIterator(jp, matcher.needsKeyCollection(), matcher.needsObjectCollection());
    }

    @Override
    public boolean hasNext() {
        findNext();
        return ! next.isEmpty();
    }
    @Override
    public GrepEvent next() {
        findNext();
        if (next.isEmpty()) {
            throw new NoSuchElementException();
        }
        return next.remove(0);
    }
    @Override
    public void remove() {
        wrapped.remove();
    }

    protected void findNext() {
        if( next.isEmpty()) {
            while (wrapped.hasNext() && next.isEmpty()) {
                ParseEvent event = wrapped.next();
                switch (event.getToken()) {
                    case VALUE_STRING:
                    case VALUE_NUMBER_INT:
                    case VALUE_NUMBER_FLOAT:
                    case VALUE_TRUE:
                    case VALUE_FALSE:
                    case VALUE_NULL:
                    case END_ARRAY:
                    case END_OBJECT:
                        String value = event.getValue();
                        if (recordMatcher.matches(event, value)) {
                            next.add(new GrepEvent(event, GrepEvent.Type.RECORD));
                        }
                        if (matcher.matches(event, value)) {
                            next.add(new GrepEvent(event));
                        }
                }
            }
        }
    }




    public PathMatcher getRecordMatcher() {
        return recordMatcher;
    }

    public void setRecordMatcher(PathMatcher recordMatcher) {
        this.recordMatcher = recordMatcher;
    }


}
