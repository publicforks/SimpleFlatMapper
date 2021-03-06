package org.sfm.utils.conv.time;

import org.sfm.utils.conv.Converter;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class DateToJavaLocalDateTimeConverter implements Converter<Date, LocalDateTime> {
    private final ZoneId dateTimeZone;

    public DateToJavaLocalDateTimeConverter(ZoneId dateTimeZone) {
        this.dateTimeZone = dateTimeZone;
    }

    @Override
    public LocalDateTime convert(Date in) throws Exception {
        if (in == null) return null;
        return in.toInstant().atZone(dateTimeZone).toLocalDateTime();
    }
}
