package jsf2.demo.scrum.infra.util;

import static org.junit.Assert.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;

public class DatesTest {

    @Test
    public void createYMD() {
        Date date = Dates.create(2010, 4, 12);

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        assertEquals("2010-04-12 00:00:00", df.format(date));
    }

    @Test
    public void createYMD_HMS() {
        Date date = Dates.create(2010, 4, 12, 1, 2, 3);

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        assertEquals("2010-04-12 01:02:03", df.format(date));
    }
}
