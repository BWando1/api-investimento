package com.investimento.domain.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

@Converter(autoApply = false)
public class OffsetDateTimeStringConverter implements AttributeConverter<OffsetDateTime, String> {

    @Override
    public String convertToDatabaseColumn(OffsetDateTime attribute) {
        return attribute == null ? null : attribute.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    @Override
    public OffsetDateTime convertToEntityAttribute(String dbData) {
        return dbData == null || dbData.isBlank() ? null : OffsetDateTime.parse(dbData, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }
}