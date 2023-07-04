package org.meeuw.json.grep.matching;

import java.util.function.UnaryOperator;

import org.meeuw.json.ParseEvent;

/**
* @author Michiel Meeuwissen
*/
public class ReplaceScalarMatcher extends ScalarMatcher {

    private final UnaryOperator<String> replacement;

    public ReplaceScalarMatcher(UnaryOperator<String> replacement) {
        this.replacement = replacement;
    }

    public ReplaceScalarMatcher(String replacement) {
        this.replacement = t -> replacement;
    }

    @Override
    protected MatchResult matchesScalar(ParseEvent event) {
        return new MatchResult(event.withValue(replacement.apply(event.getValue())), true);
    }

    @Override
    public String toString() {
        return "replace:" + replacement.apply("<value>");
    }
}
