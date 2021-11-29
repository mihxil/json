package org.meeuw.json.grep.matching;

import org.meeuw.json.ParseEvent;

/**
* @author Michiel Meeuwissen
*/
public class ValueEqualsMatcher extends ValueMatcher {
    private final String test;

    private final String replacement;

    public ValueEqualsMatcher(String test, String replacement) {
        this.test = test;
        this.replacement = replacement;
    }

    @Override
    public MatchResult matches(ParseEvent event) {
        return new MatchResult(replacement == null ? event : event.withValue(replacement), test.equals(event.getValue()));
    }
    @Override
    public String toString() {
        return test;
    }
}
