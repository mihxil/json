/*
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package org.meeuw.json;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.cli.*;
import org.meeuw.util.Manifests;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;

/**
 * Pretty prints the json-stream. Depends entirely on jackson.
 * @author Michiel Meeuwissen
 */

public class Formatter extends AbstractJsonReader {

    final JsonGenerator generator;
    public Formatter(OutputStream out) throws IOException {
        generator = Util.getJsonFactory().createGenerator(out);
        generator.setPrettyPrinter(new DefaultPrettyPrinter());
    }

    @Override
    protected void handleToken(ParseEvent event) throws IOException {
        event.toGenerator(generator);
    }

    @Override
    protected void ready() throws IOException {
        generator.close();
    }

    public static String version() throws IOException {
        return Manifests.read("ProjectVersion");
    }

    public static void main(String[] argv) throws IOException, ParseException {
        CommandLineParser parser = new DefaultParser();
        Options options = new Options().addOption(new Option("help", "print this message"));
        options.addOption(new Option("version", false, "Output version"));
        CommandLine cl = parser.parse(options, argv, true);
        String[] args = cl.getArgs();
        if (cl.hasOption("version")) {
            System.out.println(version());
            System.exit(0);
        }
        if (cl.hasOption("help")) {
            System.out.println("jsonformat - " + version() + " - See https://github.com/mihxil/json");
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(
                    "jsonformat [OPTIONS]  [<INPUT FILE>|-] [<OUTPUT FILE>|-]",
                    options);

            System.exit(0);
        }


        InputStream in = Util.getInput(args, 0);

        OutputStream out = Util.getOutput(args, 1);
        Formatter formatter = new Formatter(out);

        formatter.read(Util.getJsonParser(in));
        in.close();
        out.close();
    }

}
