package com.minorityhobbies.util.ee;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import java.lang.reflect.Field;

public class SystemPropertyConfigProvider {
    @Produces
    public String getConfig(InjectionPoint injectionPoint) {
        String name = injectionPoint.getMember().getName();
        try {
            Class<?> type = injectionPoint.getMember().getDeclaringClass();
            Field field = type.getDeclaredField(name);
            SystemProperty prop = field.getDeclaredAnnotation(SystemProperty.class);
            return System.getProperty(prop.value());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
