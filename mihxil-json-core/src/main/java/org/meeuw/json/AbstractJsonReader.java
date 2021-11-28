package org.meeuw.json;

import lombok.SneakyThrows;

import java.io.*;

import com.fasterxml.jackson.core.JsonParser;


public abstract class AbstractJsonReader implements Closeable {


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

    @SneakyThrows
    @Override
    public void close() {
        ready();
    }



}
