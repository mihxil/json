package org.meeuw.json;

import java.io.IOException;
import java.util.function.Consumer;

import org.apache.commons.cli.*;
import org.apache.commons.cli.help.HelpFormatter;
import org.meeuw.util.Manifests;

public class MainUtil {

    private MainUtil() {

    }

    public static String version() {
        try {
            return Manifests.read("ProjectVersion");
        } catch (IOException e) {
            return "<unknown>";
        }
    }

    public static void ignoreArrays(Options options){
        options.addOption(new Option("i", "ignoreArrays", false, "Ignore arrays (no need to match those)"));
    }

    public static void debug(Options options) {
        options.addOption(new Option("d", "debug", false, "Debug"));
    }


    public static CommandLine commandLine(
        String name,
        String header,
        String argsDescription,
        Consumer<Options> addOptions,
        int expectedNumberOfArguments,
        String[] argv) throws ParseException, IOException {
        CommandLineParser parser = new DefaultParser();
        Options options = new Options();
        options.addOption(new Option("?", "help", false, "print this message"));
        options.addOption(new Option("v", "version", false, "Print version"));
        addOptions.accept(options);
        CommandLine cl = parser.parse(options, argv, true);

        boolean exit = false;
        if (cl.hasOption("version")) {
            System.out.println(version());
            exit = true;
        }
        if (cl.hasOption("help") || cl.getArgList().size() < expectedNumberOfArguments) {
            HelpFormatter formatter = HelpFormatter.builder()
                .setShowSince(false)
                .get();


            formatter.printHelp(
                name + " [OPTIONS] " + argsDescription,
                header,
                options,
                "See https://github.com/mihxil/json",
                false);

            exit = true;
        }

        if (exit) {
            System.exit(0);
        }

        return cl;
     }
}
