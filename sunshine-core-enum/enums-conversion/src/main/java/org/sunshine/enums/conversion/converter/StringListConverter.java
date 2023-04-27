package org.sunshine.enums.conversion.converter;

import javax.persistence.Converter;
import java.util.List;

/**
 * @author: Teamo
 * @date: 2020/7/10 18:06
 * @description:
 */
@Converter(autoApply = true)
public class StringListConverter extends AbstractJsonConverter<List<String>> {
}
