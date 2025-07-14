package com.nksoft.entrance_examination.common.config.props;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "custom.placement")
public class PlacementProperties {
    private LocalDateTime date;
    private int daysBeforePlacement;
}
