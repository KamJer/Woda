package com.kamjer.woda;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.kamjer.woda.database.converters.Converter;

import org.junit.Test;

import java.time.LocalDate;

public class DateConverterUnitTest {

    @Test
    public void testToLocalDate() {
        String dateString = "2023-05-30";
        LocalDate expectedDate = LocalDate.of(2023, 5, 30);

        LocalDate result = Converter.toLocalDate(dateString);

        assertEquals(expectedDate, result);
    }

    @Test
    public void testToLocalDateWithNull() {
        String dateString = null;

        LocalDate result = Converter.toLocalDate(dateString);

        assertNull(result);
    }

    @Test
    public void testFromLocalDate() {
        LocalDate date = LocalDate.of(2023, 5, 30);
        String expectedString = "2023-05-30";

        String result = Converter.fromLocalDate(date);

        assertEquals(expectedString, result);
    }

    @Test
    public void testFromLocalDateWithNull() {
        LocalDate date = null;

        String result = Converter.fromLocalDate(date);

        assertNull(result);
    }
}
