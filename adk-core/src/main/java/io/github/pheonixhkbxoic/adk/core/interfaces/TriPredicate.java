package io.github.pheonixhkbxoic.adk.core.interfaces;

/**
 * @author PheonixHkbxoic
 * @date 2025/5/5 16:20
 * @desc
 */
@FunctionalInterface
public interface TriPredicate<T, U, V> {

    boolean test(T t, U u, V v);

}
