package com.kirtesh.subwaytransition.utils.converter;

import com.kirtesh.subwaytransition.sro.SearchAvailablePathRequestSro;

import java.time.LocalDateTime;
/**
 * Converters to convert API request to SRO.
 * SROs are used to pass data between different services.
 * SRO -> Service Request/Response Objects.
 */
public class RequestToSroConverter {
    public static SearchAvailablePathRequestSro getAvailablePathRequestSro(String sourceStationCode, String destinationStationCode, LocalDateTime travelTime) {
        String sourceStation = sourceStationCode.toUpperCase();
        String destinationStation = destinationStationCode.toUpperCase();
        return new SearchAvailablePathRequestSro(sourceStation, destinationStation, travelTime);
    }
}
