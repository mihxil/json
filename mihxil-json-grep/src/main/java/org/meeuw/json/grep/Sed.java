package org.meeuw.json.grep;

import java.io.IOException;
import java.util.Iterator;
import java.util.StringJoiner;

import org.meeuw.json.JsonIterator;
import org.meeuw.json.ParseEvent;
import org.meeuw.json.grep.matching.NeverPathMatcher;
import org.meeuw.json.grep.matching.PathMatcher;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;

/**
 * jsonsed. To search/replace in json streams.
 * @since 0.10
 */
public class Sed  implements Iterator<ParseEvent> {

    private final PathMatcher matcher;

    final JsonIterator wrapped;

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

    public void toGenerator(JsonGenerator generator) throws IOException {
        while (hasNext()) {
            next().toGenerator(generator);
        }
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
