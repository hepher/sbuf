import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.StringUtils.defaultIfBlank;

public class TimeUtils {

    private final static String ISO_8601_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    private final static int FIRST_MONT_OF_YEAR = 1;
    private final static int LAST_MONT_OF_YEAR = 12;
    private final static int FIRST_DAY_OF_MONTH = 1;
    private final static int LAST_DAY_OF_MONTH = 31;
    private final static int FIRST_HOUR_OF_DAY = 0;
    private final static int LAST_HOUR_OF_DAY = 23;
    private final static int FIRST_MINUTE_OF_HOUR = 0;
    private final static int LAST_MINUTE_OF_HOUR = 59;
    private final static int FIRST_SECOND_OF_MINUTE = 0;
    private final static int LAST_SECOND_OF_MINUTE = 59;
    private final static int FIRST_NANO_OF_SECOND = 0;
    private final static int LAST_NANO_OF_SECOND = 999999999;

    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(ISO_8601_PATTERN);

    // date conversions
    public static Date getDate(long milliseconds) {
        return new Date(milliseconds);
    }

    public static Date getDate(String dateInString) throws ParseException {
        return simpleDateFormat.parse(getZonedDateTimeAtTimeZone(dateInString, ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ")));
    }

    public static Date getDateWithTimeZone(long milliseconds, ZoneId zoneId) {
        return Date.from(getInstantWithTimeZone(milliseconds, zoneId));
    }

    public static Date getDateWithTimeZone(String dateInString, ZoneId zoneId) {
        return Date.from(getInstantWithTimeZone(dateInString, zoneId));
    }

    public static Date getDateAtTimeZone(long milliseconds, ZoneId zoneId) {
        return Date.from(getInstantAtTimeZone(milliseconds, zoneId));
    }

    public static Date getDateAtTimeZone(String dateInString, ZoneId zoneId) {
        return Date.from(getInstantAtTimeZone(dateInString, zoneId));
    }

    // calendar
    public static Calendar getCalendar(long milliseconds) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliseconds);
        return calendar;
    }

    public static Calendar getCalendar(String dateInString) throws ParseException {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(getDate(dateInString));
        return calendar;
    }

    public static Instant getInstant(long milliseconds) {
        return Instant.ofEpochMilli(milliseconds);
    }

    public static Instant getInstant(String dateInString) {
        return ZonedDateTime.parse(normalizeDateInStringToISO8610(dateInString)).toInstant();
    }

    public static Instant getInstantWithTimeZone(long milliseconds, ZoneId zoneId) {
        return Instant.ofEpochMilli(milliseconds).atZone(ZoneOffset.UTC).withZoneSameLocal(zoneId).toInstant();
    }

    public static Instant getInstantWithTimeZone(String dateInString, ZoneId zoneId) {
        return getZonedDateTimeWithTimeZone(dateInString, zoneId).toInstant();
    }

    public static Instant getInstantAtTimeZone(long milliseconds, ZoneId zoneId) {
        return Instant.ofEpochMilli(milliseconds).atZone(ZoneOffset.UTC).withZoneSameInstant(zoneId).toInstant();
    }

    public static Instant getInstantAtTimeZone(String dateInString, ZoneId zoneId) {
        return getZonedDateTimeAtTimeZone(dateInString, zoneId).toInstant();
    }

    // LocalDate conversions
    public static LocalDate getLocalDate(long milliseconds) {
        return Instant.ofEpochMilli(milliseconds).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static LocalDate getLocalDate(String dateInString) {
        return getZonedDateTimeWithTimeZone(dateInString, ZoneId.systemDefault()).toLocalDate();
    }

    // LocalDateTime conversions
    public static LocalDateTime getLocalDateTime(long milliseconds) {
        return Instant.ofEpochMilli(milliseconds).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public static LocalDateTime getLocalDateTime(String dateInString) {
        return LocalDateTime.parse(normalizeDateInStringToISO8610(dateInString, false));
    }

    // Zoned Date Time
    public static ZonedDateTime getZonedDateTime(String dateInString) {
        return ZonedDateTime.parse(normalizeDateInStringToISO8610(dateInString));
    }

    public static ZonedDateTime getZonedDateTimeWithTimeZone(String dateInString, ZoneId zoneId) {
        return ZonedDateTime.parse(normalizeDateInStringToISO8610(dateInString)).withZoneSameLocal(zoneId);
    }

    public static ZonedDateTime getZonedDateTimeAtTimeZone(String dateInString, ZoneId zoneId) {
        return ZonedDateTime.parse(normalizeDateInStringToISO8610(dateInString)).withZoneSameInstant(zoneId);
    }

    public static ZonedDateTime getStartOfYear() {
        return ZonedDateTime.now(ZoneId.systemDefault())
                .withDayOfYear(FIRST_DAY_OF_MONTH)
                .withMonth(FIRST_MONT_OF_YEAR)
                .withHour(FIRST_HOUR_OF_DAY)
                .withMinute(FIRST_MINUTE_OF_HOUR)
                .withSecond(FIRST_SECOND_OF_MINUTE)
                .withNano(FIRST_NANO_OF_SECOND);
    }

    public static ZonedDateTime getEndOfYear() {
        return ZonedDateTime.now(ZoneId.systemDefault())
                .withDayOfYear(LAST_DAY_OF_MONTH)
                .withMonth(LAST_MONT_OF_YEAR)
                .withHour(LAST_HOUR_OF_DAY)
                .withMinute(LAST_MINUTE_OF_HOUR)
                .withSecond(LAST_SECOND_OF_MINUTE)
                .withNano(LAST_NANO_OF_SECOND);
    }

    // normalize to ISO8610
    public static String normalizeDateInStringToISO8610(String dateInString) {
        return normalizeDateInStringToISO8610(dateInString, true);
    }

    private static String normalizeDateInStringToISO8610(String dateInString, boolean timezone) {

        if (StringUtils.isBlank(dateInString)) {
            throw new RuntimeException("the input string cannot be blank");
        }

        // regular expression for standard ISO 8601
        String dateTimePattern = "(\\d{4})[-]?(\\d{2})[-]?(\\d{2})" + // date pattern, contains 3 groups (YYYY, MM, DD )
                "[T]?" + // Time separator
                "(\\d{0,2})[:]?(\\d{0,2})[:]?(\\d{0,2})[\\.]?(\\d{0,9})" + // time pattern, contains 4 groups (HH, mm, SS, NNN )
                "([Z]?)" + // match value +00:00 in ISO 8601 standard
                "([+-]?\\d{0,2})[:]?(\\d{0,2})"; // offset time zone pattern, contains 2 groups ( +/- hours, minutes )

        Pattern pattern = Pattern.compile(dateTimePattern);
        Matcher matcher = pattern.matcher(dateInString);

        if (!matcher.find()) {
            throw new RuntimeException("Input string isn't a date in format ISO 8601");
        }

        String find = matcher.group();
        String year = matcher.group(1);
        String month = matcher.group(2);
        String day = matcher.group(3);
        String hours = defaultIfBlank(matcher.group(4), "00");
        String minutes = defaultIfBlank(matcher.group(5), "00");
        String seconds = defaultIfBlank(matcher.group(6), "00");
        String nanoseconds = defaultIfBlank(matcher.group(7), null);
        String zOffSet = "Z".equalsIgnoreCase(matcher.group(8)) ? "+00:00" : null;
        String hoursOffSet = defaultIfBlank(matcher.group(9), "+00");
        String minutesOffSet = defaultIfBlank(matcher.group(10), "00");
        String timeZone = defaultIfBlank(dateInString.replace(find, ""), ""); // from original string remove the matched string

        // date ISO 8601
        String result = year + "-" + month + "-" + day + "T" + hours + ":" + minutes + ":" + seconds + (nanoseconds != null ? "." + nanoseconds : "");
        if (timezone) {
            result = result + (zOffSet != null ? zOffSet : hoursOffSet + ":" + minutesOffSet) + timeZone;
        }

        return result;
    }
}
