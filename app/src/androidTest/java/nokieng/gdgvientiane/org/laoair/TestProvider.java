package nokieng.gdgvientiane.org.laoair;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.test.AndroidTestCase;

import nokieng.gdgvientiane.org.laoair.data.KContact;
import nokieng.gdgvientiane.org.laoair.data.LaoAirDBHelper;

/**
 * Created by kieng on 4/8/2015.
 */
public class TestProvider extends AndroidTestCase {

    public TestProvider() {
        super();
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        deleteAllRecord();
        deleteAllFromDB();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void deleteAllRecord() {
        mContext.getContentResolver().delete(
                KContact.CurrencyRate.CONTENT_URI,
                null,
                null
        );
        Cursor cursorCurrency = getContext().getContentResolver().query(
                KContact.CurrencyRate.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error deleting all record", 0, cursorCurrency.getCount());

        mContext.getContentResolver().delete(
                KContact.History.CONTENT_URI,
                null,
                null
        );
        Cursor cursorHistory = getContext().getContentResolver().query(
                KContact.History.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        assertEquals("Error deleting all record", 0, cursorHistory.getCount());
        cursorCurrency.close();
    }

    public void deleteAllFromDB() {
        LaoAirDBHelper mOProvider = new LaoAirDBHelper(mContext);
        SQLiteDatabase db = mOProvider.getWritableDatabase();
        db.delete(KContact.CurrencyRate.TABLE_NAME, null, null);
        db.delete(KContact.History.TABLE_NAME, null, null);

    }

    public void testGetType() {
        String typeCurrency = mContext.getContentResolver().getType(KContact.CurrencyRate.CONTENT_URI);
        assertEquals("Error Type should return KContact.CurrencyRate.CONTENT_URI", KContact.CurrencyRate.CONTENT_TYPE, typeCurrency);

        String typeHistory = mContext.getContentResolver().getType(KContact.History.CONTENT_URI);
        assertEquals("Error get Type should return KContact.History.CONTENT_URI", KContact.History.CONTENT_TYPE, typeHistory);

        String currentName = "USDEUR";
        typeCurrency = mContext.getContentResolver().getType(KContact.CurrencyRate.buildCurrencyNameUri(currentName));
        assertEquals("Error: the currency_rate CONTENT_URI should return KContact.CurrencyRate.CONTENT_TYPE",
                KContact.CurrencyRate.CONTENT_ITEM_TYPE, typeCurrency);

        String leaveFrom = "VTE";
        String goTo = "PKZ";
        typeHistory = mContext.getContentResolver().getType(KContact.History.buildLeaveFromUri(leaveFrom, goTo));
        assertEquals("Error: the History URI should return KContact.History.CONTENT_ITEM_TYPE", KContact.History.CONTENT_ITEM_TYPE, typeHistory);
    }

    public void testBasicCurrencyQuery() {
        LaoAirDBHelper dbHelper = new LaoAirDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = TestUtilities.createCurrency();
        long currencyID = TestUtilities.insertCurrency(mContext);

        Cursor currencyCursor = mContext.getContentResolver().query(
                KContact.CurrencyRate.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor("testBasicWeatherQuery", currencyCursor, testValues);

        ContentValues historyValues = TestUtilities.createHistory();
        long historyID = TestUtilities.insertHistory(mContext);

        Cursor historyCursor = mContext.getContentResolver().query(
                KContact.History.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        TestUtilities.validateCursor("Error : Test History Cursor", historyCursor, historyValues);
    }


    public void testCurrencyUpdate() {
        ContentValues currencyValues = TestUtilities.createCurrency();
        Uri currencyUri = mContext.getContentResolver().insert(
                KContact.CurrencyRate.CONTENT_URI, currencyValues
        );
        long longCurrencyID = ContentUris.parseId(currencyUri);
        // Verify we got a row back.
        assertTrue(longCurrencyID != -1);

        ContentValues updatedValues = new ContentValues(currencyValues);
        updatedValues.put(KContact.CurrencyRate._ID, longCurrencyID);
        updatedValues.put(KContact.CurrencyRate.COLUMN_NAME, "USDLAK");

        Cursor currencyCursor1 = mContext.getContentResolver().query(KContact.CurrencyRate.CONTENT_URI, null, null, null, null);

        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        currencyCursor1.registerContentObserver(tco);

        int count = mContext.getContentResolver().update(
                KContact.CurrencyRate.CONTENT_URI, updatedValues, KContact.CurrencyRate._ID + "= ?",
                new String[]{Long.toString(longCurrencyID)});
        assertEquals(count, 1);

        // Test to make sure our observer is called.  If not, we throw an assertion.
        //
        // Students: If your code is failing here, it means that your content provider
        // isn't calling getContext().getContentResolver().notifyChange(currencyUri, null);
        tco.waitForNotificationOrFail();

        currencyCursor1.unregisterContentObserver(tco);
        currencyCursor1.close();

        // A currencyCursor2 is your primary interface to the query results.
        Cursor currencyCursor2 = mContext.getContentResolver().query(
                KContact.CurrencyRate.CONTENT_URI,
                null,   // projection
                KContact.CurrencyRate._ID + " = " + longCurrencyID,
                null,   // Values for the "where" clause
                null    // sort order
        );

        TestUtilities.validateCursor("testUpdateLocation.  Error validating currency_rate entry update.",
                currencyCursor2, updatedValues);

        currencyCursor2.close();

        //test History
        ContentValues historyValues = TestUtilities.createHistory();
        Uri HistoryUri = mContext.getContentResolver().insert(
                KContact.History.CONTENT_URI, historyValues
        );
        long longHistoryID = ContentUris.parseId(HistoryUri);
        // Verify we got a row back.
        assertTrue(longHistoryID != -1);

        ContentValues updateHistoryValues = new ContentValues(historyValues);
        updateHistoryValues.put(KContact.History._ID, longHistoryID);
        updateHistoryValues.put(KContact.History.COLUMN_LEAVE_FROM, "KMG");

        Cursor historyCursor1 = mContext.getContentResolver().query(KContact.History.CONTENT_URI, null, null, null, null);

        TestUtilities.TestContentObserver historyTco = TestUtilities.getTestContentObserver();
        historyCursor1.registerContentObserver(historyTco);

        int historyCount = mContext.getContentResolver().update(
                KContact.History.CONTENT_URI, updateHistoryValues, KContact.History._ID + "= ?",
                new String[]{Long.toString(longHistoryID)});
        assertEquals(historyCount, 1);

        // Test to make sure our observer is called.  If not, we throw an assertion.
        //
        // Students: If your code is failing here, it means that your content provider
        // isn't calling getContext().getContentResolver().notifyChange(currencyUri, null);
        historyTco.waitForNotificationOrFail();

        historyCursor1.unregisterContentObserver(historyTco);
        historyCursor1.close();

        // A historyCursor2 is your primary interface to the query results.
        Cursor historyCursor2 = mContext.getContentResolver().query(
                KContact.History.CONTENT_URI,
                null,   // projection
                KContact.History._ID + " = " + longHistoryID,
                null,   // Values for the "where" clause
                null    // sort order
        );

        TestUtilities.validateCursor("testUpdateLocation.  Error validating currency_rate entry update.",
                historyCursor2, updateHistoryValues);

        historyCursor2.close();
    }

    public void testInsertCurrency() {
        ContentValues testValues = TestUtilities.createCurrency();
        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(KContact.CurrencyRate.CONTENT_URI, true, tco);

        Uri currencyUri = mContext.getContentResolver().insert(KContact.CurrencyRate.CONTENT_URI, testValues);

        // Did our content observer get called?  Students:  If this fails, your insert location
        // isn't calling getContext().getContentResolver().notifyChange(uri, null);
        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        long locationRowId = ContentUris.parseId(currencyUri);

        //        // Verify we got a row back.
        assertTrue(locationRowId != -1);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                KContact.CurrencyRate.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        TestUtilities.validateCursor("testInsertCurrency. Error validating currency entries.",
                cursor, testValues);

        //now here end test currency perfect

        //test History
        ContentValues testHistoryValues = TestUtilities.createHistory();
        TestUtilities.TestContentObserver historyTco = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(KContact.History.CONTENT_URI, true, historyTco);

        Uri historyUri = mContext.getContentResolver().insert(KContact.History.CONTENT_URI, testHistoryValues);

        // Did our content observer get called?  Students:  If this fails, your insert location
        // isn't calling getContext().getContentResolver().notifyChange(uri, null);
        historyTco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(historyTco);

        long historyID = ContentUris.parseId(historyUri);

        //        // Verify we got a row back.
        assertTrue(historyID != -1);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // A cursor is your primary interface to the query results.
        Cursor historyCursor = mContext.getContentResolver().query(
                KContact.History.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        TestUtilities.validateCursor("test insert history. Error validating history entries.",
                historyCursor, testHistoryValues);
    }

    public void testCurrencyDelete() {
        testInsertCurrency();

        TestUtilities.TestContentObserver currencyObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(KContact.CurrencyRate.CONTENT_URI, true, currencyObserver);
        //test history
        TestUtilities.TestContentObserver historyObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(KContact.History.CONTENT_URI, true, historyObserver);


        deleteAllRecord();

        // Students: If either of these fail, you most-likely are not calling the
//        // getContext().getContentResolver().notifyChange(uri, null); in the ContentProvider
//        // delete.  (only if the insertReadProvider is succeeding)
        currencyObserver.waitForNotificationOrFail();

        historyObserver.waitForNotificationOrFail();

        mContext.getContentResolver().unregisterContentObserver(currencyObserver);

        mContext.getContentResolver().unregisterContentObserver(historyObserver);

    }

    static private final int BULK_INSERT_RECORDS_TO_INSERT = 10;

    static ContentValues[] createBulkInsertWeatherValues() {
        long currentTestDate = TestUtilities.TEST_DATE;
        long millisecondsInADay = 1000 * 60 * 60 * 24;
        ContentValues[] returnContentValues = new ContentValues[BULK_INSERT_RECORDS_TO_INSERT];

        for (int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++, currentTestDate += millisecondsInADay) {
            ContentValues values = new ContentValues();
            values.put(KContact.CurrencyRate.COLUMN_NAME, "USDTHB");
            values.put(KContact.CurrencyRate.COLUMN_RATE, "9000");
            values.put(KContact.CurrencyRate.COLUMN_DATE, "20150505");
            returnContentValues[i] = values;
        }
        return returnContentValues;
    }

    static ContentValues[] createBulkInsertHistory() {
        long currentTestDate = TestUtilities.TEST_DATE;
        long millisecondInDay = 1000 * 60 * 60 * 24;
        ContentValues[] returnContentValues = new ContentValues[BULK_INSERT_RECORDS_TO_INSERT];
        for (int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++, currentTestDate += millisecondInDay) {
            ContentValues value = TestUtilities.createHistory();
            returnContentValues[i] = value;
        }
        return returnContentValues;
    }

    public void bulkInsert() {
        // first, let's create a location value
        ContentValues testValues = TestUtilities.createCurrency();
        Uri locationUri = mContext.getContentResolver().insert(KContact.CurrencyRate.CONTENT_URI, testValues);
        long locationRowId = ContentUris.parseId(locationUri);

        // Verify we got a row back.
        assertTrue(locationRowId != -1);
        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                KContact.CurrencyRate.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        TestUtilities.validateCursor("testBulkInsert. Error validating currency entries.",
                cursor, testValues);


        // Now we can bulkInsert some weather.  In fact, we only implement BulkInsert for weather
        // entries.  With ContentProviders, you really only have to implement the features you
        // use, after all.
        ContentValues[] bulkInsertContentValues = createBulkInsertWeatherValues();

        // Register a content observer for our bulk insert.
        TestUtilities.TestContentObserver Observer = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(KContact.CurrencyRate.CONTENT_URI, true, Observer);

        int insertCount = mContext.getContentResolver().bulkInsert(KContact.CurrencyRate.CONTENT_URI, bulkInsertContentValues);

        // Students:  If this fails, it means that you most-likely are not calling the
        // getContext().getContentResolver().notifyChange(uri, null); in your BulkInsert
        // ContentProvider method.
        Observer.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(Observer);

        assertEquals(insertCount, BULK_INSERT_RECORDS_TO_INSERT);

        // A cursor is your primary interface to the query results.
        cursor = mContext.getContentResolver().query(
                KContact.CurrencyRate.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                KContact.CurrencyRate.COLUMN_NAME + " ASC"  // sort order == by DATE ASCENDING
        );

        // we should have as many records in the database as we've inserted
        assertEquals(cursor.getCount(), BULK_INSERT_RECORDS_TO_INSERT);

        // and let's make sure they match the ones we created
        cursor.moveToFirst();
        for (int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++, cursor.moveToNext()) {
            TestUtilities.validateCurrentRecord("testBulkInsert.  Error validating currency_rate " + i,
                    cursor, bulkInsertContentValues[i]);
        }
        cursor.close();


        //bulkInsert History
        // first, let's create a location value
        ContentValues historyValues = TestUtilities.createCurrency();
        Uri historyUri = mContext.getContentResolver().insert(KContact.History.CONTENT_URI, historyValues);
        long historyRowID = ContentUris.parseId(historyUri);

        // Verify we got a row back.
        assertTrue(historyRowID != -1);
        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // A cursor is your primary interface to the query results.
        Cursor historyCursor = mContext.getContentResolver().query(
                KContact.History.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        TestUtilities.validateCursor("testBulkInsert. Error validating History entries.",
                historyCursor, historyValues);


        // Now we can bulkInsert some weather.  In fact, we only implement BulkInsert for weather
        // entries.  With ContentProviders, you really only have to implement the features you
        // use, after all.
        ContentValues[] bulkInsertHistoryContentValues = createBulkInsertHistory();

        // Register a content observer for our bulk insert.
        TestUtilities.TestContentObserver historyObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(KContact.History.CONTENT_URI, true, historyObserver);

        int insertHistoryCount = mContext.getContentResolver().bulkInsert(KContact.History.CONTENT_URI, bulkInsertHistoryContentValues);

        // Students:  If this fails, it means that you most-likely are not calling the
        // getContext().getContentResolver().notifyChange(uri, null); in your BulkInsert
        // ContentProvider method.
        historyObserver.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(historyObserver);

        assertEquals(insertHistoryCount, BULK_INSERT_RECORDS_TO_INSERT);

        // A cursor is your primary interface to the query results.
        historyCursor = mContext.getContentResolver().query(
                KContact.History.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                KContact.History.COLUMN_DEPART + " ASC"  // sort order == by DATE ASCENDING
        );

        // we should have as many records in the database as we've inserted
        assertEquals(historyCursor.getCount(), BULK_INSERT_RECORDS_TO_INSERT);

        // and let's make sure they match the ones we created
        historyCursor.moveToFirst();
        for (int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++, historyCursor.moveToNext()) {
            TestUtilities.validateCurrentRecord("testBulkInsert.  Error validating history entries " + i,
                    historyCursor, bulkInsertHistoryContentValues[i]);
        }
        historyCursor.close();
    }

    //in concept, you get all content provider .. :) NOKEING Good Job!
    //woh! hoo!
}
