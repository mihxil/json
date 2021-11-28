package org.meeuw.json.grep.matching;

import org.meeuw.json.ParseEvent;

import com.fasterxml.jackson.core.JsonToken;

/**
 * @author Michiel Meeuwissen
 * @since ...
 */
abstract class  ObjectMatcher implements PathMatcher {

    @Override
    public final MatchResult matches(ParseEvent event) {
        if (event.getToken() != JsonToken.END_OBJECT) {
            return MatchResult.NO;
        } else {
            return matchesObject(event);
        }
    }

    protected abstract MatchResult matchesObject(ParseEvent event);

}
