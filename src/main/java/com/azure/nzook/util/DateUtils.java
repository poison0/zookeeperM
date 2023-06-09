package com.azure.nzook.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
/**
 * @author niu
 * @since 1.0
 */
public class DateUtils {
    public static String toAge(long time) {
        var creation = LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault());
        var now = LocalDateTime.now();

        var years = ChronoUnit.YEARS.between(creation, now);
        if (years > 0) {
            return pluralize(years, Bundle.getString("description.date.year"));
        }
        var months = ChronoUnit.MONTHS.between(creation, now);
        if (months > 0) {
            return pluralize(months, Bundle.getString("description.date.month"));
        }
        var days = ChronoUnit.DAYS.between(creation, now);
        if (days > 0) {
            return pluralize(days, Bundle.getString("description.date.day"));
        }
        var hours = ChronoUnit.HOURS.between(creation, now);
        if (hours > 0) {
            return pluralize(hours, Bundle.getString("description.date.hour"));
        }
        var minutes = ChronoUnit.MINUTES.between(creation, now);
        if (minutes > 0) {
            return pluralize(minutes, Bundle.getString("description.date.minute"));
        }
        var seconds = ChronoUnit.SECONDS.between(creation, now);
        if (seconds > 0) {
            return pluralize(seconds, Bundle.getString("description.date.seconds"));
        }

        return Bundle.getString("description.date.few");
    }

    private static String pluralize(long strictlyPositiveCount, String singular) {
        return pluralize(strictlyPositiveCount, singular, singular + Bundle.getString("description.date.plural"));
    }

    private static String pluralize(long strictlyPositiveCount, String singular, String plural) {
        String separator = "";
        if ("1".equals(Bundle.getString("description.date.separator"))) {
            separator = " ";
        }
        if (strictlyPositiveCount == 1) {
            return "1" + separator + singular + separator + Bundle.getString("description.date.ago");
        }
        return strictlyPositiveCount + separator + plural + separator +Bundle.getString("description.date.ago");
    }
}
