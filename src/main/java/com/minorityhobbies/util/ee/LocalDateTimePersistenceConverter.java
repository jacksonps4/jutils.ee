package com.minorityhobbies.util.ee;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class LocalDateTimePersistenceConverter implements AttributeConverter<LocalDateTime, Timestamp> {
	@Override
	public Timestamp convertToDatabaseColumn(LocalDateTime entityValue) {
		return entityValue != null ? Timestamp.valueOf(entityValue) : null;
	}

	@Override
	public LocalDateTime convertToEntityAttribute(
			java.sql.Timestamp databaseValue) {
		return databaseValue != null ? databaseValue.toLocalDateTime() : null;
	}
}
