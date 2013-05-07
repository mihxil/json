package org.meeuw.json;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Deque;

/**

 */
public class Structure extends AbstractJsonReader {

    final PrintWriter out;

    public Structure(OutputStream out) {
        this.out = new PrintWriter(out);
    }

    @Override
    protected void handleToken(JsonParser jp, JsonToken token, Deque<PathEntry> path) throws IOException {
        switch(token) {
            case VALUE_EMBEDDED_OBJECT:
            case VALUE_STRING:
            case VALUE_NUMBER_INT:
            case VALUE_NUMBER_FLOAT:
            case VALUE_TRUE:
            case VALUE_FALSE:
            case VALUE_NULL:
                out.println(join(path));
        }
    }

    @Override
    protected void ready() {
        out.close();

    }
}
