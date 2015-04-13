package nokieng.gdgvientiane.org.laoair.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by kieng on 4/6/2015.
 */
public class CurrencyRateTable {

    private static final String TAG = CurrencyRateTable.class.getSimpleName();

    private Context context;

    public CurrencyRateTable(Context context) {
        this.context = context;
    }

    public static final String CREATE_LAO_AIR_TABLE = "CREATE TABLE IF NOT EXISTS "
            + KContact.CurrencyRate.TABLE_NAME
            + " ( "
            + KContact.CurrencyRate._ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
            + KContact.CurrencyRate.COLUMN_NAME
            + " TEXT NOT NULL, "
            + KContact.CurrencyRate.COLUMN_RATE
            + " REAL NOT NULL, "
            + KContact.CurrencyRate.COLUMN_DATE
            + " REAL NOT NULL "
            + " );";

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_LAO_AIR_TABLE);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + KContact.CurrencyRate.TABLE_NAME);
        onCreate(db);
    }
}
