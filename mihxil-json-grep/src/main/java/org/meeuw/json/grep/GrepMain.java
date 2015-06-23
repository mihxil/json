package org.meeuw.json.grep;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.cli.*;
import org.meeuw.json.Util;
import org.meeuw.json.grep.matching.PathMatcher;
import org.meeuw.json.grep.parsing.Parser;
import org.meeuw.util.Manifests;

import com.fasterxml.jackson.core.JsonParser;

/**
 * @author Michiel Meeuwissen
 * @since 0.4
 */
public class GrepMain {


    public static enum Output {
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

    private Output outputFormat = Output.PATHANDVALUE;

    private final PrintStream output;

    private String sep = "\n";

    private String recordsep = "\n";


    private final PathMatcher matcher;
    private PathMatcher recordMatcher;
    private boolean sortFields = true;


    public GrepMain(PathMatcher pathMatcher, OutputStream output) {
        this.matcher = pathMatcher;
        this.output = new PrintStream(output);
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


    public void setSortFields(boolean sortFields) {
        this.sortFields = sortFields;
    }

    public PathMatcher getMatcher() {
        return matcher;
    }
    private class ResultField implements Comparable<ResultField> {
        final int weight;
        final String value;

        private ResultField(int weight, String value) {
            this.weight = weight;
            this.value = value;
        }

        @Override
        public int compareTo(ResultField o) {
            return weight - o.weight;
        }
    }
    public void read(JsonParser in) throws IOException {
        Grep grep = new Grep(matcher, in);
        if (recordMatcher != null) {
            grep.setRecordMatcher(recordMatcher);
        }
        List<ResultField> fields = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        while (grep.hasNext()) {
            GrepEvent match = grep.next();
            switch (match.getType()) {
                case VALUE:
                    builder.setLength(0);
                    outputFormat.toBuilder(builder, match);
                    break;
                case RECORD:
                    if (fields.size() > 0) {
                        output.print(sort(fields).stream().map(f -> f.value).collect(Collectors.joining(this.getSep())));
                        output.print(recordsep);
                        fields.clear();
                    }
            }
            fields.add(new ResultField(match.getWeight(), builder.toString()));
        }
        if (fields.size() > 0) {
            output.print(sort(fields).stream().map(f -> f.value).collect(Collectors.joining(this.getSep())));
            output.print(recordsep);
        }
        output.close();
    }
    private List<ResultField> sort(List<ResultField> fields) {
        if (recordMatcher != null && sortFields) {
            Collections.sort(fields);
        }
        return fields;
    }

    public void read(Reader in) throws IOException {
        read(Util.getJsonParser(in));
    }
    public void read(InputStream in) throws IOException {
        read(Util.getJsonParser(in));
    }


    public static String version() throws IOException {
        return Manifests.read("ProjectVersion");
    }

    public static void main(String[] argv) throws IOException, ParseException {
        CommandLineParser parser = new BasicParser();
        Options options = new Options().addOption(new Option("help", "print this message"));
        options.addOption(new Option("output", true, "Output format, one of " + Arrays.asList(Output.values())));
        options.addOption(new Option("sep", true, "Separator (defaults to newline)"));
        options.addOption(new Option("record", true, "Record pattern (default to no matching at all). On match, a record separator will be outputted."));
        options.addOption(new Option("recordsep", true, "Record separator"));
        options.addOption(new Option("sortfields", true, "Sort the fields of a found 'record', according to the order of the matchers."));
        options.addOption(new Option("version", false, "Output version"));
        options.addOption(new Option("ignoreArrays", false, "Ignore arrays (no need to match those)"));
        options.addOption(new Option("debug", false, "Debug"));
        CommandLine cl = parser.parse(options, argv, true);
        String[] args = cl.getArgs();
        if (cl.hasOption("version")) {
            System.out.println(version());
            System.exit(1);
        }
        if (cl.hasOption("help") || cl.getArgList().isEmpty()) {
            System.out.println("jsongrep - " + version() + " - See https://github.com/mihxil/json");
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(
                    "jsongrep [OPTIONS] <pathMatcher expression> [<INPUT FILE>|-]",
                    options);

            System.exit(1);
        }

        if (args.length < 1) {
            throw new MissingArgumentException("No pathMatcher expression given");
        }
        boolean ignoreArrays = false;
        if (cl.hasOption("ignoreArrays")) {
            ignoreArrays = true;
        }
        Output output = Output.PATHANDVALUE;
        if (cl.hasOption("output")) {
            output = Output.valueOf(cl.getOptionValue("output").toUpperCase());
        }
        GrepMain main = new GrepMain(Parser.parsePathMatcherChain(args[0], ignoreArrays, output.needsObject()), System.out);

        main.setOutputFormat(output);

        if (cl.hasOption("sep")) {
            main.setSep(cl.getOptionValue("sep"));
        }
        if (cl.hasOption("recordsep")) {
            main.setRecordsep(cl.getOptionValue("recordsep"));
        }
        if (cl.hasOption("record")) {
            main.setRecordMatcher(Parser.parsePathMatcherChain(cl.getOptionValue("record")));
        }

        if (cl.hasOption("sortfields")) {
            main.setSortFields(Boolean.valueOf(cl.getOptionValue("sortfields")));
        }

        if (cl.hasOption("debug")) {
            System.out.println(String.valueOf(main.matcher));
            return;
        }

		List<String> argList = cl.getArgList();
		InputStream in = Util.getInput(argList.toArray(new String[argList.size()]), 1);
        main.read(in);
        in.close();
    }
}
