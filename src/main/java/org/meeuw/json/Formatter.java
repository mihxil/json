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

import java.io.*;

/**
 * @author Michiel Meeuwissen
 * @since 1.0
 */

public class Formatter {

	void format(InputStream in, OutputStream out) throws IOException {
		JsonFactory jsonFactory = getJsonFactory();

		JsonParser jp = jsonFactory.createJsonParser(in);
		JsonGenerator generator = jsonFactory.createJsonGenerator(out);
		format(jp, generator);
	}

	void format(Reader in, OutputStream out) throws IOException {
		JsonFactory jsonFactory = getJsonFactory();
		JsonParser jp = jsonFactory.createJsonParser(in);
		JsonGenerator generator = jsonFactory.createJsonGenerator(out);
		format(jp, generator);
	}

    private void format(JsonParser jp, JsonGenerator generator) throws IOException {
		jp.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		jp.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
		jp.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
		jp.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
		generator.setPrettyPrinter(new DefaultPrettyPrinter ());

		int depth = 0;
        while(true) {
            JsonToken token = jp.nextToken();
            if(token == null) {
                break;
            }
            if(token == JsonToken.START_OBJECT) {
                depth++;
            }
            if(token == JsonToken.END_OBJECT) {
                depth--;
            }
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
					generator.writeObject(token.asByteArray());
					break;
				case VALUE_STRING:
					generator.writeString(jp.getText());
					break;
				case VALUE_NUMBER_INT:
				case VALUE_NUMBER_FLOAT:
					generator.writeNumber(jp.getText());
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
		generator.close();
    }

	public JsonFactory getJsonFactory() {
		return new JsonFactory();
	}

	private static File getFile(String string) {
		if ("-".equals(string)) return null;
		return new File(string);
	}

	public static void main(String[] argv) throws IOException {
		InputStream in = System.in;
		OutputStream out = System.out;
		if (argv.length > 0) {
			File file = getFile(argv[0]);
			in = file == null ? System.in : new FileInputStream(file);
		}
		if (argv.length > 1) {
			File file = getFile(argv[1]);
			out = file == null ? System.out : new FileOutputStream(file);
		}

		Formatter formatter = new Formatter();
		formatter.format(in, out);
	}

}
