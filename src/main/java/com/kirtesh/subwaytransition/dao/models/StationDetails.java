package com.kirtesh.subwaytransition.dao.models;

import com.opencsv.bean.CsvBindByName;

public class StationDetails {

    @CsvBindByName(column = "Station Code")
    private String stationCode;

    @CsvBindByName(column = "Station Name")
    private String stationName;

    @CsvBindByName(column = "Opening Date")
    private String openingDate;

    public StationDetails() {

    }

    public StationDetails(String stationCode, String stationName, String openingDate) {
        this.stationCode = stationCode;
        this.stationName = stationName;
        this.openingDate = openingDate;
    }

    public String getStationCode() {
        return stationCode;
    }

    public void setStationCode(String stationCode) {
        this.stationCode = stationCode;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public String getOpeningDate() {
        return openingDate;
    }

    public void setOpeningDate(String openingDate) {
        this.openingDate = openingDate;
    }

    @Override
    public String toString() {
        StringBuffer str = new StringBuffer(String.format("StationDetails { stationCode='%s'", stationCode));
        str.append(String.format(", stationName='%s'", stationName));
        str.append(String.format(", openingDate='%s' }", openingDate));

        return str.toString();
    }
}
