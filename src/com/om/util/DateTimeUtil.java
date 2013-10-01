package com.om.util;



import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: dosapati
 * Date: 7/2/12
 * Time: 9:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class DateTimeUtil {

    static PeriodFormatter formatter = new PeriodFormatterBuilder()
            .appendYears().appendSuffix(" years, ")
            .appendMonths().appendSuffix(" months, ")
            .appendWeeks().appendSuffix(" weeks, ")
            .appendDays().appendSuffix(" days, ")
            .appendHours().appendSuffix(" hours, ")
            .appendMinutes().appendSuffix(" min(s), ")
            .appendSeconds().appendSuffix(" sec(s)")
            .printZeroNever()
            .toFormatter();

    public static  String getPeriodicDiffStr(long oldDateLongVal){
        DateTime myBirthDate = new DateTime(oldDateLongVal);
        DateTime now = new DateTime();
        Period period = new Period(myBirthDate, now);



        String elapsed = formatter.print(period);
        //System.out.println("elapsed = " + elapsed);
        //System.out.println(elapsed + " ago");
        return elapsed;
    }

    public static String getFormattedDate(int addOrMinusFromCurrent){
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");

        return sd.format(getDate(addOrMinusFromCurrent));
    }

    public static Date getDateSince(int addOrMinusFromCurrent){
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        String fmDate = sd.format (getDate(addOrMinusFromCurrent));
        try {
            return sd.parse(fmDate);
        } catch (ParseException e) {}
        return new Date();
    }

    public static Date getDate(int addOrMinusFromCurrent){
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DATE, addOrMinusFromCurrent);
        return c.getTime();
    }

    public static Date getHoursSince(int addOrMinusFromCurrent){
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        String fmDate = sd.format (getDateByHours(addOrMinusFromCurrent));
        try {
            return sd.parse(fmDate);
        } catch (ParseException e) {}
        return new Date();
    }

    public static Date getDateByHours(int addOrMinusFromCurrent){
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.HOUR_OF_DAY, addOrMinusFromCurrent);
        return c.getTime();
    }
}

