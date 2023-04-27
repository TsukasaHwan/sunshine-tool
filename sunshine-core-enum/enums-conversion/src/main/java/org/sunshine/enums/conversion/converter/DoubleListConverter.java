package org.sunshine.enums.conversion.converter;

import javax.persistence.Converter;
import java.util.List;

/**
 * @author: Teamo
 * @date: 2020/7/10 18:08
 * @description:
 */
@Converter(autoApply = true)
public class DoubleListConverter extends AbstractJsonConverter<List<Double>> {
}
