package com.nksoft.entrance_examination.common.config.graalvm.hints;

import com.nksoft.entrance_examination.common.config.props.PlacementProperties;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;

public class PlacementHints  implements RuntimeHintsRegistrar {
    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        hints.reflection().registerType(PlacementProperties.class,
                MemberCategory.INVOKE_PUBLIC_METHODS);
    }
}
