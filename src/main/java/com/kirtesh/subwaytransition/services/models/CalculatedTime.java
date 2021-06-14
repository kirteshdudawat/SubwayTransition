package com.kirtesh.subwaytransition.services.models;

public class CalculatedTime {
    private int changeTimeInMinutes;
    private int travelTimeInMinutes;

    public CalculatedTime() {

    }

    public CalculatedTime(int changeTimeInMinutes, int travelTimeInMinutes) {
        this.changeTimeInMinutes = changeTimeInMinutes;
        this.travelTimeInMinutes = travelTimeInMinutes;
    }

    public int getChangeTimeInMinutes() {
        return changeTimeInMinutes;
    }

    public void setChangeTimeInMinutes(int changeTimeInMinutes) {
        this.changeTimeInMinutes = changeTimeInMinutes;
    }

    public int getTravelTimeInMinutes() {
        return travelTimeInMinutes;
    }

    public void setTravelTimeInMinutes(int travelTimeInMinutes) {
        this.travelTimeInMinutes = travelTimeInMinutes;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("CalculatedTime{");
        sb.append("changeTimeInMinutes=").append(changeTimeInMinutes);
        sb.append(", travelTimeInMinutes=").append(travelTimeInMinutes);
        sb.append('}');
        return sb.toString();
    }
}
