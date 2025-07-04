package com.nksoft.entrance_examination.common.config.hints;

import com.vladmihalcea.hibernate.type.array.LongArrayType;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.aot.hint.TypeReference;

public class HibernateHints implements RuntimeHintsRegistrar {

    @Override
    public void registerHints(RuntimeHints hints, @jakarta.annotation.Nullable ClassLoader classLoader) {
        // mihalcea Long[] (LongArrayType) registration
        hints.reflection().registerType(
                TypeReference.of(LongArrayType.class), MemberCategory.values()
        );

        // CreationTimestamp registration
        hints.reflection().registerType(
                org.hibernate.generator.internal.CurrentTimestampGeneration.class,
                MemberCategory.values()
        );
    }
}
