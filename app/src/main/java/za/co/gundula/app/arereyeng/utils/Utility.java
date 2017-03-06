package za.co.gundula.app.arereyeng.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


/**
 * Created by kgundula on 2017/02/14.
 */

public class Utility {


    public static NetworkInfo getNetworkWorkInfo(Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo();

    }


    public static String getISOCurrentDateTime(final Date date) {
        String formatted = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.ENGLISH)
                .format(date);
        return formatted.substring(0, 19) + "Z";
    }

    /**
     * @param dateObject value of date
     * @return formatted date
     */
    public static String formatDate(Date dateObject) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("LLL dd, yyyy");
        return dateFormat.format(dateObject);
    }

    /**
     * @param dateObject value of time
     * @return formatted time
     */
    public static String formatTime(Date dateObject) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        return timeFormat.format(dateObject);
    }


    public static Date getDateFromISOString(String inputDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.ENGLISH);
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(dateFormat.parse(inputDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        cal.add(Calendar.DATE, 0);

        return cal.getTime();
    }

    public static String getISODateFromDate(String inputDate) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.ENGLISH);
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(dateFormat.parse(inputDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        cal.add(Calendar.DATE, 1);

        Date date = cal.getTime();
        String formatted = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.ENGLISH)
                .format(date);

        return formatted.substring(0, 16) + "Z";
    }

}
