package org.meeuw.json.grep;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Michiel Meeuwissen
 * @since 0.7
 */
public class GrepMainRecord {
    final List<Field> fields = new ArrayList<>();
    final String sep;

    GrepMainRecord(String sep) {
        this.sep = sep;
    }

    @Override
    public String toString() {
        return fields.stream().map(f -> f.value).collect(Collectors.joining(sep));
    }


    public static class Field implements Comparable<Field> {
        final int weight;
        final String value;

        Field(int weight, String value) {
            this.weight = weight;
            this.value = value;
        }

        @Override
        public int compareTo(Field o) {
            return weight - o.weight;
        }
        @Override
        public String toString() {
            return weight + ":" + value;
        }
    }
}
