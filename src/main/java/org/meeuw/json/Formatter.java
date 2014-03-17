/**
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package org.meeuw.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import org.apache.commons.cli.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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


    public static void main(String[] argv) throws IOException, ParseException {
        CommandLineParser parser = new BasicParser();
        CommandLine cl = parser.parse(new Options(), argv, true);
        String[] args = cl.getArgs();
        InputStream in = Util.getInput(args, 0);

        OutputStream out = Util.getOutput(args, 1);
        Formatter formatter = new Formatter(out);

        formatter.read(Util.getJsonParser(in));
        in.close();
        out.close();
    }

}
