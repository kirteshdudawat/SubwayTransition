package com.kirtesh.subwaytransition.dto;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class StationDetailsDto implements Comparable<StationDetailsDto>{

    private String stationCode;

    private String lineCode;

    private int stationNumber;

    private String stationName;

    private LocalDate openingDate;

    public String getStationCode() {
        return stationCode;
    }

    public void setStationCode(String stationCode) {
        this.stationCode = stationCode;
    }

    public String getLineCode() {
        return lineCode;
    }

    public void setLineCode(String lineCode) {
        this.lineCode = lineCode;
    }

    public int getStationNumber() {
        return stationNumber;
    }

    public void setStationNumber(int stationNumber) {
        this.stationNumber = stationNumber;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public LocalDate getOpeningDate() {
        return openingDate;
    }

    public void setOpeningDate(LocalDate openingDate) {
        this.openingDate = openingDate;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");

        StringBuffer str = new StringBuffer(String.format("StationDetailsDto { stationCode='%s'", stationCode));
        str.append(String.format(", lineCode='%s'", lineCode));
        str.append(String.format(", stationNumber=%d", stationNumber));
        str.append(String.format(", stationName='%s'", stationName));
        str.append(String.format(", openingDate='%s' }", formatter.format(openingDate)));

        return str.toString();
    }

    /**
     * To be used in Collection.sort method
     */
    @Override
    public int compareTo(StationDetailsDto obj) {
        return Integer.compare(this.stationNumber, obj.getStationNumber());
    }

    public boolean isOpen(LocalDate travelDate) {
        return travelDate.isEqual(this.openingDate) || travelDate.isAfter(this.openingDate);
    }
}