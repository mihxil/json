package org.meeuw.json.grep.matching;

import org.meeuw.json.ParseEvent;

/**
* @author Michiel Meeuwissen
*/
public class ScalarEqualsMatcher extends ScalarMatcher {

    private final String test;

    private final String replacement;

    public ScalarEqualsMatcher(String test, String replacement) {
        this.test = test;
        this.replacement = replacement;
    }

    @Override
    protected MatchResult matchesScalar(ParseEvent event) {
        return new MatchResult(replacement == null ? event : event.withValue(replacement), test.equals(event.getValue()));
    }

    @Override
    public String toString() {
        return test;
    }
}
