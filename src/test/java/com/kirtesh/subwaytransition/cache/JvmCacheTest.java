package com.kirtesh.subwaytransition.cache;

import com.kirtesh.subwaytransition.dto.StationDetailsDto;
import com.kirtesh.subwaytransition.dto.StationDetailsDtoFixture;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Collectors;

public class JvmCacheTest {
    @Test
    void addToStationNameToDtoListMapTest() {
        JvmCacheTestFixture testFixture = new JvmCacheTestFixture();

        // Expected result from test
        Set<String> expectedKeys = Set.of(testFixture.firstStation.getStationName(), testFixture.secondStation.getStationName());
        Set<String> expectedStationNamesForFirstKey = Set.of(testFixture.firstStation.getStationName(), testFixture.thirdStation.getStationName(), testFixture.fourthStation.getStationName());
        Set<String> expectedStationNamesForSecondKey = Set.of(testFixture.secondStation.getStationName());

        //Prepare Data for test
        testFixture.initializeCache();

        // Fetch Result
        Map<String, List<StationDetailsDto>> result = testFixture.cache.getStationNameToDtoListMap();
        Set<String> stationNamesForFirstKey = result.get(testFixture.firstStation.getStationName()).stream().map(StationDetailsDto::getStationName).collect(Collectors.toSet());
        Set<String> stationNamesForSecondKey = result.get(testFixture.secondStation.getStationName()).stream().map(StationDetailsDto::getStationName).collect(Collectors.toSet());

        // Validate Result
        Assert.isTrue(expectedKeys.equals(result.keySet()), "Different keyset found in JvmCacheTest.addToStationNameToDtoListMapTest()");
        Assert.isTrue(expectedStationNamesForFirstKey.equals(stationNamesForFirstKey), "Different stationNames found for first key in JvmCacheTest.addToStationNameToDtoListMapTest()");
        Assert.isTrue(expectedStationNamesForSecondKey.equals(stationNamesForSecondKey), "Different stationNames found for second key in JvmCacheTest.addToStationNameToDtoListMapTest()");
    }

    class JvmCacheTestFixture {
        JvmCache cache = new JvmCache();

        StationDetailsDto firstStation = StationDetailsDtoFixture.getDefaultStationDetailsDto();
        StationDetailsDto secondStation = StationDetailsDtoFixture.getCustomizedStationDetailsDto(2, "EW02", "EW", "TEST_STATION_2", null);
        StationDetailsDto thirdStation = StationDetailsDtoFixture.getCustomizedStationDetailsDto(3, "EW03", "EW", "TEST_STATION_3", null);
        StationDetailsDto fourthStation = StationDetailsDtoFixture.getCustomizedStationDetailsDto(4, "EW04", "EW", "TEST_STATION_4", null);

        public void initializeCache() {
            this.cache.addToStationNameToDtoListMap(this.firstStation.getStationName(), this.firstStation);
            this.cache.addToStationNameToDtoListMap(this.firstStation.getStationName(), this.thirdStation);
            this.cache.addToStationNameToDtoListMap(this.firstStation.getStationName(), this.fourthStation);
            this.cache.addToStationNameToDtoListMap(this.secondStation.getStationName(), this.secondStation);
        }
    }
}
