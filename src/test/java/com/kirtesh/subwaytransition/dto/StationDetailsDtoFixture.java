package com.kirtesh.subwaytransition.dto;

import java.time.LocalDate;

public class StationDetailsDtoFixture {
    private static LocalDate defaultOpenDate = LocalDate.of(2000, 01, 01);

    public static StationDetailsDto getCustomizedStationDetailsDto(int stationNumber, String stationCode, String lineCode, String stationName, LocalDate openingDate) {
        StationDetailsDto dto = new StationDetailsDto();
        dto.setStationNumber(stationNumber);
        dto.setOpeningDate(openingDate == null ? defaultOpenDate: openingDate);
        dto.setStationCode(stationCode);
        dto.setLineCode(lineCode);
        dto.setStationName(stationName);

        return dto;
    }

    public static StationDetailsDto getDefaultStationDetailsDto() {
        StationDetailsDto dto = new StationDetailsDto();
        dto.setStationNumber(1);
        dto.setOpeningDate(defaultOpenDate);
        dto.setStationCode("EW01");
        dto.setLineCode("EW");
        dto.setStationName("TEST_STATION_1");

        return dto;
    }
}
