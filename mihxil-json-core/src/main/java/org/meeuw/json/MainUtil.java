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


    public static CommandLine commandLine(
        String name,
        String argsDescription,
        Consumer<Options> addOptions,
        String[] argv) throws IOException, ParseException {
        CommandLineParser parser = new DefaultParser();
        Options options = new Options();
        options.addOption(new Option("help", "print this message"));
        options.addOption(new Option("version", false, "Print version"));
        addOptions.accept(options);
        CommandLine cl = parser.parse(options, argv, true);

         if (cl.hasOption("version")) {
             System.out.println(version());
             System.exit(0);
         }
         if (cl.hasOption("help") || cl.getArgList().isEmpty()) {
             System.out.println(name + " - " + version() + " - See https://github.com/mihxil/json");
             HelpFormatter formatter = new HelpFormatter();
             formatter.setWidth(100);
             formatter.printHelp(
                 name + " [OPTIONS] " + argsDescription,
                 options);

             System.exit(0);
         }
         return cl;
     }
}
