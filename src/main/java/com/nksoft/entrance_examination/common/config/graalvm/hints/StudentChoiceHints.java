package com.nksoft.entrance_examination.common.config.graalvm.hints;

import com.nksoft.entrance_examination.common.config.props.StudentChoiceProperties;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;

public class StudentChoiceHints implements RuntimeHintsRegistrar {
    // Public methods only
    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        hints.reflection().registerType(StudentChoiceProperties.class,
                MemberCategory.INVOKE_PUBLIC_METHODS);
    }
}
