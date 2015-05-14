package nokieng.gdgvientiane.org.laoair.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

/**
 * Created by kieng on 4/10/2015.
 */
public class HistoryTable {
    private Context mContext;

    private static final String TAG = HistoryTable.class.getSimpleName();

    public HistoryTable(Context mContext) {
        this.mContext = mContext;
    }

    public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
            KContact.History.TABLE_NAME +
            " ( " +
            KContact.History._ID +
            " INTEGER PRIMARY KEY AUTOINCREMENT ," +
            KContact.History.COLUMN_LEAVE_FROM +
            " TEXT NOT NULL, " +
            KContact.History.COLUMN_GO_TO +
            " TEXT NOT NULL, " +
            KContact.History.COLUMN_FLIGHT_NO +
            " TEXT NOT NULL, " +
            KContact.History.COLUMN_CLASS +
            " TEXT NOT NULL, " +
            KContact.History.COLUMN_PRICE +
            " TEXT NOT NULL, " +
            KContact.History.COLUMN_DEPART +
            " TEXT NOT NULL, " +
            KContact.History.COLUMN_ARRIVE +
            " TEXT NOT NULL, " +
            KContact.History.COLUMN_LEAVE_RETURN +
            " TEXT NULL, " +
            KContact.History.COLUMN_DETAIL +
            " TEXT NOT NULL, " +
            KContact.History.COLUMN_DATE_INSERT +
            " TEXT NOT NULL " +
            " );";

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + KContact.History.TABLE_NAME);
            onCreate(db);
        }
    }

    public boolean checkIfFlightExists(String leaveFrom, String goTo, String leaveTime) {
        Log.d(TAG, "checkIfFlightExists : " + leaveFrom + goTo + leaveTime);
        Uri uri = Uri.parse(KContact.History.CONTENT_URI + "/" + leaveFrom + "/" + goTo + "/" + leaveTime);
       /* String where = KContact.History.COLUMN_LEAVE_FROM + " = " + leaveFrom +
                " AND " + KContact.History.COLUMN_GO_TO + " = " + goTo +
                " AND " + KContact.History.COLUMN_DEPART + " = " + leaveTime;*/
        Cursor cursor = mContext.getContentResolver().query(uri,
                KContact.History.columnHacks,
                null,
                null,
                null);
//        cursor.moveToFirst();
//        Log.d(TAG, "WHERE QUERY URI :" + uri);
//        Log.d(TAG, "Cursor isExists :" + cursor.getString(KContact.History.COL_FLiGHT_NO));
        return cursor.getCount() > 0;
    }
}