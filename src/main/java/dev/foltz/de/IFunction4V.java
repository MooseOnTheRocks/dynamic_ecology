package dev.foltz.de;

@FunctionalInterface
public interface IFunction4V<T1, T2, T3, T4> {
    void apply(T1 t1, T2 t2, T3 t3, T4 t4);
}