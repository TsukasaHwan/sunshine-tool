package org.sunshine.core.tool.exception;

import org.sunshine.core.tool.api.response.Result;

/**
 * 自定义异常
 *
 * @author Teamo
 * @since 2019/7/10
 */
public class CustomException extends RuntimeException {
    private final Result<?> result;

    public CustomException(String message, Result<?> result) {
        super(message);
        this.result = result;
    }

    public CustomException(String message) {
        super(message);
        this.result = Result.fail(message);
    }

    public Result<?> getResult() {
        return this.result;
    }
}
