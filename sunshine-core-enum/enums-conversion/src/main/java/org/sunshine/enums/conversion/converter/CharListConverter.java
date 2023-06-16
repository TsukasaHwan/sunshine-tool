package org.sunshine.enums.conversion.converter;

import jakarta.persistence.Converter;

import java.util.List;

/**
 * @author: Teamo
 * @date: 2020/7/10 18:12
 * @description:
 */
@Converter(autoApply = true)
public class CharListConverter extends AbstractJsonConverter<List<Character>> {
}
