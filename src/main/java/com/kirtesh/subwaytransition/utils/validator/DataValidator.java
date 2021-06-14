package com.kirtesh.subwaytransition.utils.validator;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataValidator {
    /**
     * Station Code should follow following format
     *      1. Should be atleast 3 character long.
     *      2. First 2 characters should be alphabets.
     *      3. Post 2 characters, only numbers are allowed.
     *
     * @param stationCode
     * @return validates if station code is in correct format or not
     */
    public static boolean validateStationCode(String stationCode) {
        if (stationCode.length() >= 3
                && stationCode.substring(0,2).matches("^[a-zA-Z]*$")
                && stationCode.substring(2).matches("^[0-9]*$")) {
            return true;
        }
        return false;
    }

    public static boolean isPastDatetime(LocalDateTime datetime) {
        LocalDateTime validDateTime = LocalDateTime.now();
        return validDateTime.isAfter(datetime);
    }
}
