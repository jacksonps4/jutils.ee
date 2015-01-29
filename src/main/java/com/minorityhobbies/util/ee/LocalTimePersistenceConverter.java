package com.minorityhobbies.util.ee;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.sql.Time;
import java.time.LocalTime;

@Converter(autoApply = true)
public class LocalTimePersistenceConverter implements AttributeConverter<LocalTime, Time> {
	@Override
	public Time convertToDatabaseColumn(LocalTime entityValue) {
		return Time.valueOf(entityValue);
	}

	@Override
	public LocalTime convertToEntityAttribute(
			Time databaseValue) {
		return databaseValue.toLocalTime();
	}
}
