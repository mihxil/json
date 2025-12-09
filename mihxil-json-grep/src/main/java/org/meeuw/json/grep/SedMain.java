package org.meeuw.json.grep;

import tools.jackson.core.*;

import java.io.*;
import java.util.List;

import org.apache.commons.cli.*;
import org.meeuw.json.MainUtil;
import org.meeuw.json.Util;
import org.meeuw.json.grep.matching.PathMatcher;
import org.meeuw.json.grep.parsing.Parser;

/**
 * SedMain is a wrapper around {@link Sed},
 * @author Michiel Meeuwissen
 * @since 0.10
 */
public class SedMain {


    private final PathMatcher matcher;
    private final boolean pretty;

    public SedMain(PathMatcher pathMatcher, boolean pretty) {
        this.matcher = pathMatcher;
        this.pretty = pretty;
    }


    public static void main(String[] argv) throws IOException, ParseException {
        CommandLine cl = MainUtil.commandLine("jsonsed", "<pathMatcher expression> [<INPUT FILE>|-] [<OUTPUT FILE>|-]",
            (options) -> {
                options.addOption(new Option("f", "format", false, "Pretty print output"));
                MainUtil.ignoreArrays(options);
                MainUtil.debug(options);
            },
            1,
            argv);

        String[] args = cl.getArgs();
        final List<String> argList = cl.getArgList();
        boolean ignoreArrays = cl.hasOption("ignoreArrays");

        SedMain main = new SedMain(Parser.parsePathMatcherChain(args[0], ignoreArrays, false, null), cl.hasOption("format"));

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
        JsonParser parser = Util.getJsonParser(in);
        Sed sed = new Sed(matcher, parser);
        ObjectWriteContext writeContext = pretty ? Util.prettyWriteContext() : ObjectWriteContext.empty();

        try (JsonGenerator generator = Util.getJsonFactory()
            .createGenerator(writeContext, out)) {
            sed.toGenerator(generator);
        }
    }
}
