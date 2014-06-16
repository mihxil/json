package org.meeuw.json.grep;

/**
* @author Michiel Meeuwissen
* @since ...
*/
class ValueEqualsMatcher extends ValueMatcher {
    private final String test;

    public ValueEqualsMatcher(String test) {
        this.test = test;
    }

    @Override
    protected boolean matches(String value) {
        return test.equals(value);
    }
    @Override
    public String toString() {
        return test;
    }
}
