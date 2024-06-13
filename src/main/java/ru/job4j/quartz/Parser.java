package ru.job4j.quartz;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Parser implements DateTimeParser {
    @Override
    public LocalDateTime parse(String parse) {
        try {
           return LocalDateTime.parse(parse, DateTimeFormatter.ISO_DATE_TIME);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}
