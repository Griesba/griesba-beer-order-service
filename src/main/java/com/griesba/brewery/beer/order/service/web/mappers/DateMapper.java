package com.griesba.brewery.beer.order.service.web.mappers;

import com.fasterxml.jackson.datatype.jsr310.ser.OffsetTimeSerializer;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Component
public class DateMapper {

    Timestamp asTimestamp(OffsetDateTime offsetDateTime) {
        if (offsetDateTime != null) {
            return Timestamp.valueOf(offsetDateTime.atZoneSameInstant(ZoneOffset.UTC).toLocalDateTime());
        } else {
            return null;
        }
    }

    OffsetDateTime asOffsetDateTime(Timestamp ts) {
        if (ts != null) {
            LocalDateTime ldt = ts.toLocalDateTime();
            return OffsetDateTime.of(ldt.getYear(), ldt.getMonthValue(), ldt.getDayOfMonth(), ldt.getHour(),
                    ldt.getMinute(), ldt.getSecond(), ldt.getNano(), ZoneOffset.UTC);
        } else {
            return null;
        }
    }
}
