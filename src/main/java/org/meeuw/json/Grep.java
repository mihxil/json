package org.meeuw.json;

import org.apache.commons.cli.*;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.regex.Pattern;

/**
 * jsongrep. To search in json streams. You can match on the keys
 * E.g. 'rows.*.id' will result in only all 'id' values in a json structure.
 * Try the commandline option -help for an overview of all features.
 */
public class Grep extends AbstractJsonReader {

    private final PathMatcher matcher;
    private final PrintStream output;

    private Output outputFormat = Output.PATHANDVALUE;

    private String sep = "\n";
    private String recordsep = "\n";
    private PathMatcher recordMatcher = new NeverPathMatcher();

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
                String value = jp.getText();
                if (recordMatcher.matches(path, value)) {
                    output.print(recordsep);
                }
                if (matcher.matches(path, value)) {
                    switch (outputFormat) {
                        case PATHANDVALUE:
                            output.print(join(path) + "=" + value + sep);
                            break;
                        case KEYANDVALUE:
                            output.print(path.peekLast() + "=" + value + sep);
                            break;
                        case VALUE:
                            output.print(jp.getText() + sep);
                            break;
                    }
                }

                break;

        }
    }

    @Override
    protected void ready() {
        output.close();
    }

    public Output getOutputFormat() {
        return outputFormat;
    }

    public void setOutputFormat(Output outputFormat) {
        this.outputFormat = outputFormat;
    }

    public String getSep() {
        return sep;
    }

    public void setSep(String sep) {
        this.sep = sep;
    }

    public String getRecordsep() {
        return recordsep;
    }

    public void setRecordsep(String recordsep) {
        this.recordsep = recordsep;
    }

    public PathMatcher getRecordMatcher() {
        return recordMatcher;
    }

    public void setRecordMatcher(PathMatcher recordMatcher) {
        this.recordMatcher = recordMatcher;
    }

    protected static interface KeyPattern {
        boolean matches(PathEntry key);
    }
    protected static class PreciseMatch implements  KeyPattern {
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
    protected static class Wildcard implements  KeyPattern {

        @Override
        public boolean matches(PathEntry key) {
            return true;
        }

        @Override
        public String toString() {
            return "*";
        }
    }

    protected static interface PathMatcher {

        boolean matches(Deque<PathEntry> path, String value);
    }

    protected static abstract class KeysMatcher implements PathMatcher {
        @Override
        final public boolean matches(Deque<PathEntry> path, String value) {
            return matches(path);
        }
        protected abstract  boolean matches(Deque<PathEntry> path);

    }

    protected static abstract class ValueMatcher implements PathMatcher {
        @Override
        final public boolean matches(Deque<PathEntry> path, String value) {
            return matches(value);
        }

        protected abstract boolean matches(String value);

    }
    protected static class ValueRegexpMatcher extends ValueMatcher {
        private final Pattern pattern;

        public ValueRegexpMatcher(Pattern pattern) {
            this.pattern = pattern;
        }

        @Override
        protected boolean matches(String value) {
            return pattern.matcher(value).matches();
        }
    }

    protected static class ValueEqualsMatcher extends ValueMatcher {
        private final String test;

        public ValueEqualsMatcher(String test) {
            this.test = test;
        }

        @Override
        protected boolean matches(String value) {
            return test.equals(value);
        }
    }


    protected static class SinglePathMatcher extends KeysMatcher {
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

    protected static class PathMatcherOrChain implements PathMatcher {
        private final PathMatcher[] matchers;

        public PathMatcherOrChain(PathMatcher... matchers) {
            this.matchers = matchers;
        }

        @Override
        public boolean matches(Deque<PathEntry> path, String value) {
            for (PathMatcher matcher : matchers) {
                if (matcher.matches(path, value)) return true;
            }
            return false;
        }
    }

    protected static class PathMatcherAndChain implements PathMatcher {
        private final PathMatcher[] matchers;

        public PathMatcherAndChain(PathMatcher... matchers) {
            this.matchers = matchers;
        }

        @Override
        public boolean matches(Deque<PathEntry> path, String value) {
            for (PathMatcher matcher : matchers) {
                if (! matcher.matches(path, value)) return false;
            }
            return true;
        }
    }
    protected  static class NeverPathMatcher implements PathMatcher {

        @Override
        public boolean matches(Deque<PathEntry> path, String value) {
            return false;
        }
    }

    protected static PathMatcher parsePathMatcherChain(String arg) {
        String[] split = arg.split(",");
        if (split.length == 1) return parsePathMatcher(arg);
        ArrayList<PathMatcher> list = new ArrayList<PathMatcher>(split.length);
        for (String s : split) {
            list.add(parsePathMatcher(s));
        }
        return new PathMatcherOrChain(list.toArray(new PathMatcher[list.size()]));

    }
    protected static PathMatcher parsePathMatcher(String arg) {
        String[] split = arg.split("~", 2);
        if (split.length == 2) {
            return new PathMatcherAndChain(
                    parseKeysMatcher(split[0]),
                    new ValueRegexpMatcher(Pattern.compile(split[1])));
        }
        split = arg.split("=", 2);
        if (split.length == 2) {
            return new PathMatcherAndChain(
                    parseKeysMatcher(split[0]),
                    new ValueEqualsMatcher(split[1]));
        }
        // >, <, operators...

        return parseKeysMatcher(split[0]);

    }

    protected static PathMatcher parseKeysMatcher(String arg) {
        String[] split = arg.split("\\.");
        ArrayList<KeyPattern> list = new ArrayList<KeyPattern>(split.length);
        for (String s : split) {
            list.add(parseKeyPattern(s));
        }
        return new SinglePathMatcher(list.toArray(new KeyPattern[list.size()]));
    }

    protected static KeyPattern parseKeyPattern(String arg) {
        if ("*".equals(arg)) return new Wildcard();
        return new PreciseMatch(arg);
    }

    private enum Output {
        PATHANDVALUE,
        KEYANDVALUE,
        VALUE
    }

    public static void main(String[] argv) throws IOException, ParseException {
        CommandLineParser parser = new BasicParser();
        Options options = new Options().addOption(new Option("help", "print this message"));
        options.addOption(new Option("output", true, "Output format, one of " + Arrays.asList(Output.values())));
        options.addOption(new Option("sep", true, "Separator (defaults to newline)"));
        options.addOption(new Option("record", true, "Record pattern (default to no matching at all)"));
        options.addOption(new Option("recordsep", true, "Record separator"));
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
        if (cl.hasOption("output")) {
            grep.setOutputFormat(Output.valueOf(cl.getOptionValue("output").toUpperCase()));
        }
        if (cl.hasOption("sep")) {
            grep.setSep(cl.getOptionValue("sep"));
        }
        if (cl.hasOption("recordsep")) {
            grep.setRecordsep(cl.getOptionValue("recordsep"));
        }
        if (cl.hasOption("record")) {
            grep.setRecordMatcher(parsePathMatcherChain(cl.getOptionValue("record")));
        }


        InputStream in = getInput(args, 1);

        grep.read(in);
        in.close();
    }

}
