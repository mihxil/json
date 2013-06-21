package org.meeuw.json;

import com.fasterxml.jackson.core.JsonParser;

import java.io.IOException;
import java.io.Reader;


public abstract class AbstractJsonReader {


    public void read(final JsonParser jp) throws IOException {
        JsonIterator i = new JsonIterator(jp);
        while (i.hasNext()) {
            ParseEvent event = i.next();
            handleToken(event);
        }
        ready();

    }
    public void read(Reader reader) throws IOException {
        read(Util.getJsonParser(reader));
    }

    protected abstract void handleToken(ParseEvent event) throws  IOException;

    protected void ready() throws IOException {

    }



}
