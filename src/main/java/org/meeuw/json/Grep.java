package org.meeuw.json;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Deque;

/**
 *
 */
public class Grep extends AbstractJsonReader {

    private final PathMatcher matcher;
    private final PrintStream output;

    public Grep(OutputStream out, PathMatcher matcher) {
        this.matcher = matcher;
        this.output = new PrintStream(out);
    }

    @Override
    protected void handleToken(JsonParser jp, JsonToken token, Deque<PathEntry> path) throws IOException {
        switch (token) {
            case VALUE_STRING:
            case VALUE_NUMBER_INT:
            case VALUE_NUMBER_FLOAT:
            case VALUE_TRUE:
            case VALUE_FALSE:
            case VALUE_NULL:
                if (matcher.matches(path)) {
                    output.println(join(path) + "=" + jp.getText());
                }
                break;

        }
    }

    @Override
    protected void ready() {
        output.close();
    }



    public static interface KeyPattern {
        boolean matches(PathEntry key);
    }
    public static class PreciseMatch implements  KeyPattern {
        private final String key;

        public PreciseMatch(String key) {
            this.key = key;
        }

        @Override
        public boolean matches(PathEntry key) {
            return this.key.equals(key.toString());
        }
        @Override
        public String toString() {
            return key;
        }
    }
    public static class Wildcard implements  KeyPattern {

        @Override
        public boolean matches(PathEntry key) {
            return true;
        }

        @Override
        public String toString() {
            return "*";
        }
    }

    public static interface PathMatcher {

        boolean matches(Deque<PathEntry> path);
    }

    public static class SinglePathMatcher implements PathMatcher {
        private final KeyPattern[] pathPattern;

        public SinglePathMatcher(KeyPattern... pathPattern) {
            this.pathPattern = pathPattern;
        }

        @Override
        public boolean matches(Deque<PathEntry> path) {
            if (path.size() != pathPattern.length) return false;
            int i = 0;
            for (PathEntry e : path) {
                if (! pathPattern[i++].matches(e)) return false;
            }
            return true;
        }
    }


    public static void main(String[] argv) throws IOException {
        Grep grep = new Grep(System.out, new SinglePathMatcher(new PreciseMatch("rows"), new PreciseMatch("sortDate")));

        InputStream in = getInput(argv);

        grep.read(in);
        in.close();
    }

}
