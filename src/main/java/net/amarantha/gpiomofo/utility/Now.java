package net.amarantha.gpiomofo.utility;

import javax.inject.Singleton;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;

import static java.time.ZoneOffset.UTC;

@Singleton
public class Now {

    public LocalDateTime now() {
        if ( offset==null ) {
            return LocalDateTime.now();
        }
        return LocalDateTime.ofEpochSecond(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) + offset, 0, ZoneOffset.UTC);
    }

    public long epochMilli() {
        return now().toInstant(UTC).toEpochMilli();
    }

    public LocalDate date() {
        return now().toLocalDate();
    }

    public LocalTime time() {
        return now().toLocalTime();
    }

    private Long offset = null;

    public void setDate(String date) {
        setDateTime(date, time().toString());
    }

    public void setTime(String time) {
        setDateTime(date().toString(), time);
    }

    public void setDateTime(String date, String time) {
        LocalDateTime target = LocalDateTime.parse(date + "T" + time);
        offset = target.toEpochSecond(ZoneOffset.UTC) - LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
    }

    public void pushSeconds(long seconds) {
        offset += seconds;
    }

    public void pushMinutes(long minutes) {
        pushSeconds(minutes * 60);
    }

}
