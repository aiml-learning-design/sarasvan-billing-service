package com.sarasvan.billing.enums;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;

public interface IDEnum<T> {

    T get();

    static <T, E extends IDEnum<T>> E from(Class<E> enumClass, T value) {
        return Optional.ofNullable(enumClass.getEnumConstants())
                .flatMap(enumConstants -> Stream.of(enumConstants)
                .filter(e -> e.get().equals(value))
                .findFirst())
        .orElse(null);
    }
}
