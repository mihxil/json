/*
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package org.meeuw.json;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.ObjectWriteContext;

import java.io.*;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;

/**
 * Pretty prints the json-stream. Depends entirely on jackson.
 * @author Michiel Meeuwissen
 */

public class Formatter extends AbstractJsonReader {

    final JsonGenerator generator;
    public Formatter(OutputStream out) {
        ObjectWriteContext writeContext = Util.prettyWriteContext();
        generator = Util.getJsonFactory().createGenerator(
            writeContext, out
        );


    }

    @Override
    protected void handleToken(ParseEvent event) throws IOException {
        event.toGenerator(generator);
    }

    @Override
    protected void ready() throws IOException {
        generator.close();
    }


    public static void main(String[] argv) throws IOException, ParseException {
        CommandLine cl = MainUtil
            .commandLine("jsonformat", "[<INPUT FILE>|-] [<OUTPUT FILE>|-]",  (options) -> {}, 0, argv);
        String[] args  = cl.getArgs();
        try (InputStream in = Util.getInput(args, 0);
             OutputStream out = Util.getOutput(args, 1);) {

            Formatter formatter = new Formatter(out);
            formatter.read(Util.getJsonParser(in));
        }
    }

}
