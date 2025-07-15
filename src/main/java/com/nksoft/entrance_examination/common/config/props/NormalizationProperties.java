package com.nksoft.entrance_examination.common.config.props;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Component
@ConfigurationProperties(prefix = "custom.normalization")
public class NormalizationProperties {
    private float rescaledMean;
    private float rescaledSd;

    public void setRescaledMean(float rescaledMean) {
        if (rescaledMean < 0) {
            throw new IllegalArgumentException("rescaledMean must be positive");
        }
        this.rescaledMean = rescaledMean;
    }

    public void setRescaledSd(float rescaledSd) {
        if (rescaledSd < 0) {
            throw new IllegalArgumentException("rescaledSd must be positive");
        }
        this.rescaledSd = rescaledSd;
    }
}
