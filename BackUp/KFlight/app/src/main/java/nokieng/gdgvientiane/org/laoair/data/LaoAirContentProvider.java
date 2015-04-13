package nokieng.gdgvientiane.org.laoair.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * Created by kieng on 4/6/2015.
 */
public class LaoAirContentProvider extends ContentProvider {

    private UriMatcher sUriMatcher = buildUriMatcher();
    private static LaoAirDBHelper mOpenHelper;
    private static final SQLiteQueryBuilder qCurrencyBuilder;

    static final int CURRENCY_RATE = 100;

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = KContact.CONTENT_AUTHORITY;

        matcher.addURI(authority, KContact.PATH_CURRENCY_RATE, CURRENCY_RATE);

        return matcher;
    }

    static {
        qCurrencyBuilder = new SQLiteQueryBuilder();
        qCurrencyBuilder.setTables(KContact.CurrencyRate.TABLE_NAME);
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new LaoAirDBHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case CURRENCY_RATE: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        KContact.CurrencyRate.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown Uri " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public String getType(Uri uri) {

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case CURRENCY_RATE:
                return KContact.CurrencyRate.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown Uri " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        Uri returnUri;
        switch (match) {
            case CURRENCY_RATE: {
                long _id = db.insert(KContact.CurrencyRate.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = KContact.CurrencyRate.buildCurrencyRateUri(_id);
                else
                    throw new SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("unknown Uri " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowDelete;
        if (null == selection) selection = uri.getLastPathSegment();
        switch (match) {
            case CURRENCY_RATE: {
                rowDelete = db.delete(KContact.CurrencyRate.TABLE_NAME, selection, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown Uri " + uri);
        }
        if (rowDelete != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowDelete;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowUpdate;
        switch (match) {
            case CURRENCY_RATE: {
                rowUpdate = db.update(KContact.CurrencyRate.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown Uri " + uri);
        }
        if (rowUpdate != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowUpdate;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case CURRENCY_RATE: {
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(KContact.CurrencyRate.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }
            default:
                return super.bulkInsert(uri, values);
        }
    }
}
