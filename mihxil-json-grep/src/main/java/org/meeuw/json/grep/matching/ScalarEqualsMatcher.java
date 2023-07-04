package org.meeuw.json.grep.matching;

import java.util.function.UnaryOperator;

import org.meeuw.json.ParseEvent;

/**
* @author Michiel Meeuwissen
*/
public class ScalarEqualsMatcher extends ScalarMatcher {

    private final String test;

    private final UnaryOperator<String> replacement;

    public ScalarEqualsMatcher(String test, String replacement) {
        this(test, (r) -> replacement);
    }

    public ScalarEqualsMatcher(String test, UnaryOperator<String> replacement) {
        this.test = test;
        this.replacement = replacement;
    }

    @Override
    protected MatchResult matchesScalar(ParseEvent event) {
        return new MatchResult(replacement == null ? event :
            event.withValue(replacement.apply(event.getValue())), test.equals(event.getValue()));
    }

    @Override
    public String toString() {
        return test;
    }
}
