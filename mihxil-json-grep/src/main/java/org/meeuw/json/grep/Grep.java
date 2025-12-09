package org.meeuw.json.grep;

import lombok.Getter;
import lombok.Setter;
import tools.jackson.core.JsonParser;

import java.util.*;

import org.meeuw.json.JsonIterator;
import org.meeuw.json.ParseEvent;
import org.meeuw.json.grep.matching.NeverPathMatcher;
import org.meeuw.json.grep.matching.PathMatcher;

/**
 * jsongrep. To search in json streams. You can match on the keys
 * E.g. 'rows.*.id' will result in only all 'id' values in a json structure.
 * Try the commandline option -help for an overview of all features.
 */
public class Grep implements Iterator<GrepEvent>, Iterable<GrepEvent> {

    // settings
    @Getter
    private final PathMatcher matcher;

    @Getter
    @Setter
    private PathMatcher recordMatcher = new NeverPathMatcher();

    final JsonIterator wrapped;
    private final List<GrepEvent> next = new ArrayList<>();

    @lombok.Builder
    public Grep(PathMatcher matcher, JsonParser jp) {
        this.matcher = matcher == null ? new NeverPathMatcher() : matcher;
        this.wrapped = new JsonIterator(jp,
                this.matcher.needsKeyCollection(),
                this.matcher.needsObjectCollection());
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
        if(next.isEmpty()) {
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
                        if (recordMatcher != null) {
                            int recordWeight = recordMatcher.matches(event).getWeight();
                            if (recordWeight > 0) {
                                next.add(new GrepEvent(event, GrepEvent.Type.RECORD, recordWeight));
                            }
                        }
                        PathMatcher.MatchResult result = matcher.matches(event);
                        if (result.getWeight() > 0) {
                            next.add(new GrepEvent(result.getEvent(), result.getWeight()));
                        }
                }
            }
        }
    }



    @Override
    public Iterator<GrepEvent> iterator() {
        return this;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Grep.class.getSimpleName() + "[", "]")
            .add("matcher=" + matcher)
            .add("recordMatcher=" + recordMatcher)
            .toString();
    }
}
