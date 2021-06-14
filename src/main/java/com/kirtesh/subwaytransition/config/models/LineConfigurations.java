package com.kirtesh.subwaytransition.config.models;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * This would be populated from configurations.
 */
@ConfigurationProperties(prefix = "lineconfig")
public class LineConfigurations {
    private List<LineLevelConfig> lineLevelConfigs;

    public List<LineLevelConfig> getLineLevelConfigs() {
        return lineLevelConfigs;
    }

    public void setLineLevelConfigs(List<LineLevelConfig> lineLevelConfigs) {
        this.lineLevelConfigs = lineLevelConfigs;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("LineConfigurations{");
        sb.append("lineLevelConfigs=").append(lineLevelConfigs);
        sb.append('}');
        return sb.toString();
    }
}
