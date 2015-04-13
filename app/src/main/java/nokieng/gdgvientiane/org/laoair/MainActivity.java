package nokieng.gdgvientiane.org.laoair;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.viewpagerindicator.TabPageIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Vector;
import java.util.concurrent.TimeUnit;

import nokieng.gdgvientiane.org.laoair.Helper.AdapterPager;
import nokieng.gdgvientiane.org.laoair.Helper.Utilities;
import nokieng.gdgvientiane.org.laoair.data.KContact;


public class MainActivity extends ActionBarActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private ViewPager pager;
    private TabPageIndicator indicator;
    private AdapterPager adapter;
    private Utilities utilities;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        pager = (ViewPager) findViewById(R.id.pager);
        adapter = new AdapterPager(getSupportFragmentManager());
        pager.setAdapter(adapter);
        pager.setCurrentItem(0);


        indicator = (TabPageIndicator) findViewById(R.id.titlePageIndicator);
        indicator.setViewPager(pager);

        pager.setOnPageChangeListener(indicator);

        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        utilities = new Utilities(MainActivity.this);
        Cursor cursor = getContentResolver().query(
                KContact.CurrencyRate.CONTENT_URI,
                KContact.CurrencyRate.columnHacks,
                null,
                null,
                null
        );
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            String lastUpdateDate = cursor.getString(KContact.CurrencyRate.COL_DATE);
            if (utilities.isDateValid(lastUpdateDate)) {
                long lnLastUpdate = utilities.convert_yyyy_MM_dd_ToMillis(lastUpdateDate);
                long drift = utilities.getCurrentDateInMillis() - lnLastUpdate;
//                if (TimeUnit.MILLISECONDS.toHours(drift) >= 12) {
                if (TimeUnit.MILLISECONDS.toHours(drift) != -1) {
                    aqSyncRate();
                } else {
                    Log.d(TAG, "CURRENCY IS UP TO DATE");
                }
            } else {
                Toast.makeText(MainActivity.this, "Check Last update: Invalid date + " + lastUpdateDate, Toast.LENGTH_SHORT).show();
            }
        } else {
            aqSyncRate();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    //the rate will be update every time start activity, cuz the rate would be refresh every time .. :D
    private void aqSyncRate() {
        AQuery aq = new AQuery(MainActivity.this);
        String strUrl = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.xchange%20where%20pair%20in%20(%22USDEUR%22%2C%20%22USDCNY%22%2C%20%22USDTHB%22%2C%20%22USDLAK%22)&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys";
        AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {
                if (json != null) {
//                      mListRate = new ArrayList<>();
//                       mListRate.clear();
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
                            String strDate = paramsDate[2] + "-" + paramsDate[0] + "-" + paramsDate[1];
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
                            getContentResolver().delete(KContact.CurrencyRate.CONTENT_URI,
                                    null, null);
                            getContentResolver().bulkInsert(KContact.CurrencyRate.CONTENT_URI, cvArray);

                            //see what i store in bulkInsert
                            Uri currencyUri = KContact.CurrencyRate.CONTENT_URI;
                            Log.d(TAG, "Uri : " + currencyUri);
                            Cursor cursor = getContentResolver().query(currencyUri,
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
                } else {
                    status.invalidate();
                    Log.d(TAG, "error code: " + status.getCode() + " .Message: " + status.getMessage());
                }
            }
        };

        aq.ajax(strUrl, JSONObject.class, cb);
    }
}
