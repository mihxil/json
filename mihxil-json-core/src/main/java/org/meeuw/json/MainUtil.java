package org.meeuw.json;

import java.io.IOException;
import java.util.function.Consumer;

import org.apache.commons.cli.*;
import org.meeuw.util.Manifests;

public class MainUtil {

    private MainUtil() {

    }

    public static String version() throws IOException {
        return Manifests.read("ProjectVersion");
    }

    public static void ignoreArrays(Options options){
        options.addOption(new Option("i", "ignoreArrays", false, "Ignore arrays (no need to match those)"));
    }

    public static void debug(Options options) {
        options.addOption(new Option("d", "debug", false, "Debug"));
    }


    public static CommandLine commandLine(
        String name,
        String argsDescription,
        Consumer<Options> addOptions,
        String[] argv) throws IOException, ParseException {
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
        if (cl.hasOption("help") || cl.getArgList().isEmpty()) {
            System.out.println(name + " - " + version() + " - See https://github.com/mihxil/json");
            HelpFormatter formatter = new HelpFormatter();
            formatter.setWidth(100);
            formatter.printHelp(
                name + " [OPTIONS] " + argsDescription,
                options);

            exit = true;
        }

        if (exit) {
            System.exit(0);
        }

        return cl;
     }
}
