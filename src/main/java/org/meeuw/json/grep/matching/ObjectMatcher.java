package org.meeuw.json.grep.matching;

import com.fasterxml.jackson.core.JsonToken;
import org.meeuw.json.ParseEvent;

/**
 * @author Michiel Meeuwissen
 * @since ...
 */
abstract class  ObjectMatcher implements PathMatcher {

    @Override
    public boolean matches(ParseEvent event, String value) {
        return event.getToken() == JsonToken.END_OBJECT && matches(event);
    }

    protected abstract boolean matches(ParseEvent event);

}
