package com.kirtesh.subwaytransition.cache.models;

import java.io.Serializable;

public class LineConfigs {
    private String expression;
    private Serializable compiledExpression;
    private int exchangeWaitTime;
    private int travelTimeBetweenStation;

    public String getExpression() {
        return expression;
    }

    /**
     *
     * @param expression - raw expression
     */
    public void setExpression(String expression) {
        this.expression = expression;
    }

    public Serializable getCompiledExpression() {
        return compiledExpression;
    }

    /**
     *
     * @param compiledExpression - compiled expression
     */
    public void setCompiledExpression(Serializable compiledExpression) {
        this.compiledExpression = compiledExpression;
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
        final StringBuffer sb = new StringBuffer("LineConfigs{");
        sb.append("expression='").append(expression).append('\'');
        sb.append(", compiledExpression=").append(compiledExpression);
        sb.append(", exchangeWaitTime=").append(exchangeWaitTime);
        sb.append(", travelTimeBetweenStation=").append(travelTimeBetweenStation);
        sb.append('}');
        return sb.toString();
    }
}
