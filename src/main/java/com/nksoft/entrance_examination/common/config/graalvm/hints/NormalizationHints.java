package com.nksoft.entrance_examination.common.config.graalvm.hints;

import com.nksoft.entrance_examination.common.config.props.NormalizationProperties;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;

public class NormalizationHints  implements RuntimeHintsRegistrar {
    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        hints.reflection().registerType(NormalizationProperties.class,
                MemberCategory.INVOKE_PUBLIC_METHODS);
    }
}