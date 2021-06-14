package com.kirtesh.subwaytransition.utils;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class DateTimeUtility {
    /**
     * @param date in format 10 February 1996 (d MMMM yyyy)
     * @return LocalDate
     */
    public static LocalDate convertStringToLocalDate(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy");
        return  LocalDate.parse(date, formatter);
    }
}
