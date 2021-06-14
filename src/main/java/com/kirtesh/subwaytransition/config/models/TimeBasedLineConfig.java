package com.kirtesh.subwaytransition.config.models;

public class TimeBasedLineConfig {
    private String timeType;
    private String expression;
    private int exchangeWaitTime;
    private int travelTimeBetweenStation;

    public String getTimeType() {
        return timeType;
    }

    public void setTimeType(String timeType) {
        this.timeType = timeType;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public int getExchangeWaitTime() {
        return exchangeWaitTime;
    }

    public void setExchangeWaitTime(int exchangeWaitTime) {
        this.exchangeWaitTime = exchangeWaitTime;
    }

    public int getTravelTimeBetweenStation() {
        return travelTimeBetweenStation;
    }

    public void setTravelTimeBetweenStation(int travelTimeBetweenStation) {
        this.travelTimeBetweenStation = travelTimeBetweenStation;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("TimeBasedLineConfig{");
        sb.append("timeType='").append(timeType).append('\'');
        sb.append(", expression='").append(expression).append('\'');
        sb.append(", exchangeWaitTime=").append(exchangeWaitTime);
        sb.append(", travelTimeBetweenStation=").append(travelTimeBetweenStation);
        sb.append('}');
        return sb.toString();
    }
}
