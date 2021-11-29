package org.meeuw.json.grep;

import lombok.extern.java.Log;

import java.io.*;
import java.util.Iterator;
import java.util.StringJoiner;
import java.util.concurrent.*;

import org.meeuw.json.*;
import org.meeuw.json.grep.matching.NeverPathMatcher;
import org.meeuw.json.grep.matching.PathMatcher;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;

/**
 * jsonsed. To search/replace in json streams.
 * @since 0.10
 */
@Log
public class Sed  implements Iterator<ParseEvent> {

    private static final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool();


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

    public int toGenerator(JsonGenerator generator) throws IOException {
        int i = 0;
        while (hasNext()) {
            ParseEvent next = next();
            next.toGenerator(generator);
            i++;
        }
        return i;
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

    public static OutputStream transform(OutputStream to, PathMatcher pathMatcher) throws IOException {
        PipedInputStream in = new PipedInputStream();
        final Future<?>[] future = new Future[1];
        PipedOutputStream out = new PipedOutputStream(in) {
            @Override
            public void close() throws IOException {
                super.close();
                try {
                    future[0].get();
                } catch (ExecutionException e) {
                    throw new IOException(e);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new IOException(e);

                }
            }
        };

        Callable<Void> callable = () -> {
            try (JsonGenerator generator = Util.getJsonFactory().createGenerator(to);
                 JsonParser parser = Util.getJsonParser(in)
            ){
                Sed sed = new Sed(pathMatcher, parser);
                int events = sed.toGenerator(generator);
                log.fine(() -> "Generated " + events + " events");

            }
            return null;
        };


        future[0] = EXECUTOR_SERVICE.submit(callable);
        return out;
    }
}
