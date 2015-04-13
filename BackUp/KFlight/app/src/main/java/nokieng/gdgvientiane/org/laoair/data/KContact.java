package nokieng.gdgvientiane.org.laoair.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by kieng on 4/6/2015.
 */
public class KContact {

    public static final String CONTENT_AUTHORITY = "nokieng.gdgvientiane.org.laoair";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_CURRENCY_RATE = "currency_rate";
    public static final String PATH_FLIGHT_DETAIL = "flight_detail";

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

        public static Uri buildCurrencyRateUri(long _id) {
            return ContentUris.withAppendedId(CONTENT_URI, _id);
        }

    }
}
