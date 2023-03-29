package org.sunshine.core.tool.util;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * 当 Lambda 遇上受检异常
 * <a href="https://segmentfault.com/a/1190000007832130">Reference</a>
 *
 * @author Teamo
 */
public class Try {

    public static <T, R> Function<T, R> apply(UncheckedFunction<T, R> mapper) {
        Objects.requireNonNull(mapper);

        return t -> {
            try {
                return mapper.apply(t);
            } catch (Exception e) {
                throw Exceptions.unchecked(e);
            }
        };
    }

    public static <T, R> Function<T, R> apply(UncheckedFunction<T, R> mapper, Function<Throwable, R> handler) {
        Objects.requireNonNull(mapper);
        Objects.requireNonNull(handler);

        return t -> {
            try {
                return mapper.apply(t);
            } catch (Exception e) {
                return handler.apply(e);
            }
        };
    }

    public static <T> Predicate<T> test(UncheckedPredicate<T> mapper) {
        Objects.requireNonNull(mapper);

        return t -> {
            try {
                return mapper.test(t);
            } catch (Exception e) {
                throw Exceptions.unchecked(e);
            }
        };
    }

    public static <T> Predicate<T> test(UncheckedPredicate<T> mapper, Consumer<Throwable> handler) {
        Objects.requireNonNull(mapper);
        Objects.requireNonNull(handler);

        return t -> {
            try {
                return mapper.test(t);
            } catch (Exception e) {
                handler.accept(e);
            }
            return false;
        };
    }

    @FunctionalInterface
    public interface UncheckedFunction<T, R> {
        /**
         * 调用
         *
         * @param t t
         * @return R
         * @throws Exception Exception
         */
        R apply(T t) throws Exception;
    }

    @FunctionalInterface
    public interface UncheckedPredicate<T> {
        /**
         * 调用
         *
         * @param t t
         * @return boolean
         * @throws Exception Exception
         */
        boolean test(T t) throws Exception;
    }
}
