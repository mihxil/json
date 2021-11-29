package org.meeuw.json.grep.matching;

import org.meeuw.json.ParseEvent;

/**
* @author Michiel Meeuwissen
*/
public class ReplaceScalarMatcher extends ScalarMatcher {

    private final String replacement;

    public ReplaceScalarMatcher(String replacement) {
        this.replacement = replacement;
    }

    @Override
    protected MatchResult matchesScalar(ParseEvent event) {
        return new MatchResult(event.withValue(replacement), true);
    }

    @Override
    public String toString() {
        return "replace:" + replacement;
    }
}
