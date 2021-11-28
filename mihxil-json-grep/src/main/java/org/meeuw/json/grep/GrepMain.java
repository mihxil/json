package org.meeuw.json.grep;

import lombok.Getter;
import lombok.Setter;

import java.io.*;
import java.util.*;

import org.apache.commons.cli.*;
import org.meeuw.json.Util;
import org.meeuw.json.grep.matching.PathMatcher;
import org.meeuw.json.grep.parsing.Parser;
import org.meeuw.util.Manifests;
import org.meeuw.util.MaxOffsetIterator;

import com.fasterxml.jackson.core.JsonParser;

/**
 * GrepMain is a wrapper around {@link Grep}, it arranges output,and record collection and sorting.
 *
 * @author Michiel Meeuwissen
 * @since 0.4
 */
public class GrepMain {


    public enum Output {
        PATHANDVALUE(false) {
            @Override
            void toBuilder(StringBuilder builder, GrepEvent match) {
                builder.append(match.getPath().toString());
                builder.append('=');
                builder.append(match.getValue());
            }
        },

        PATHANDFULLVALUE(true) {
            @Override
            void toBuilder(StringBuilder builder, GrepEvent match) {
                builder.append(match.getPath().toString());
                builder.append('=');
                builder.append(match.getNode());
            }
        },
        KEYANDVALUE(false) {
            @Override
            void toBuilder(StringBuilder builder, GrepEvent match) {
                builder.append(match.getPath().peekLast());
                builder.append('=');
                builder.append(match.getValue());
            }
        },
        KEYANDFULLVALUE(true) {
            @Override
            void toBuilder(StringBuilder builder, GrepEvent match) {
                builder.append(match.getPath().peekLast());
                builder.append('=');
                builder.append(match.getNode());
            }
        },
        PATH(false) {
            @Override
            void toBuilder(StringBuilder builder, GrepEvent match) {
                builder.append(match.getPath().toString());
            }
        },
        KEY(false) {
            @Override
            void toBuilder(StringBuilder builder, GrepEvent match) {
                builder.append(match.getPath().peekLast());
            }
        },
        VALUE(false) {
            @Override
            void toBuilder(StringBuilder builder, GrepEvent match) {
                builder.append(match.getValue());
            }
        },
        FULLVALUE(true) {
            @Override
            void toBuilder(StringBuilder builder, GrepEvent match) {
                builder.append(match.getNode());
            }
        };
        private final boolean needsObject;

        Output(boolean needsObject) {
            this.needsObject = needsObject;
        }
        public boolean needsObject() {
            return needsObject;
        }
        abstract void toBuilder(StringBuilder builder, GrepEvent event);
    }

    @Getter
    @Setter
    Output outputFormat = Output.PATHANDVALUE;

    @Setter
    @Getter
    private String sep = "\n";

    @Setter
    @Getter
    private String recordsep = "\n";

    @Getter
    private final PathMatcher matcher;

    @Getter
    @Setter
    PathMatcher recordMatcher;

    @Setter
    boolean sortFields = true;

    @Setter
    @Getter
    private Long max = null;

    private Long previousMaxRecordSize = null;


    public GrepMain(PathMatcher pathMatcher) {
        this.matcher = pathMatcher;
    }



    public GrepMainIterator iterate(JsonParser in) {
        final GrepMainIteratorImpl wrapped = new GrepMainIteratorImpl(this, in);
        final Iterator<GrepMainRecord> maxoffset = new MaxOffsetIterator<>(wrapped, max);
        return new GrepMainIterator() {
            @Override
            public long getMaxRecordSize() {
                return wrapped.getMaxRecordSize();
            }
            @Override
            public boolean hasNext() {
                return maxoffset.hasNext();
            }
            @Override
            public GrepMainRecord next() {
                return maxoffset.next();
            }
        };
    }
    public <T extends OutputStream> T read(JsonParser in, T out) throws IOException {
        PrintStream output = new PrintStream(out);
        GrepMainIterator iterator = iterate(in);
        iterator.forEachRemaining((record) -> {
            output.print(record.toString());
            output.print(recordsep);
        });
        output.close();
        previousMaxRecordSize =  iterator.getMaxRecordSize();
        return out;
    }
    public Long  getPreviousMaxRecordSize() {
        return previousMaxRecordSize;
    }


    public <T extends OutputStream> T read(Reader in, T out) throws IOException {
        return read(Util.getJsonParser(in), out);
    }
    public <T extends OutputStream> T read(InputStream in, T out) throws IOException {
        return read(Util.getJsonParser(in), out);
    }

    public String read(Reader in) throws IOException {
        return read(Util.getJsonParser(in), new ByteArrayOutputStream()).toString();
    }

    public String read(InputStream in) throws IOException {
        return read(Util.getJsonParser(in), new ByteArrayOutputStream()).toString();
    }

    public Iterator<GrepMainRecord> iterate(InputStream in) {
        return iterate(Util.getJsonParser(in));
    }

    public static String version() throws IOException {
        return Manifests.read("ProjectVersion");
    }

    public static void main(String[] argv) throws IOException, ParseException {
        CommandLineParser parser = new DefaultParser();
        Options options = new Options().addOption(new Option("help", "print this message"));
        options.addOption(new Option("output", true, "Output format, one of " + Arrays.asList(Output.values())));
        options.addOption(new Option("sep", true, "Separator (defaults to newline)"));
        options.addOption(new Option("record", true, "Record pattern (default to no matching at all). On match, a record separator will be outputted."));
        options.addOption(new Option("recordsep", true, "Record separator"));
        options.addOption(new Option("sortfields", true, "Sort the fields of a found 'record', according to the order of the matchers."));
        options.addOption(new Option("max", false, "Max number of records"));
        options.addOption(new Option("version", false, "Print version"));
        options.addOption(new Option("ignoreArrays", false, "Ignore arrays (no need to match those)"));

        options.addOption(new Option("debug", false, "Debug"));
        CommandLine cl = parser.parse(options, argv, true);
        String[] args = cl.getArgs();
        if (cl.hasOption("version")) {
            System.out.println(version());
            System.exit(0);
        }
        final List<String> argList = cl.getArgList();
        if (cl.hasOption("help") || argList.isEmpty()) {
            System.out.println("jsongrep - " + version() + " - See https://github.com/mihxil/json");
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(
                    "jsongrep [OPTIONS] <pathMatcher expression> [<INPUT FILE>|-]",
                    options);

            System.exit(0);
        }

        boolean ignoreArrays = cl.hasOption("ignoreArrays");

        Output output = Output.PATHANDVALUE;
        if (cl.hasOption("output")) {
            output = Output.valueOf(cl.getOptionValue("output").toUpperCase());
        }
        String record = null;
        if (cl.hasOption("record")) {
            record = cl.getOptionValue("record");
        }

        GrepMain main = new GrepMain(Parser.parsePathMatcherChain(args[0], ignoreArrays, output.needsObject(), record));

        main.setOutputFormat(output);

        if (cl.hasOption("sep")) {
            main.setSep(cl.getOptionValue("sep"));
        }
        if (cl.hasOption("recordsep")) {
            main.setRecordsep(cl.getOptionValue("recordsep"));
        }
        if (record != null) {
            main.setRecordMatcher(Parser.parsePathMatcherChain(record));
        }

        if (cl.hasOption("sortfields")) {
            main.setSortFields(Boolean.parseBoolean(cl.getOptionValue("sortfields")));
        }

        if (cl.hasOption("max")) {
            main.setMax(Long.valueOf(cl.getOptionValue("max")));
        }

        if (cl.hasOption("debug")) {
            System.out.println(main.matcher);
            System.exit(0);
            return;
        }

        try (InputStream in = Util.getInput(argList.toArray(new String[0]), 1)) {
            main.read(in, System.out);
        }
        System.exit(0);
    }
}
