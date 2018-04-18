package np.com.naxa.staffattendance.utlils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateConvertor {


    public static String getCurrentDate() {
        Date cDate = new Date();
        return formatDate(cDate);
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
}
