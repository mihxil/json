package org.meeuw.json.grep.matching;

import java.util.regex.Pattern;

import org.meeuw.json.PathEntry;

/**
 * @author Michiel Meeuwissen
 * @since 0.8
 */
public class RegexpKeyMatch extends AbstractKeyPattern {

    final Pattern pattern;

    public RegexpKeyMatch(Pattern pattern) {
        this.pattern = pattern;
    }

    @Override
    public boolean matches(PathEntry key) {
        return pattern.matcher(key.toString()).matches();
    }

    @Override
    public String toString() {
        return "Regexp(" + pattern + ")";
    }
}
