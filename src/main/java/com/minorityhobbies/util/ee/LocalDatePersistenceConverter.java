package com.minorityhobbies.util.ee;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;

@Converter(autoApply = true)
public class LocalDatePersistenceConverter implements AttributeConverter<LocalDate, Date> {
	@Override
	public Date convertToDatabaseColumn(LocalDate entityValue) {
		return Date.valueOf(entityValue);
	}

	@Override
	public LocalDate convertToEntityAttribute(
			Date databaseValue) {
		return databaseValue.toLocalDate();
	}
}
