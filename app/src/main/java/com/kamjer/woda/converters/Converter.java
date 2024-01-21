package com.kamjer.woda.converters;

import androidx.room.TypeConverter;

import java.time.LocalDate;

public class Converter {

    @TypeConverter
    public static LocalDate toLocalDate(String date) {
        return date == null ? null : LocalDate.parse(date);
    }

    @TypeConverter
    public static String fromLocalDate(LocalDate date) {
        return date == null ? null : date.toString();
    }
}
