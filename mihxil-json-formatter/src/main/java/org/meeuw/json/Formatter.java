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
        switch(event.getToken()) {
            case START_OBJECT:
                generator.writeStartObject();
                break;
            case END_OBJECT:
                generator.writeEndObject();
                break;
            case START_ARRAY:
                generator.writeStartArray();
                break;
            case END_ARRAY:
                generator.writeEndArray();
                break;
            case FIELD_NAME:
                generator.writeFieldName(event.getValue());
                break;
            case VALUE_EMBEDDED_OBJECT:
                // don't know
                generator.writeObject(event.getValue());
                break;
            case VALUE_STRING:
                generator.writeString(event.getValue());
                break;
            case VALUE_NUMBER_INT:
                generator.writeNumber(event.getValue());
                break;
            case VALUE_NUMBER_FLOAT:
                generator.writeNumber(event.getValue()); //.getValueAsDouble());
                break;
            case VALUE_TRUE:
                generator.writeBoolean(true);
                break;
            case VALUE_FALSE:
                generator.writeBoolean(false);
                break;
            case VALUE_NULL:
                generator.writeNull();
                break;

        }
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
