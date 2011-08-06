package jsf2.demo.scrum.infra.util;

import java.util.Calendar;
import java.util.Date;

public class Dates {

    public static Date create(int year, int month, int date) {
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(year, month - 1, date);
        return cal.getTime();
    }

    public static Date create(int year, int month, int date, int hour, int minute, int second) {
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(year, month - 1, date, hour, minute, second);
        return cal.getTime();
    }
}
