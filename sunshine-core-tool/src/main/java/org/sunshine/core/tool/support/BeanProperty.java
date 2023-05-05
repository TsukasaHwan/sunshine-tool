package org.sunshine.core.tool.support;

/**
 * Bean属性
 *
 * @author Chill
 */
public class BeanProperty {
    private final String name;

    private final Class<?> type;

    public BeanProperty(String name, Class<?> type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public Class<?> getType() {
        return type;
    }
}
