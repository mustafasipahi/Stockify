package com.stockify.project.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DateUtil {

    public static Long getTime(LocalDateTime localDateTime) {
        return Optional.ofNullable(localDateTime)
                .map(i -> Timestamp.valueOf(localDateTime).getTime())
                .orElse(null);
    }

    public static Long getTime(LocalDate localDate) {
        return Optional.ofNullable(localDate)
                .map(i -> Timestamp.valueOf(localDate.atStartOfDay()).getTime())
                .orElse(0L);
    }

    public static LocalDateTime getLocalDateTime(Long timestamp) {
        return Optional.ofNullable(timestamp)
                .map(i -> LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault()))
                .orElse(null);
    }

    public static LocalDate getLocalDate(Long timestamp) {
        return Optional.ofNullable(timestamp)
                .map(i -> LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault()).toLocalDate())
                .orElse(null);
    }
}
