package nokieng.gdgvientiane.org.laoair.SyncData;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

import nokieng.gdgvientiane.org.laoair.Helper.Utilities;
import nokieng.gdgvientiane.org.laoair.R;
import nokieng.gdgvientiane.org.laoair.data.KContact;

public class CurrencySyncAdapter extends AbstractThreadedSyncAdapter {
    public final String TAG = CurrencySyncAdapter.class.getSimpleName();
    // Interval at which to sync with the weather, in seconds.
    // 60 seconds (1 minute) * 360 = 6 hours
    public static final int SYNC_INTERVAL = 60 * 360;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL / 3;
    private static final long DAY_IN_MILLIS = 1000 * 60 * 60 * 24;
    private static final int CURRENCY_NOTIFICATION_ID = 215;

    private Utilities utilities;

    private static String[] CURRENCY_PROJECTION = new String[0];

    public CurrencySyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        this.utilities = new Utilities(context);
        CURRENCY_PROJECTION = KContact.CurrencyRate.columnHacks;
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d(TAG, "Starting sync");

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String strCurrencyJson = null;

        String format = "json";
        String units = "metric";
        int numDays = 14;

        try {
            // Construct the URL for the OpenWeatherMap query
            // Possible parameters are avaiable at OWM's forecast API page, at
            // http://openweathermap.org/API#forecast
            final String BASE_URL =
                    "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.xchange%20where%20pair%20in%20(%22USDEUR%22%2C%20%22USDCNY%22%2C%20%22USDTHB%22%2C%20%22USDLAK%22)&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys";

            Uri builtUri = Uri.parse(BASE_URL).buildUpon().build();

            URL url = new URL(builtUri.toString());

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return;
            }
            strCurrencyJson = buffer.toString();
            getCurrencyDateFromJson(strCurrencyJson);
        } catch (IOException e) {
            Log.e(TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attempting
            // to parse it.
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(TAG, "Error closing stream", e);
                }
            }
        }
        return;
    }

    private void getCurrencyDateFromJson(String strJson) throws JSONException {
        if (strJson != null) {
            JSONObject json = new JSONObject(strJson);
            try {
                JSONObject objQuery = json.getJSONObject("query");
                JSONObject objResult = objQuery.getJSONObject("results");
                JSONArray arrRate = objResult.getJSONArray("rate");

                //this Vector is use for bulkInsert
                Vector<ContentValues> cVVector = new Vector<>(arrRate.length());
                for (int i = 0; i < arrRate.length(); i++) {
                    ContentValues item = new ContentValues();
                    JSONObject objRate = arrRate.getJSONObject(i);
                    String strID = objRate.getString("id");
                    String strName = objRate.getString("Name").replace("/", "");
                    String strRate = objRate.getString("Rate");
                    String[] paramsDate = objRate.getString("Date").split("/"); //we got
                    String strDate = paramsDate[2] + "-" + paramsDate[1] + "-" + paramsDate[0];
//                            item.put("id", strID);
                    item.put(KContact.CurrencyRate.COLUMN_NAME, strName);
                    item.put(KContact.CurrencyRate.COLUMN_RATE, strRate);
                    item.put(KContact.CurrencyRate.COLUMN_DATE, strDate);
//                            mListRate.add(item);
                    cVVector.add(item);
                }
                Log.d(TAG, "cVVector :" + cVVector.toString());
                if (cVVector.size() > 0) {
                    ContentValues[] cvArray = new ContentValues[cVVector.size()];
                    cVVector.toArray(cvArray);
                    getContext().getContentResolver().delete(KContact.CurrencyRate.CONTENT_URI,
                            null, null);
                    getContext().getContentResolver().bulkInsert(KContact.CurrencyRate.CONTENT_URI, cvArray);

                    //see what i store in bulkInsert
                    Uri currencyUri = KContact.CurrencyRate.CONTENT_URI;
                    Log.d(TAG, "Uri : " + currencyUri);
                    Cursor cursor = getContext().getContentResolver().query(currencyUri,
                            null, KContact.CurrencyRate.COLUMN_NAME + " = USDLAK", null, null);
                    Log.d(TAG, " TEST  Bulk insert Cursor :" + cursor.moveToFirst() + cursor.getCount() + " : " + cursor.getString(KContact.CurrencyRate.COL_NAME));
                    cVVector = new Vector<>(cursor.getColumnCount());
                    if (cursor.moveToFirst()) {
                        do {
                            ContentValues cv = new ContentValues();
                            DatabaseUtils.cursorRowToContentValues(cursor, cv);
                            cVVector.add(cv);
                            if (cursor.isLast()) cursor.close();
                        } while (cursor.moveToNext());
                    }
                    Log.d(TAG, "Vector ContentValues :" + cVVector.toString());
                }
//                        Log.d(TAG, "Rate :" + mListRate.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    /**
     * Helper method to have the sync adapter sync immediately
     *
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if (null == accountManager.getPassword(newAccount)) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        CurrencySyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }
}