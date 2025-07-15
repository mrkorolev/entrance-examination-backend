package com.nksoft.entrance_examination.common.config.props;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Getter
@Component
@ConfigurationProperties(prefix = "custom.placement")
public class PlacementProperties {
    private LocalDateTime choiceSubmissionDeadline;

    public void setChoiceSubmissionDeadline(LocalDateTime choiceSubmissionDeadline) {
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(choiceSubmissionDeadline)) {
            throw new IllegalArgumentException("choice submission deadline is supposed to be in the future");
        }
        this.choiceSubmissionDeadline = choiceSubmissionDeadline;
    }
}
