package cjminecraft.doubleslabs.common.util;

@FunctionalInterface
public interface TriFunction<T1, T2, T3, R> {
    R apply(T1 a, T2 b, T3 c);
}
