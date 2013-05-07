/**
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package org.meeuw.json;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.util.DefaultPrettyPrinter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Deque;

/**
 * @author Michiel Meeuwissen
 * @since 1.0
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


    public static void main(String[] argv) throws IOException {
        OutputStream out = getOutput(argv);
        Formatter formatter = new Formatter(out);
        InputStream in = getInput(argv);

        formatter.read(in);
        in.close();
        out.close();
    }

}
