package com.nksoft.entrance_examination.common.config.props;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "custom.student.choices")
public class StudentChoiceProperties {
    private int min;
    private int max;
}

