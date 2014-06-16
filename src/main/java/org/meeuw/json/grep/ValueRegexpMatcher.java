package org.meeuw.json.grep;

import java.util.regex.Pattern;

/**
 * Matches the value with a regular expression.
 */
class ValueRegexpMatcher extends ValueMatcher {
    private final Pattern pattern;

    public ValueRegexpMatcher(Pattern pattern) {
        this.pattern = pattern;
    }

    @Override
    protected boolean matches(String value) {
        return pattern.matcher(value).matches();
    }

    @Override
    public String toString() {
        return String.valueOf(pattern);
    }
}
