package org.meeuw.json.grep;

import lombok.Getter;

import java.io.*;
import java.util.List;

import org.apache.commons.cli.*;
import org.meeuw.json.Util;
import org.meeuw.json.grep.matching.PathMatcher;
import org.meeuw.json.grep.parsing.Parser;
import org.meeuw.util.Manifests;

import com.fasterxml.jackson.core.JsonGenerator;

/**
 * SedMain is a wrapper around {@link Sed},
 * @author Michiel Meeuwissen
 * @since 0.10
 */
public class SedMain {


    @Getter
    private final PathMatcher matcher;


    public SedMain(PathMatcher pathMatcher) {
        this.matcher = pathMatcher;
    }


    public static String version() throws IOException {
        return Manifests.read("ProjectVersion");
    }

    public static void main(String[] argv) throws IOException, ParseException {
        CommandLineParser parser = new DefaultParser();
        Options options = new Options().addOption(new Option("help", "print this message"));

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


        SedMain main = new SedMain(Parser.parsePathMatcherChain(args[0], ignoreArrays, false, null));


        if (cl.hasOption("debug")) {
            System.out.println(main.matcher);
            System.exit(0);
            return;
        }

        try (InputStream in = Util.getInput(argList.toArray(new String[0]), 1);
             OutputStream out = Util.getOutput(argList.toArray(new String[0]), 2);
        ) {
            main.read(in, out);
        }
        System.exit(0);
    }

    private void read(InputStream in, OutputStream out) throws IOException {
        Sed sed = new Sed(matcher, Util.getJsonParser(in));
        try (JsonGenerator generator = Util.getJsonFactory().createGenerator(out)) {
            sed.toGenerator(generator);
        }
    }
}
