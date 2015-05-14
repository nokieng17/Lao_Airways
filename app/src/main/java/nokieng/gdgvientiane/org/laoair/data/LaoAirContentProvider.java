package nokieng.gdgvientiane.org.laoair.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

/**
 * Created by kieng on 4/6/2015.
 */
public class LaoAirContentProvider extends ContentProvider {

    private static final String TAG = LaoAirContentProvider.class.getSimpleName();

    private UriMatcher sUriMatcher = buildUriMatcher();
    private static LaoAirDBHelper mOpenHelper;

    //no need since we do not have to query with two databases
//    private static final SQLiteQueryBuilder qCurrencyBuilder;

    static final int CURRENCY_RATE = 100;
    static final int CURRENCY_RATE_ITEM = 101;

    static final int HISTORY = 200;
    static final int HISTORY_ITEM = 201;
    static final int HISTORY_ITEM_WITH_ID = 202;

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = KContact.CONTENT_AUTHORITY;

        matcher.addURI(authority, KContact.PATH_CURRENCY_RATE, CURRENCY_RATE);
        matcher.addURI(authority, KContact.PATH_CURRENCY_RATE + "/*", CURRENCY_RATE_ITEM);
        matcher.addURI(authority, KContact.PATH_HISTORY, HISTORY);
        matcher.addURI(authority, KContact.PATH_HISTORY + "/#", HISTORY_ITEM_WITH_ID);
//        matcher.addURI(authority, KContact.PATH_HISTORY + "/*", HISTORY_ITEM);
        matcher.addURI(authority, KContact.PATH_HISTORY + "/*/*", HISTORY_ITEM);
        matcher.addURI(authority, KContact.PATH_HISTORY + "/*/*/*", HISTORY_ITEM);

        return matcher;
    }

/*    static {
        qCurrencyBuilder = new SQLiteQueryBuilder();
        qCurrencyBuilder.setTables(KContact.CurrencyRate.TABLE_NAME);
    }*/

    @Override
    public boolean onCreate() {
        mOpenHelper = new LaoAirDBHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Log.d(TAG, "Cursor Query Uri :" + uri);
        final SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor retCursor;
        String where = "";
        String table_name;
        switch (sUriMatcher.match(uri)) {
            case CURRENCY_RATE:
                table_name = KContact.CurrencyRate.TABLE_NAME;
                projection = KContact.CurrencyRate.columnHacks;
                break;
            case CURRENCY_RATE_ITEM:
                projection = KContact.CurrencyRate.columnHacks;
                table_name = KContact.CurrencyRate.TABLE_NAME;
                where = KContact.CurrencyRate.COLUMN_NAME + " = '" + uri.getLastPathSegment() + "'";
                break;
            case HISTORY:
                table_name = KContact.History.TABLE_NAME;
                projection = KContact.History.columnHacks;
                break;
            case HISTORY_ITEM:
                table_name = KContact.History.TABLE_NAME;
                where = KContact.History.COLUMN_LEAVE_FROM + " = '" + uri.getPathSegments().get(1) + "' AND "
                        + KContact.History.COLUMN_GO_TO + " = '" + uri.getPathSegments().get(2) + "' AND "
                        + KContact.History.COLUMN_DEPART + " = '" + uri.getPathSegments().get(3) + "'";
                projection = KContact.History.columnHacks;
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri " + uri);
        }
        if (!where.isEmpty())
            selection = where;
        else
            selection = "";
        Log.d(TAG, "SELECTION : " + selection);
        retCursor = db.query(
                table_name,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public String getType(Uri uri) {

        Log.d(TAG, "getType Uri : " + uri);
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case CURRENCY_RATE:
                return KContact.CurrencyRate.CONTENT_TYPE;
            case CURRENCY_RATE_ITEM:
                return KContact.CurrencyRate.CONTENT_ITEM_TYPE;
            case HISTORY:
                return KContact.History.CONTENT_TYPE;
            case HISTORY_ITEM:
                return KContact.History.CONTENT_ITEM_TYPE;
            case HISTORY_ITEM_WITH_ID:
                return KContact.History.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("getType Unknown Uri " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues initialValues) {

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        long _id = -1;
        ContentValues values;
        if (initialValues != null)
            values = initialValues;
        else
            values = new ContentValues();
        Uri returnUri;
        returnUri = null;
        switch (match) {
        /*    case CURRENCY_RATE_ITEM:
                _id = db.insertWithOnConflict(KContact.CurrencyRate.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                if (_id > 0)
                    returnUri = KContact.CurrencyRate.buildCurrencyRateUri(_id);
                else
                    throw new SQLException("Failed to inset with initialValues :" + values.toString());
                break;*/
            case CURRENCY_RATE: {
                _id = db.insertWithOnConflict(KContact.CurrencyRate.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                if (_id > 0)
                    returnUri = KContact.CurrencyRate.buildCurrencyRateUri(_id);
                else
                    throw new SQLException("Failed to insert row into " + uri);
                break;
            }
            case HISTORY:
                boolean isExists = false;
                HistoryTable historyTable = new HistoryTable(getContext());
                String leaveFrom = "";
                String goTo = "";
                String leaveDate = "";
                leaveFrom = values.get(KContact.History.COLUMN_LEAVE_FROM).toString();
                goTo = values.get(KContact.History.COLUMN_GO_TO).toString();
                leaveDate = values.get(KContact.History.COLUMN_DEPART).toString();

                if (!(leaveDate.equals("") && goTo.equals("") && leaveDate.equals(""))) {
                    isExists = historyTable.checkIfFlightExists(leaveFrom, goTo, leaveDate);
                }
                Log.d(TAG, "isExists :" + isExists);
                //check this case, maybe access one data for many time, so let it check before insert
                if (!isExists) {
                    //if not exist insert it
                    _id = db.insertWithOnConflict(KContact.History.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                } else {
                    String where = KContact.History.COLUMN_LEAVE_FROM + " = '" + leaveFrom +
                            "' AND " + KContact.History.COLUMN_GO_TO + " = '" + goTo +
                            "' AND " + KContact.History.COLUMN_DEPART + " = '" + leaveDate + "'";
                    Log.d(TAG, "Where clause :" + where);
                    _id = db.updateWithOnConflict(KContact.History.TABLE_NAME,
                            values, where, null, SQLiteDatabase.CONFLICT_REPLACE);
                }
                if (_id > 0)
                    returnUri = KContact.History.buildHistoryUri(_id);
                else
                    throw new SQLException("failed to insert to History : " + uri);
                break;
            default:
                throw new UnsupportedOperationException("Insert unknown Uri " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        Log.d(TAG, "delete Uri : " + uri);
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowDelete = 0;
        String tableName;
//        if (null == selection) selection = uri.getLastPathSegment();
        switch (match) {
            case CURRENCY_RATE: {
                tableName = KContact.CurrencyRate.TABLE_NAME;
                break;
            }
            case CURRENCY_RATE_ITEM:
                tableName = KContact.CurrencyRate.TABLE_NAME;
                selection = selection + KContact.CurrencyRate.COLUMN_NAME + " = " + uri.getLastPathSegment();
                break;
            case HISTORY:
                tableName = KContact.History.TABLE_NAME;
                break;
            case HISTORY_ITEM:
                tableName = KContact.History.TABLE_NAME;
                selection = selection + KContact.History.COLUMN_LEAVE_FROM + " = " + uri.getPathSegments().get(1) +
                        " AND " + KContact.History.COLUMN_GO_TO + " = " + uri.getPathSegments().get(2);
                break;
            case HISTORY_ITEM_WITH_ID:
                tableName = KContact.History.TABLE_NAME;
                selection = KContact.History._ID + " = '" + uri.getLastPathSegment() + "'";
                Log.d(TAG, "DELETE WITH ID  :" + uri.getLastPathSegment());
                break;
            default:
                throw new UnsupportedOperationException("Insert Unknown Uri " + uri);
        }
        Log.d(TAG, "delete with Uri :" + uri);
        rowDelete = db.delete(tableName, selection, selectionArgs);
        Log.d(TAG, "Delete rowDelete :" + rowDelete);
        if (rowDelete != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowDelete;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        Log.d(TAG, "update Uri : " + uri);
        Log.d(TAG, "update Values :" + values.toString());
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowUpdate;
        switch (match) {
            case CURRENCY_RATE: {
                Log.d(TAG, " case :" + CURRENCY_RATE);
                rowUpdate = db.update(KContact.CurrencyRate.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            case CURRENCY_RATE_ITEM: {
                Log.d(TAG, "case :" + CURRENCY_RATE_ITEM);
                String segment = uri.getLastPathSegment();
                String whereClause = KContact.CurrencyRate.COLUMN_NAME + " = " + segment +
                        (!TextUtils.isEmpty(selection) ? " AND ( " + selection + " ) " : "");

                Log.d(TAG, "selecttion :" + selection + "whereCase :" + whereClause);
                rowUpdate = db.update(KContact.CurrencyRate.TABLE_NAME, values, whereClause, selectionArgs);
                break;
            }
            case HISTORY:
                rowUpdate = db.update(KContact.History.TABLE_NAME, values, selection, selectionArgs);
                break;
            case HISTORY_ITEM:
                String where = KContact.History.COLUMN_LEAVE_FROM + " = '" + uri.getPathSegments().get(1) +
                        "' AND " + KContact.History.COLUMN_GO_TO + " = '" + uri.getPathSegments().get(2) +
                        "' AND " + KContact.History.COLUMN_DEPART + " = '" + uri.getPathSegments().get(3) + "'";
                rowUpdate = db.update(KContact.History.TABLE_NAME, values, where, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("upDate Unknown Uri " + uri);
        }
        if (rowUpdate != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowUpdate;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {

        Log.d(TAG, "BulkInsert Uri :" + uri);
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case CURRENCY_RATE: {
                Log.d(TAG, "CURRENCY RATE =>");
                db.beginTransaction();
                int returnCount = 0;
                try {
                    assert values != null;
                    for (ContentValues value : values) {
                        long _id = db.insert(KContact.CurrencyRate.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                            Log.d(TAG, "BulkInsert :" + returnCount);
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }
            case HISTORY:
                Log.d(TAG, "HISTORY BULK INSERT ==>");
                db.beginTransaction();
                int returnCount = 0;
                try {
                    assert values != null;
                    for (ContentValues value : values) {
                        long _id = db.insert(KContact.History.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                            Log.d(TAG, "BulkInsert Count :" + returnCount);
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
            default:
                return super.bulkInsert(uri, values);
        }
    }
}
