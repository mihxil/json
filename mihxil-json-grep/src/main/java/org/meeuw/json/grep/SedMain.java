package org.meeuw.json.grep;

import java.io.*;
import java.util.List;
import java.util.function.Consumer;

import org.apache.commons.cli.*;
import org.meeuw.json.MainUtil;
import org.meeuw.json.Util;
import org.meeuw.json.grep.matching.PathMatcher;
import org.meeuw.json.grep.parsing.Parser;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;

/**
 * SedMain is a wrapper around {@link Sed},
 * @author Michiel Meeuwissen
 * @since 0.10
 */
public class SedMain {


    private final PathMatcher matcher;


    public SedMain(PathMatcher pathMatcher) {
        this.matcher = pathMatcher;
    }


    public static void main(String[] argv) throws IOException, ParseException {
        CommandLine cl = MainUtil.commandLine("jsonsed", "<pathMatcher expression> [<INPUT FILE>|-] [<OUTPUT FILE>|-]",
            (options) -> {
                options.addOption(new Option("f", "format", false, "Pretty print output"));
                MainUtil.ignoreArrays(options);
                MainUtil.debug(options);
            },
            argv);

        String[] args = cl.getArgs();
        final List<String> argList = cl.getArgList();
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
            main.read(in, out, (generator) -> {
                if (cl.hasOption("format")) {
                    generator.setPrettyPrinter(new DefaultPrettyPrinter());
                }
            });
        }
        System.exit(0);
    }

    private void read(InputStream in, OutputStream out, Consumer<JsonGenerator> jsonGeneratorConsumer) throws IOException {
        Sed sed = new Sed(matcher, Util.getJsonParser(in));
        try (JsonGenerator generator = Util.getJsonFactory().createGenerator(out)) {
            jsonGeneratorConsumer.accept(generator);
            sed.toGenerator(generator);
        }
    }
}
