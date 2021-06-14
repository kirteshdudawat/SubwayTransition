package com.kirtesh.subwaytransition.sro;

import java.time.LocalDateTime;

public class SearchAvailablePathRequestSro {
    private LocalDateTime dateTime;
    private String sourceStationCode;
    private String destinationStationCode;

    public SearchAvailablePathRequestSro(String sourceStationCode, String destinationStationCode, LocalDateTime dateTime) {
        this.sourceStationCode = sourceStationCode;
        this.destinationStationCode = destinationStationCode;
        if (dateTime != null) {
            this.dateTime = dateTime;
        } else {
            this.dateTime = LocalDateTime.now().plusMinutes(1);
        }
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public String getSourceStationCode() {
        return sourceStationCode;
    }

    public void setSourceStationCode(String sourceStationCode) {
        this.sourceStationCode = sourceStationCode;
    }

    public String getDestinationStationCode() {
        return destinationStationCode;
    }

    public void setDestinationStationCode(String destinationStationCode) {
        this.destinationStationCode = destinationStationCode;
    }

    @Override
    public String toString() {
        StringBuffer str = new StringBuffer(String.format("SearchAvailablePathRequestSro { sourceStationCode='%s'", sourceStationCode));
        str.append(String.format(", destinationStationCode='%s'", destinationStationCode));
        str.append(String.format(", dateTime='%s' }", dateTime));

        return str.toString();
    }
}
