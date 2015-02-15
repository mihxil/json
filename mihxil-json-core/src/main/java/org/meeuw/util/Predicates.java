package org.meeuw.util;

/**
 * @author Michiel Meeuwissen
 * @since 0.6
 */
public class Predicates {

    private Predicates() { }

	public static <S> Predicate<S> alwaysFalse() {
		return new Predicate<S>() {
			@Override
			public boolean test(S t) {
				return false;
			}
		};
	}

	public static <S> Predicate<S> alwaysTrue() {
		return new Predicate<S>() {
			@Override
			public boolean test(S t) {
				return true;
			}
		};
	}

	public static <S> Predicate<S> and(final Predicate<S>... predicates) {
		return new Predicate<S>() {
			@Override
			public boolean test(S t) {
				for (Predicate<S> p : predicates) {
					if (! p.test(t)) return false;
				}
				return true;
			}
		};
	}

	public static <S> Predicate<S> or(final Predicate<S>... predicates) {
		return new Predicate<S>() {
			@Override
			public boolean test(S t) {
				for (Predicate<S> p : predicates) {
					if (p.test(t)) return true;
				}
				return false;
			}
		};
	}

}
