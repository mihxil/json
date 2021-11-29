package org.meeuw.json.grep;

import lombok.Getter;
import lombok.Setter;

import java.io.*;
import java.util.*;

import org.apache.commons.cli.*;
import org.meeuw.json.MainUtil;
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
public class GrepMain  {


    public enum Output {
        PATHANDVALUE(false) {
            @Override
            void toBuilder(StringBuilder builder, GrepEvent match) {
                builder.append(match.getPath().toString());
                builder.append('=');
                builder.append(match.valueOrNodeAsConciseString());
            }
        },

        PATHANDFULLVALUE(true) {
            @Override
            void toBuilder(StringBuilder builder, GrepEvent match) {
                builder.append(match.getPath().toString());
                builder.append('=');
                builder.append(match.getEvent().valueOrNodeAsString());
            }
        },
        KEYANDVALUE(false) {
            @Override
            void toBuilder(StringBuilder builder, GrepEvent match) {
                builder.append(match.getPath().peekLast());
                builder.append('=');
                builder.append(match.valueOrNodeAsConciseString());
            }
        },
        KEYANDFULLVALUE(true) {
            @Override
            void toBuilder(StringBuilder builder, GrepEvent match) {
                builder.append(match.getPath().peekLast());
                builder.append('=');
                builder.append(match.getEvent().valueOrNodeAsString());
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
                builder.append(match.valueOrNodeAsConciseString());
            }
        },
        FULLVALUE(true) {
            @Override
            void toBuilder(StringBuilder builder, GrepEvent match) {
                builder.append(match.valueOrNodeAsString());
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

        CommandLine cl = MainUtil.commandLine(
            "jsongrep",
            "<pathMatcher expression> [<INPUT FILE>|-]",
            (options) -> {
                options.addOption(new Option("o", "output", true, "Output format, one of " + Arrays.asList(Output.values())));
                options.addOption(new Option("s", "sep", true, "Separator (defaults to newline)"));
                options.addOption(new Option("r", "record", true, "Record pattern (default to no matching at all). On match, a record separator will be outputted."));
                options.addOption(new Option("rs", "recordsep", true, "Record separator"));
                options.addOption(new Option("sf", "sortfields", true, "Sort the fields of a found 'record', according to the order of the matchers."));
                options.addOption(new Option("m", "max", false, "Max number of records"));
                MainUtil.ignoreArrays(options);
                options.addOption(new Option("d", "debug", false, "Debug"));

            },argv
        );

        String[] args = cl.getArgs();

        final List<String> argList = cl.getArgList();
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
