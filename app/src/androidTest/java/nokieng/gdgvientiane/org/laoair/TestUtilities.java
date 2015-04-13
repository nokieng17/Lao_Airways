package nokieng.gdgvientiane.org.laoair;

import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.test.AndroidTestCase;

import java.util.Map;
import java.util.Set;

import nokieng.gdgvientiane.org.laoair.data.KContact;
import nokieng.gdgvientiane.org.laoair.data.LaoAirDBHelper;
import nokieng.gdgvientiane.org.laoair.utils.PollingCheck;

/**
 * Created by kieng on 4/8/2015.
 */
public class TestUtilities extends AndroidTestCase {

    static final long TEST_DATE = 1419033600L;  // December 20th, 2014

    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }

    static ContentValues createCurrency() {
        ContentValues values = new ContentValues();
        values.put(KContact.CurrencyRate.COLUMN_NAME, "USDEUR");
        values.put(KContact.CurrencyRate.COLUMN_RATE, "8000");
        values.put(KContact.CurrencyRate.COLUMN_DATE, "20150425");
        return values;
    }


    static long insertCurrency(Context context) {
        LaoAirDBHelper dbHelper = new LaoAirDBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = TestUtilities.createCurrency();

        long _id;
        _id = db.insert(KContact.CurrencyRate.TABLE_NAME, null, testValues);
        assertTrue("Error: Failure to insert currency_rate Values", _id != -1);
        return _id;
    }

    static ContentValues createHistory() {
        ContentValues values = new ContentValues();
        values.put(KContact.History.COLUMN_LEAVE_FROM, "VTE");
        values.put(KContact.History.COLUMN_GO_TO, "PKZ");
        values.put(KContact.History.COLUMN_FLIGHT_NO, "QV 931");
        values.put(KContact.History.COLUMN_CLASS, "Economic (E)");
        values.put(KContact.History.COLUMN_PRICE, "200 US");
        values.put(KContact.History.COLUMN_DEPART, "12:00");
        values.put(KContact.History.COLUMN_ARRIVE, "13:00");
        values.put(KContact.History.COLUMN_LEAVE_RETURN, "Leave");
        values.put(KContact.History.COLUMN_DETAIL, "Detail");
        values.put(KContact.History.COLUMN_DATE_INSERT, "20150304");
        return values;
    }

    static long insertHistory(Context con) {
        LaoAirDBHelper dbHelper = new LaoAirDBHelper(con);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = TestUtilities.createHistory();
        long _id;
        _id = db.insert(KContact.History.TABLE_NAME, null, testValues);
        assertTrue("Error: Failed to insert into History ", _id != -1);
        return _id;
    }


    static class TestContentObserver extends ContentObserver {
        final HandlerThread mHT;
        boolean mContentChanged;

        static TestContentObserver getTestContentObserver() {
            HandlerThread ht = new HandlerThread("ContentObserverThread");
            ht.start();
            return new TestContentObserver(ht);
        }

        private TestContentObserver(HandlerThread ht) {
            super(new Handler(ht.getLooper()));
            mHT = ht;
        }

        // On earlier versions of Android, this onChange method is called
        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            mContentChanged = true;
        }

        public void waitForNotificationOrFail() {
            // Note: The PollingCheck class is taken from the Android CTS (Compatibility Test Suite).
            // It's useful to look at the Android CTS source for ideas on how to test your Android
            // applications.  The reason that PollingCheck works is that, by default, the JUnit
            // testing framework is not running on the main Android application thread.
            new PollingCheck(5000) {
                @Override
                protected boolean check() {
                    return mContentChanged;
                }
            }.run();
            mHT.quit();
        }
    }

    static TestContentObserver getTestContentObserver() {
        return TestContentObserver.getTestContentObserver();
    }

}
