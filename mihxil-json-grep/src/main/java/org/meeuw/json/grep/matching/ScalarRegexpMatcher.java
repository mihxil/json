package org.meeuw.json.grep.matching;

import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.meeuw.json.ParseEvent;

/**
 * Matches the value with a regular expression.
 */
public class ScalarRegexpMatcher extends ScalarMatcher {

    private final Pattern pattern;
    private final UnaryOperator<String> replacement;

    public ScalarRegexpMatcher(Pattern pattern, UnaryOperator<String> replacement) {
        this.pattern = pattern;
        this.replacement = replacement;
    }
    public ScalarRegexpMatcher(Pattern pattern, String replacement) {
        this(pattern, (t) -> replacement);
    }

    @Override
    protected MatchResult matchesScalar(ParseEvent event) {
        Matcher matcher = pattern.matcher(event.getValue());
        if (matcher.matches()) {
            if (replacement != null) {
                String replaced = replacement.apply(event.getValue());
                String v = matcher.replaceAll(replaced);
                return new MatchResult(event.withValue(v), true);
            } else {
                return new MatchResult(event, true);
            }
        } else {
            return MatchResult.NO;
        }
    }

    @Override
    public String toString() {
        return "value~" + pattern;
    }
}
