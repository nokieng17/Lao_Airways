package nokieng.gdgvientiane.org.laoair.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by kieng on 4/6/2015.
 */
public class LaoAirDBHelper extends SQLiteOpenHelper {

    private static final String TAG = LaoAirDBHelper.class.getSimpleName();

    private CurrencyRateTable rateTable;
    private Context mContext;

    private static final String DATABASE_NAME = "laoair.db";
    private static final int DATABASE_VERSION = 1;

    public LaoAirDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mContext = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        rateTable.onCreate(db);
        Log.d(TAG, "onCreate Database");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        rateTable.onUpgrade(db, oldVersion, newVersion);
        Log.d(TAG, "DROP TABLE " + DATABASE_NAME + " from " + oldVersion + " version to " + newVersion + " version");
    }
}
