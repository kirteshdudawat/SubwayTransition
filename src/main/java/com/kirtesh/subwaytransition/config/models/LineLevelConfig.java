package com.kirtesh.subwaytransition.config.models;

import java.util.List;

public class LineLevelConfig {
    private String lineCode;
    private List<TimeBasedLineConfig> timeBasedLineConfigs;

    public String getLineCode() {
        return lineCode;
    }

    public void setLineCode(String lineCode) {
        this.lineCode = lineCode;
    }

    public List<TimeBasedLineConfig> getTimeBasedLineConfigs() {
        return timeBasedLineConfigs;
    }

    public void setTimeBasedLineConfigs(List<TimeBasedLineConfig> timeBasedLineConfigs) {
        this.timeBasedLineConfigs = timeBasedLineConfigs;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("LineLevelConfig{");
        sb.append("lineCode='").append(lineCode).append('\'');
        sb.append(", timeBasedLineConfigs=").append(timeBasedLineConfigs);
        sb.append('}');
        return sb.toString();
    }
}
