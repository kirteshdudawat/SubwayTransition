package com.kirtesh.subwaytransition.utils.converter;

import com.kirtesh.subwaytransition.cache.JvmCache;
import com.kirtesh.subwaytransition.response.SearchAvailablePathResponse;
import com.kirtesh.subwaytransition.services.models.CalculatedPath;
import com.kirtesh.subwaytransition.sro.SearchAvailablePathRequestSro;
import org.apache.commons.lang3.StringUtils;

/**
 * Converters to convert Models to Response.
 * Models are generally used internally via single service.
 * Response would be API response.
 */
public class ModelToResponseConverter {

    public static SearchAvailablePathResponse generateResponse(CalculatedPath fastestPath, CalculatedPath shortestPath, SearchAvailablePathRequestSro requestSro) {
        String sourceStationName = JvmCache.getStationCodeToDtoMap().get(requestSro.getSourceStationCode()).getStationName();
        String destinationStationName = JvmCache.getStationCodeToDtoMap().get(requestSro.getDestinationStationCode()).getStationName();

        StringBuffer response = new StringBuffer("### Fastest Route from ").append(sourceStationName).append(" to ").append(destinationStationName).append(". (Time based)").append("\n")
                .append("Journey Start Time: ").append(fastestPath.getJourneyStartDateTime()).append("\n")
                .append("Journey End Time: ").append(fastestPath.getJourneyEndDateTime()).append("\n")
                .append("Total Travel Time (In Minutes): ").append(fastestPath.getTotalJourneyTimeInMinutes()).append("\n")
                .append("Total Stations in Journey: ").append(fastestPath.getTotalStationsInJourney()).append("\n\n")
                .append("Stations Travelled: ").append(fastestPath.getStationCodes().toString()).append("\n\n")
                .append("Details: ").append("\n")
                .append(StringUtils.join(fastestPath.getSteps(), "\n")).append("\n\n\n")
                .append("### Shortest Route from ").append(sourceStationName).append(" to ").append(destinationStationName).append(". (Based on station travelled)").append("\n")
                .append("Journey Start Time: ").append(shortestPath.getJourneyStartDateTime()).append("\n")
                .append("Journey End Time: ").append(shortestPath.getJourneyEndDateTime()).append("\n")
                .append("Total Travel Time (In Minutes): ").append(shortestPath.getTotalJourneyTimeInMinutes()).append("\n")
                .append("Total Stations in Journey: ").append(shortestPath.getTotalStationsInJourney()).append("\n\n")
                .append("Stations Travelled: ").append(shortestPath.getStationCodes().toString()).append("\n\n")
                .append("Details: ").append("\n")
                .append(StringUtils.join(shortestPath.getSteps(), "\n"));

        return new SearchAvailablePathResponse(response.toString());
    }
}
