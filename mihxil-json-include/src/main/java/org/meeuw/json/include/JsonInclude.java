package org.meeuw.json.include;


import java.util.function.Function;

import org.meeuw.json.JsonIterator;
import org.meeuw.json.ParseEvent;
import org.meeuw.json.grep.matching.PathMatcher;

import com.fasterxml.jackson.core.JsonParser;

/**
 * @author Michiel Meeuwissen
 * @since 0.9
 */
public class JsonInclude  {

    final JsonIterator wrapped;


    public JsonInclude(PathMatcher matcher, JsonIterator wrapped, Function<ParseEvent, JsonParser> replacement) {
        this.wrapped = wrapped;
    }
}
