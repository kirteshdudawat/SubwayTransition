package com.kirtesh.subwaytransition.services.models;

import java.time.LocalDateTime;
import java.util.List;

public class CalculatedPath {
    private List<String> stationCodes;
    private List<String> steps;
    private int totalJourneyTimeInMinutes;
    private int totalStationsInJourney;
    private LocalDateTime journeyStartDateTime;
    private LocalDateTime journeyEndDateTime;

    public CalculatedPath(List<String> stationCodes, List<String> steps, int totalJourneyTimeInMinutes, int totalStationsInJourney, LocalDateTime journeyStartDateTime, LocalDateTime journeyEndDateTime) {
        this.stationCodes = stationCodes;
        this.steps = steps;
        this.totalJourneyTimeInMinutes = totalJourneyTimeInMinutes;
        this.totalStationsInJourney = totalStationsInJourney;
        this.journeyStartDateTime = journeyStartDateTime;
        this.journeyEndDateTime = journeyEndDateTime;
    }

    public List<String> getStationCodes() {
        return stationCodes;
    }

    public void setStationCodes(List<String> stationCodes) {
        this.stationCodes = stationCodes;
    }

    public List<String> getSteps() {
        return steps;
    }

    public void setSteps(List<String> steps) {
        this.steps = steps;
    }

    public int getTotalJourneyTimeInMinutes() {
        return totalJourneyTimeInMinutes;
    }

    public void setTotalJourneyTimeInMinutes(int totalJourneyTimeInMinutes) {
        this.totalJourneyTimeInMinutes = totalJourneyTimeInMinutes;
    }

    public int getTotalStationsInJourney() {
        return totalStationsInJourney;
    }

    public void setTotalStationsInJourney(int totalStationsInJourney) {
        this.totalStationsInJourney = totalStationsInJourney;
    }

    public LocalDateTime getJourneyStartDateTime() {
        return journeyStartDateTime;
    }

    public void setJourneyStartDateTime(LocalDateTime journeyStartDateTime) {
        this.journeyStartDateTime = journeyStartDateTime;
    }

    public LocalDateTime getJourneyEndDateTime() {
        return journeyEndDateTime;
    }

    public void setJourneyEndDateTime(LocalDateTime journeyEndDateTime) {
        this.journeyEndDateTime = journeyEndDateTime;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("CalculatedPath{");
        sb.append("stationCodes=").append(stationCodes);
        sb.append(", steps=").append(steps);
        sb.append(", totalJourneyTimeInMinutes=").append(totalJourneyTimeInMinutes);
        sb.append(", totalStationsInJourney=").append(totalStationsInJourney);
        sb.append(", journeyStartDateTime=").append(journeyStartDateTime);
        sb.append(", journeyEndDateTime=").append(journeyEndDateTime);
        sb.append('}');
        return sb.toString();
    }
}
