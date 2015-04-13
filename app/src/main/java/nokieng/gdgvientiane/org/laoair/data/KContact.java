package nokieng.gdgvientiane.org.laoair.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by kieng on 4/6/2015.
 */
public class KContact {

    public static final String CONTENT_AUTHORITY = "nokieng.gdgvientiane.org.laoair.provider";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_CURRENCY_RATE = "currency_rate";
    public static final String PATH_HISTORY = "flight_history";

    //we save year in for mat 1994-03-25
    //but in data base we save as 19940325. it will be easier
    public static final class CurrencyRate implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CURRENCY_RATE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CURRENCY_RATE;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CURRENCY_RATE;

        public static final String TABLE_NAME = "currency_rate";

        public static final String COLUMN_NAME = "currency_name";
        public static final String COLUMN_RATE = "currency_rate";
        public static final String COLUMN_DATE = "currency_date"; //date save as 19940325 as TEXT

        public static String[] columnHacks = new String[]{
                _ID,
                COLUMN_NAME,
                COLUMN_RATE,
                COLUMN_DATE
        };
        //COLUMN HACK NO . AS WE DEFINE COLUMN HACK ABOVE
        public static final int COL_NAME = 1;
        public static final int COL_RATE = 2;
        public static final int COL_DATE = 3;

        public static Uri buildCurrencyRateUri(long _id) {
            return ContentUris.withAppendedId(CONTENT_URI, _id);
        }

        public static Uri buildCurrencyNameUri(String name) {
            return CONTENT_URI.buildUpon().appendPath(name).build();
        }
    }

    public static final class History implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_HISTORY).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_HISTORY;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_HISTORY;
/*        public static final String TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_HISTORY;*/

        public static final String TABLE_NAME = "fight_history";

        public static final String COLUMN_LEAVE_FROM = "LeaveFrom";
        public static final String COLUMN_GO_TO = "GoTo";
        public static final String COLUMN_FLIGHT_NO = "FlightNO";
        public static final String COLUMN_CLASS = "ClassType";
        public static final String COLUMN_PRICE = "Price";
        public static final String COLUMN_DEPART = "DepartureTime";
        public static final String COLUMN_ARRIVE = "ArriveTime";
        public static final String COLUMN_LEAVE_RETURN = "LeaveOrReturn";
        public static final String COLUMN_DETAIL = "Detail";
        public static final String COLUMN_DATE_INSERT = "dateInsert";

        public static final String[] columnHacks = new String[]{
                _ID,
                COLUMN_LEAVE_FROM,
                COLUMN_GO_TO,
                COLUMN_FLIGHT_NO,
                COLUMN_CLASS,
                COLUMN_PRICE,
                COLUMN_DEPART,
                COLUMN_ARRIVE,
                COLUMN_LEAVE_RETURN,
                COLUMN_DETAIL,
                COLUMN_DATE_INSERT
        };

        //COLUMN HACK NO . AS WE DEFINE COLUMN HACK ABOVE
        public static final int COL_ID = 0;
        public static final int COL_LEAVE_FROM = 1;
        public static final int COL_GO_TO = 2;
        public static final int COL_FLiGHT_NO = 3;
        public static final int COL_CLASS = 4;
        public static final int COL_PRICE = 5;
        public static final int COL_DEPART = 6;
        public static final int COL_ARRIVE = 7;
        public static final int COL_LEAVE_RETURN = 8;
        public static final int COL_DETAIL = 9;
        public static final int COL_DATE_INSERT = 10;

        public static Uri buildHistoryUri(long _id) {
            return ContentUris.withAppendedId(CONTENT_URI, _id);
        }

        public static Uri buildLeaveFromUri(String leaveFom, String goTO) {
            return CONTENT_URI.buildUpon().appendPath(leaveFom).appendPath(goTO).build();
        }
    }
}
