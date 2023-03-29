package org.sunshine.core.cache;

import com.google.common.collect.ImmutableMap;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * @author Teamo
 */
public interface RedisKey {

    /**
     * key模板(格式使用SpEL: #{#值})
     *
     * @return String
     */
    String getTemplate();

    /**
     * 过期时间
     *
     * @return Long
     */
    Long getExpire();

    /**
     * 线程安全构建key
     *
     * @param params 参数
     * @return 完全的key
     */
    default String buildKey(ImmutableMap<String, String> params) {
        ExpressionParser parser = new SpelExpressionParser();
        Expression expression = parser.parseExpression(getTemplate(), new TemplateParserContext());

        EvaluationContext context = new StandardEvaluationContext();

        params.forEach(context::setVariable);

        return expression.getValue(context, String.class);
    }

}
