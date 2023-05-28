package org.sunshine.core.common.exception;

import org.sunshine.core.tool.api.response.Result;
import org.sunshine.core.tool.exception.CustomException;

/**
 * @author Teamo
 * @since 2019/7/10
 */
public class ExceptionCast {
    public static void cast(String message, Result<?> result) {
        throw new CustomException(message, result);
    }

    public static void cast(String message) {
        throw new CustomException(message);
    }
}
