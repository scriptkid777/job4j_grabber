package ru.job4j.quartz;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class HabrCareerParseTest {

    private static HabrCareerParse habrCareerParse;

    @BeforeAll
    public static void init() {
        habrCareerParse = new HabrCareerParse();
    }

    @Test
    public void parseFormattedDate1() {
        String date = "2024-02-21T18:21:56+03:00";
        LocalDateTime parsedDate = habrCareerParse.parse(date);
        LocalDateTime expectDate = LocalDateTime.parse(date, DateTimeFormatter.ISO_DATE_TIME);
        assertEquals(parsedDate, expectDate);
    }

    @Test
    public void parseFormattedDate2() {
        String invalidDate = "invalidDateTimeString";
        LocalDateTime parsedDate = habrCareerParse.parse(invalidDate);
        assertNull(parsedDate);
    }
}