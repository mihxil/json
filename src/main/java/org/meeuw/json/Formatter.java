/**
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package org.meeuw.json;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import org.apache.commons.cli.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Deque;

/**
 * Pretty prints the json-stream. Depends entirely on jackson.
 * @author Michiel Meeuwissen
 */

public class Formatter extends AbstractJsonReader {

    final JsonGenerator generator;
    public Formatter(OutputStream out) throws IOException {
        JsonFactory jsonFactory = getJsonFactory();
        generator = jsonFactory.createJsonGenerator(out);
        generator.setPrettyPrinter(new DefaultPrettyPrinter());
    }

    @Override
    protected void handleToken(JsonParser jp, JsonToken token, Deque<PathEntry> path) throws IOException {
        switch(token) {
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
                generator.writeFieldName(jp.getText());
                break;
            case VALUE_EMBEDDED_OBJECT:
                // don't know
                generator.writeObject(jp.getText());
                break;
            case VALUE_STRING:
                generator.writeString(jp.getText());
                break;
            case VALUE_NUMBER_INT:
                generator.writeNumber(jp.getText());
                break;
            case VALUE_NUMBER_FLOAT:
                generator.writeNumber(jp.getValueAsDouble());
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
        OutputStream out = getOutput(args, 1);
        Formatter formatter = new Formatter(out);
        InputStream in = getInput(args, 0);

        formatter.read(in);
        in.close();
        out.close();
    }

}
