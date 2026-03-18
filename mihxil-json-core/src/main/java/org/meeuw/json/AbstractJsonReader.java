package org.meeuw.json;

import lombok.SneakyThrows;
import tools.jackson.core.JsonParser;

import java.io.*;


public abstract class AbstractJsonReader implements Closeable {


    public void read(final JsonParser jp)  {
        JsonIterator i = new JsonIterator(jp);
        while (i.hasNext()) {
            ParseEvent event = i.next();
            handleToken(event);
        }
        ready();

    }
    public void read(Reader reader) {
        read(Util.getJsonParser(reader));
    }

    protected abstract void handleToken(ParseEvent event);

    protected void ready() {

    }

    @SneakyThrows
    @Override
    public void close() {
        ready();
    }



}
