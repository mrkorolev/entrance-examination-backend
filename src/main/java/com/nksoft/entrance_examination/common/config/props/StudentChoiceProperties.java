package com.nksoft.entrance_examination.common.config.props;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Getter
@Component
@ConfigurationProperties(prefix = "custom.student.choices")
public class StudentChoiceProperties {
    private int min;
    private int max;
    private LocalDateTime choiceSubmissionDeadline;

    public void setMin(int min) {
        if (min < 3) {
            throw new IllegalArgumentException("Min department choices >= 3");
        }
        this.min = min;
    }

    public void setMax(int max) {
        if (max > 24) {
            throw new IllegalArgumentException("Max department choices <= 24");
        }
        this.max = max;
    }

    public void setChoiceSubmissionDeadline(LocalDateTime choiceSubmissionDeadline) {
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(choiceSubmissionDeadline)) {
            throw new IllegalArgumentException("choice submission deadline is supposed to be in the future");
        }
        this.choiceSubmissionDeadline = choiceSubmissionDeadline;
    }
}

