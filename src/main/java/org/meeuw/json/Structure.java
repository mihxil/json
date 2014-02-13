package org.meeuw.json;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * This Json-reader just outputs the parsed keys to output. It is not very usefull in itself, but it show what the 'path' argument of {@link #handleToken} is at any moment, so this is more or less the simplest concretisation of AbstractJsonReader.
 */
public class Structure extends AbstractJsonReader {

    final PrintWriter out;

    public Structure(OutputStream out) {
        this.out = new PrintWriter(out);
    }

    @Override
    protected void handleToken(ParseEvent event) throws IOException {
        switch(event.getToken()) {
            case VALUE_EMBEDDED_OBJECT:
            case VALUE_STRING:
            case VALUE_NUMBER_INT:
            case VALUE_NUMBER_FLOAT:
            case VALUE_TRUE:
            case VALUE_FALSE:
            case VALUE_NULL:
                out.println(event.getPath().toString());
        }
    }

    @Override
    protected void ready() {
        out.close();

    }
}
