package nokieng.gdgvientiane.org.laoair.Helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Spinner;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import nokieng.gdgvientiane.org.laoair.R;
import nokieng.gdgvientiane.org.laoair.data.KContact;

/**
 * Created by kieng on 4/9/2015.
 */
public class Utilities {

    private static final String TAG = Utilities.class.getSimpleName();
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private SharedPreferences settingPre;

    private Context mContext;

    public Utilities(Context context) {
        this.mContext = context;
        settingPre = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
//        Log.d(TAG, "Currency : " + settingPre.getString(context.getString(R.string.KEY_CURRENCY), ""));
//        Log.d(TAG, "Pref History :" + settingPre.getString(mContext.getResources().getString(R.string.KEY_KEEP_HISTORY), "DEFAULT"));
    }

    public boolean isFirstUse() {
        return settingPre.getBoolean(mContext.getResources().getString(R.string.KEY_IS_FIRST_USE), false);
    }

    public void setUsed() {
        SharedPreferences.Editor editor = settingPre.edit();
        editor.putBoolean(mContext.getResources().getString(R.string.KEY_IS_FIRST_USE), false);
        editor.commit();
    }

    public int getNumDayKeepHistory() {
        return Integer.valueOf(settingPre.getString(mContext.getResources().getString(R.string.KEY_KEEP_HISTORY), "15"));
    }

    public boolean isCurrentDate(String date) {
        try {
            Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH) + 1;
            int year = calendar.get(Calendar.YEAR);
            String strNow = String.valueOf(day) + "-" + String.valueOf(month) + "-" + String.valueOf(year);
            SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT);
            Date date1 = df.parse(date);
            Date now = df.parse(strNow);

            return date1.equals(now) || date1.before(now);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }


    public static boolean isDateAfter(String startDate, String endDate) {
        try {
            SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT);
            Date date1 = df.parse(endDate);
            Date startingDate = df.parse(startDate);

            return date1.after(startingDate) || date1.equals(startingDate);
        } catch (Exception e) {
            return false;
        }
    }


    public boolean isDateValid(String date) {
        try {
            DateFormat df = new SimpleDateFormat(DATE_FORMAT);
            df.setLenient(false);
            df.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    public long convert_yyyy_MM_dd_ToMillis(String yyyy_MM_dd) {
        GregorianCalendar gc = new GregorianCalendar(TimeZone.getDefault());
        gc.clear();
        String[] date = yyyy_MM_dd.split("-");
        gc.set(Integer.parseInt(date[0]), (Integer.parseInt(date[1]) - 1), Integer.parseInt(date[2]));
        //reduce -1 mean . month always start with 0. 0 mean january ,
        return gc.getTimeInMillis();
    }

    public long getCurrentDateInMillis() {
        return System.currentTimeMillis();
    }

    //re turn readable format
    public String convertLongToDateTime(Long dateLong) {
        Date date = new Date(dateLong);
        return DateFormat.getDateInstance().format(date);
    }

    //1 sec = 1000 millis
    // 1 min = 60 sec
    //1 h = 60 min
    //1 day = 24 h
    // => 24 * 60 * 60 * 1000
    public String calculateDate(long date) {
        long now = getCurrentDateInMillis();
        if (now >= date) {
            long drift = now - date;
            if (drift < 5 * 24 * 60 * 60 * 1000) {
                if (drift < 1000)
                    return "Just Now";
                else if (drift < 60 * 1000) {
                    String d = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(drift));
                    return d + " seconds ago";
                } else if (drift < (60 * 60 * 1000) + 1) {
                    String d = String.valueOf(TimeUnit.MILLISECONDS.toMinutes(drift));
                    return d + " minutes ago";
                } else if (drift < 24 * 60 * 60 * 1000) {
                    String d = String.valueOf(TimeUnit.MILLISECONDS.toHours(drift));
                    return (d + " hours ago");
                } else {
                    return String.valueOf(TimeUnit.MILLISECONDS.toDays(drift));
                }
            } else
                return String.valueOf(convertLongToDateTime(date));
        } else
            return "Date must greater than now";
    }

    public String getVersion() {
        String versionName = "Lao Airways V ";
        try {
            versionName = versionName + mContext.getPackageManager()
                    .getPackageInfo(mContext.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            versionName = "Unavailable Version";
        }
        return versionName;
    }

    public String calPrice(String price) {
        settingPre = PreferenceManager.getDefaultSharedPreferences(mContext);
        Cursor cursor = null;

        String params = price.replaceAll("[a-zA-Z]", "").trim();
        float num = Float.valueOf(params);
        String unit = settingPre.getString(mContext.getString(R.string.KEY_CURRENCY), "USD");
        Log.d(TAG, "unit : " + unit);
        Log.d(TAG, "Price :" + num);
        String strRate = "";
        if (unit.equals("USD")) {
            strRate = String.valueOf(num);   //DEFAULT of currency is USD
        } else if (unit.equals("EUR")) {
            cursor = getExchangeRate(("USD" + unit).toUpperCase());
            cursor.moveToFirst();
            float rate = Float.valueOf(cursor.getString(KContact.CurrencyRate.COL_RATE));
            strRate = String.valueOf(num * rate);
        } else if (unit.equals("CNY")) {
            cursor = getExchangeRate(("USD" + unit).toUpperCase());
            cursor.moveToFirst();
            strRate = String.valueOf(num * Float.valueOf(cursor.getString(KContact.CurrencyRate.COL_RATE)));
        } else if (unit.equals("THB")) {
            cursor = getExchangeRate(("USD" + unit).toUpperCase());
            cursor.moveToFirst();
            strRate = String.valueOf(num * Float.valueOf(cursor.getString(KContact.CurrencyRate.COL_RATE)));
        } else if (unit.equals("LAK")) {
            cursor = getExchangeRate(("USD" + unit).toUpperCase());
            cursor.moveToFirst();
            strRate = String.valueOf(num * Float.valueOf(cursor.getString(KContact.CurrencyRate.COL_RATE)));
        }
        return strRate + " " + unit.toUpperCase();
    }

    private Cursor getExchangeRate(String currency) {
        Uri uri = Uri.parse(KContact.CurrencyRate.CONTENT_URI + "/" + currency);
        Cursor cursor = mContext.getContentResolver().query(uri,
                KContact.CurrencyRate.columnHacks,
                null,
                null,
                null);
        return cursor;
    }

    public void ClearHistory() {
        long _id = mContext.getContentResolver().delete(
                KContact.History.CONTENT_URI,
                null,
                null
        );
    }

    /*
    * International
    * */

    public String getInterLeaveFrom(Spinner spinner) {
        switch (spinner.getSelectedItemPosition()) {
            case Spinner.INVALID_POSITION:
                return "INVALID";
            case 1:
                return "PNH";
            case 2:
                return "REP";
            case 3:
                return "CAN";
            case 4:
                return "JHG";
            case 5:
                return "KMG";
            case 6:
                return "SEL";
            case 7:
                return "LPQ";
            case 8:
                return "PKZ";
            case 9:
                return "ZVK";
            case 10:
                return "VTE";
            case 11:
                return "SIN";
            case 12:
                return "BKK";
            case 13:
                return "CNX";
            case 14:
                return "HAN";
            case 15:
                return "SGN";
            default:
                return "";
        }
    }
    /*
    * Domestic
    * */

    public String getDomesticLeaveFrom(Spinner spinner) {
        switch (spinner.getSelectedItemPosition()) {
            case Spinner.INVALID_POSITION:
                return "INVALID";
            case 1:
                return "LXG";
            case 2:
                return "LPQ";
            case 3:
                return "ODY";
            case 4:
                return "PKZ";
            case 5:
                return "ZVK";
            case 6:
                return "VTE";
            case 7:
                return "XKH";
            default:
                return "";
        }
    }

    public String getCodeFullName(String code) {
        switch (code) {
            case "PNH":
                return "Phnom Penh";
            case "REP":
                return "Siem Reap";
            case "CAN":
                return "GuangZhou";
            case "JHG":
                return "Jinghong";
            case "KMG":
                return "Kunming";
            case "SEL":
                return "Seoul";
            case "LPQ":
                return "Luang Prabang";
            case "PKZ":
                return "Pakse";
            case "ZVK":
                return "Savannakhet";
            case "VTE":
                return "Vientiane";
            case "SIN":
                return "Singapore";
            case "BKK":
                return "Bangkok";
            case "CNX":
                return "Chiangmai";
            case "HAN":
                return "Hanoi";
            case "SGN":
                return "Ho Chi Minh";
            case "LXG":
                return "Luang Namtha";
            case "ODY":
                return "Oudomxay";
            case "XKH":
                return "Xieng Khouang";
            default:
                return code;
        }
    }

}