package org.meeuw.json;

import org.apache.commons.cli.*;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Deque;

/**
 *
 */
public class Grep extends AbstractJsonReader {

    private final PathMatcher matcher;
    private final PrintStream output;

    public Grep(PathMatcher matcher, OutputStream output) {
        this.matcher = matcher;
        this.output = new PrintStream(output);
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

    public static class PathMatcherChain implements PathMatcher {
        private final PathMatcher[] matchers;

        public PathMatcherChain(PathMatcher[] matchers) {
            this.matchers = matchers;
        }

        @Override
        public boolean matches(Deque<PathEntry> path) {
            for (PathMatcher matcher : matchers) {
                if (matcher.matches(path)) return true;
            }
            return false;
        }
    }

    public static PathMatcher parsePathMatcherChain(String arg) {
        String[] split = arg.split(",");
        if (split.length == 1) return parsePathMatcher(arg);
        ArrayList<PathMatcher> list = new ArrayList<PathMatcher>(split.length);
        for (String s : split) {
            list.add(parsePathMatcher(s));
        }
        return new PathMatcherChain(list.toArray(new PathMatcher[list.size()]));

    }
    public static PathMatcher parsePathMatcher(String arg) {
        String[] split = arg.split("\\.");
        ArrayList<KeyPattern> list = new ArrayList<KeyPattern>(split.length);
        for (String s : split) {
            list.add(parseKeyPattern(s));
        }
        return new SinglePathMatcher(list.toArray(new KeyPattern[list.size()]));
    }

    public static KeyPattern parseKeyPattern(String arg) {
        if ("*".equals(arg)) return new Wildcard();
        return new PreciseMatch(arg);
    }

    public static void main(String[] argv) throws IOException, ParseException {
        CommandLineParser parser = new BasicParser();
        Options options = new Options().addOption(new Option("help", "print this message"));
        CommandLine cl = parser.parse(options, argv, true);
        String[] args = cl.getArgs();
        if (cl.hasOption("help")) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(
                    "jsongrep [OPTIONS] <grep expression> [<INPUT FILE>|-]",
                    options
            );
            System.exit(1);
        }
        if (args.length <1) throw new MissingArgumentException("No grep expression given");
        Grep grep = new Grep(parsePathMatcherChain(args[0]), System.out);


        InputStream in = getInput(args, 1);

        grep.read(in);
        in.close();
    }

}
