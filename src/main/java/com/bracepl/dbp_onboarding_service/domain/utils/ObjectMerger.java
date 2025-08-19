package com.bracepl.dbp_onboarding_service.domain.utils;

import java.lang.reflect.Field;

public class ObjectMerger {
    public static <T> void mergeNonNullValues(T source, T target) {
        if (source == null || target == null) {
            throw new IllegalArgumentException("Source and target must not be null");
        }

        Class<?> clazz = source.getClass();
        while (clazz != null) {
            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                try {
                    Object value = field.get(source);
                    if (value != null) {
                        field.set(target, value);
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Failed to access field: " + field.getName(), e);
                }
            }
            clazz = clazz.getSuperclass();
        }
    }
}
