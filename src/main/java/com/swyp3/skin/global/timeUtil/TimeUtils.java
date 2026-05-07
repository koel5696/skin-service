package com.swyp3.skin.global.timeUtil;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class TimeUtils {

    public static final ZoneId KST = ZoneId.of("Asia/Seoul");
    public static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private TimeUtils() {}

    public static String formatToKstDate(Instant instant) {
        return instant.atZone(KST).toLocalDate().format(DATE_FORMAT);
    }

    public static String formatToKstDateTime(Instant instant) {
        return instant.atZone(KST).toLocalDateTime().format(DATE_TIME_FORMAT);
    }
}
