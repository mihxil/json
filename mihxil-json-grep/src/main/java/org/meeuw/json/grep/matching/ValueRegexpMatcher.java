package org.meeuw.json.grep.matching;

import lombok.Getter;
import lombok.With;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.meeuw.json.ParseEvent;

/**
 * Matches the value with a regular expression.
 */
public class ValueRegexpMatcher extends ValueMatcher {
    @Getter
    private final Pattern pattern;

    @Getter
    @With
    private final String replacement;

    public ValueRegexpMatcher(Pattern pattern, String replacement) {
        this.pattern = pattern;
        this.replacement = replacement;
    }

    @Override
    public MatchResult matches(ParseEvent event) {
        Matcher matcher = pattern.matcher(event.getValue());
        if (matcher.matches()) {
            if (replacement != null) {
                String v = matcher.replaceAll(replacement);
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
