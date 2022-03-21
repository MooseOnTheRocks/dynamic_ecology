package dev.foltz.de;

import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

@FunctionalInterface
public interface IPredicate3<T1, T2, T3> {
    boolean test(T1 t1, T2 t2, T3 t3);

    default IPredicate3<T1, T2, T3> and(IPredicate3<? super T1, ? super T2, ? super T3> other) {
        Objects.requireNonNull(other);
        return (T1 t1, T2 t2, T3 t3) -> test(t1, t2, t3) && other.test(t1, t2, t3);
    }

    default IPredicate3<T1, T2, T3> negate() {
        return (t1, t2, t3) -> !test(t1, t2, t3);
    }

    default IPredicate3<T1, T2, T3> or(IPredicate3<? super T1, ? super T2, ? super T3> other) {
        Objects.requireNonNull(other);
        return (t1, t2, t3) -> test(t1, t2, t3) || other.test(t1, t2, t3);
    }
}
