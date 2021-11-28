package org.meeuw.json.grep;

import lombok.Getter;

import java.util.Iterator;
import java.util.StringJoiner;

import org.meeuw.json.JsonIterator;
import org.meeuw.json.ParseEvent;
import org.meeuw.json.grep.matching.NeverPathMatcher;
import org.meeuw.json.grep.matching.PathMatcher;

import com.fasterxml.jackson.core.JsonParser;

/**
 * jsonsed. To search in json streams. You can match on the keys
 * E.g. 'rows.*.id' will result in only all 'id' values in a json structure.
 * Try the commandline option -help for an overview of all features.
 */
public class Sed  implements Iterator<ParseEvent> {

    // settings
    @Getter
    private final PathMatcher matcher;

    final JsonIterator wrapped;

    @lombok.Builder
    public Sed(PathMatcher matcher, JsonParser jp) {
        this.matcher = matcher == null ? new NeverPathMatcher() : matcher;
        this.wrapped = new JsonIterator(jp,
                this.matcher.needsKeyCollection(),
                this.matcher.needsObjectCollection());
    }




    @Override
    public String toString() {
        return new StringJoiner(", ", Sed.class.getSimpleName() + "[", "]")
            .add("matcher=" + matcher)
            .toString();
    }

    @Override
    public boolean hasNext() {
        return wrapped.hasNext();
    }

    @Override
    public ParseEvent next() {

        ParseEvent event = wrapped.next();
        PathMatcher.MatchResult matches = matcher.matches(event);
        if (matches.getAsBoolean()) {
            return matches.getEvent();
        } else {
            return event;
        }
    }
}
