package org.meeuw.json.grep.matching;

import lombok.Getter;

import java.util.function.BooleanSupplier;
import java.util.function.Predicate;

import org.meeuw.json.ParseEvent;
import org.meeuw.json.Path;

/**
 * A Patch matcher defines matches on an entire json path and value.
 */
public interface PathMatcher {

    /**
     * @param event The event to match
     */
    MatchResult matches(ParseEvent event);

    Predicate<Path> needsKeyCollection();

    Predicate<Path> needsObjectCollection();

    class MatchResult implements BooleanSupplier {
        final boolean matches;

        @Getter
        final int weight;

        @Getter
        final ParseEvent event;


        public MatchResult(ParseEvent event, boolean matches) {
            this.matches = matches;
            this.weight = matches ? 1: 0;
            this.event = event;
        }
        public MatchResult(ParseEvent event, int weigth) {
            this.matches = true;
            this.weight = weigth;
            this.event = event;
        }

        @Override
        public boolean getAsBoolean() {
            return matches;
        }
        static final MatchResult NO = new MatchResult(null, false);

    }

}
