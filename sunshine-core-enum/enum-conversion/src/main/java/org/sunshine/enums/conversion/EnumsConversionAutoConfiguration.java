package org.sunshine.enums.conversion;

import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.persistence.AttributeConverter;

/**
 * @author: Teamo
 * @date: 2020/7/10 17:17
 * @description:
 */
@Configuration
@ConditionalOnClass({AttributeConverter.class})
@Import({ConverterPackageScan.class})
public class EnumsConversionAutoConfiguration {


}
