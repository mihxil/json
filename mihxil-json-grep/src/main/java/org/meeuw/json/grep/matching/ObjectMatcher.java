package org.meeuw.json.grep.matching;

import org.meeuw.json.ParseEvent;

import com.fasterxml.jackson.core.JsonToken;

/**
 * @author Michiel Meeuwissen
 * @since ...
 */
abstract class  ObjectMatcher implements PathMatcher {

    @Override
    public MatchResult matches(ParseEvent event, String value) {
        if (event.getToken() != JsonToken.END_OBJECT) {
            return MatchResult.NO;
        } else {
            return matches(event);
        }
    }

    protected abstract MatchResult matches(ParseEvent event);

}
