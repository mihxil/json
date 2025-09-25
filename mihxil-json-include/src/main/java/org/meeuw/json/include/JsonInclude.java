package org.meeuw.json.include;


import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.Function;

import org.meeuw.json.JsonIterator;
import org.meeuw.json.ParseEvent;
import org.meeuw.json.Util;
import org.meeuw.json.grep.matching.PathMatcher;

import com.fasterxml.jackson.core.JsonParser;

/**
 * @author Michiel Meeuwissen
 * @since 0.9
 */
public class JsonInclude  {


    @lombok.Builder
    private JsonInclude(
        PathMatcher matcher,
        Function<ParseEvent, JsonParser> replacement
        ) {
    }


    public void start(InputStream inputStream, OutputStream outputStream) {
        JsonIterator iterator =   new JsonIterator(Util.getJsonParser(inputStream));
        while(iterator.hasNext()) {
            ParseEvent next = iterator.next();

        }



    }


}
