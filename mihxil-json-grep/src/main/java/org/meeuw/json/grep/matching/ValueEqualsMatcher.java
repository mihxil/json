package org.meeuw.json.grep.matching;

import org.meeuw.json.ParseEvent;

/**
* @author Michiel Meeuwissen
*/
public class ValueEqualsMatcher extends ValueMatcher {
    private final String test;

    public ValueEqualsMatcher(String test) {
        this.test = test;
    }

    @Override
    public MatchResult matches(ParseEvent event) {
        return new MatchResult(event, test.equals(event.getValue()));
    }
    @Override
    public String toString() {
        return test;
    }
}
