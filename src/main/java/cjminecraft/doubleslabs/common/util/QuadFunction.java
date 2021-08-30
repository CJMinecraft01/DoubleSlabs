package cjminecraft.doubleslabs.common.util;

@FunctionalInterface
public interface QuadFunction<T1, T2, T3, T4, R> {
    R apply(T1 a, T2 b, T3 c, T4 d);
}
