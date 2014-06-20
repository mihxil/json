package org.meeuw.util;

/**
 * As java 8's Predicate, we would like to stay java 6 compatible for now.
 * @author Michiel Meeuwissen
 * @since 0.6
 */
public interface Predicate<T> {

	boolean test(T t);

}
