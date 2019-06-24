package np.com.naxa.staffattendance.utlils;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

public class DateConvertor {

    @SuppressLint("UseSparseArrays")
    private static HashMap<Integer, String> month = new HashMap<>();


    public static String getCurrentDate() {
        Date cDate = new Date();
        return formatDate(cDate);
    }


    static {
        month.put(0, "January");
        month.put(1, "February");
        month.put(2, "March");
        month.put(3, "April");
        month.put(4, "May");
        month.put(5, "June");
        month.put(6, "July");
        month.put(7, "August");
        month.put(8, "September");
        month.put(9, "October");
        month.put(10, "November");
        month.put(11, "December");
    }

    public static String formatDate(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(date);
    }

    public static Date stringToDate(String dateString) {
        // SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        Date date = null;
        try {
            date = format.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }


    public static void getFutureDate(Date currentDate, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentDate);
        cal.add(Calendar.DATE, days);

        Date futureDate = cal.getTime();
    }


    public static Date getPastDate(int days) {
        Date currentDate = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentDate);
        cal.add(Calendar.DATE, days);
        Date pastDate = cal.getTime();
        return pastDate;
    }

    public static String[] getYearMonthDay(Date date) {

        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        return new String[]{
                getDayOfMonth(month) + ", " + year, String.valueOf(day), getDayOfWeek(cal.get(Calendar.DAY_OF_WEEK))
        };
    }


    private static String getDayOfMonth(int value) {
        return month.get(value);
    }

    private static String getDayOfWeek(int value) {
        String day = "";
        switch (value) {
            case 1:
                day = "Sunday";
                break;
            case 2:
                day = "Monday";
                break;
            case 3:
                day = "Tuesday";
                break;
            case 4:
                day = "Wednesday";
                break;
            case 5:
                day = "Thursday";
                break;
            case 6:
                day = "Friday";
                break;
            case 7:
                day = "Saturday";
                break;
        }
        return day;
    }


    public static Date getDateForPosition(int pos) {
        Date date;

        switch (pos) {
            case 0:
                date = DateConvertor.getPastDate(-6);
                break;
            case 1:
                date = DateConvertor.getPastDate(-5);
                break;
            case 2:
                date = DateConvertor.getPastDate(-4);
                break;
            case 3:
                date = DateConvertor.getPastDate(-3);
                break;
            case 4:
                date = DateConvertor.getPastDate(-2);
                break;
            case 5:
                date = DateConvertor.getPastDate(-1);
                break;
            default:
                date = new Date();
                break;
        }


        return date;
    }
}
