package com.example.authodo.adapter.in.web.formatter;

import org.springframework.format.Formatter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class LocalDateFormatter implements Formatter<LocalDate> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public LocalDate parse(String text, Locale locale) {
        return LocalDate.parse(text, FORMATTER);
    }

    @Override
    public String print(LocalDate object, Locale locale) {
        return FORMATTER.format(object);
    }
}
