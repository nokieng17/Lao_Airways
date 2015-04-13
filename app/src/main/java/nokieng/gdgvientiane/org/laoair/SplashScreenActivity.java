package nokieng.gdgvientiane.org.laoair;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Vector;

import nokieng.gdgvientiane.org.laoair.Helper.Utilities;
import nokieng.gdgvientiane.org.laoair.data.KContact;


public class SplashScreenActivity extends ActionBarActivity {

    private static final String TAG = SplashScreenActivity.class.getSimpleName();


    Runnable runnable;
    Handler handler;

    Long delay_time;
    Long time = 200l;

    boolean isFirstUse = true;
    private Utilities utilities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.splash_screen_container, PlaceholderFragment.newInstance())
                    .commit();
        }

        utilities = new Utilities(SplashScreenActivity.this);
        isFirstUse = utilities.isFirstUse();

        if (!isFirstUse)
            time = 1000l;

        Log.d(TAG, "isFirstUse :" + isFirstUse);

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                if (!isFirstUse) {
                    intent();
                } else {
                    Log.d(TAG, "First Used :");
                    handler.removeCallbacks(runnable);
                    aqSyncRate();
                }
            }
        };


  /*      Uri uriDel = Uri.parse(String.valueOf(KContact.CurrencyRate.CONTENT_URI));
        int _idDel = getContentResolver().delete(uriDel, KContact.CurrencyRate.COLUMN_NAME + " IS NOT NULL", null);
        Log.d(TAG, "_idDel :" + _idDel);

        ContentValues valuesInsert = new ContentValues();
        valuesInsert.put(KContact.CurrencyRate.COLUMN_NAME, "USDEUR");
        valuesInsert.put(KContact.CurrencyRate.COLUMN_RATE, 8000);
        valuesInsert.put(KContact.CurrencyRate.COLUMN_DATE, 20150425);

        Uri uri = Uri.parse(String.valueOf(KContact.CurrencyRate.CONTENT_URI));
        Uri uriInsert = getContentResolver().insert(uri, valuesInsert);
        Log.d(TAG, "uriInsert :" + uriInsert);


        Uri uriQueryInsert = Uri.parse(String.valueOf(KContact.CurrencyRate.CONTENT_URI + "/USDEUR"));
        Cursor cursorInsert = getContentResolver().query(uriQueryInsert, null, null, null, null);
        Log.d(TAG, "Cursor :" + cursorInsert);
        if (cursorInsert != null) {
            cursorInsert.moveToFirst();
            Log.d(TAG, "get Cursor Insert:" + cursorInsert.getColumnIndex(KContact.CurrencyRate.COLUMN_NAME) +
                    " : " + KContact.CurrencyRate.COLUMN_RATE +
                    " : " + KContact.CurrencyRate.COLUMN_DATE);
            Log.d(TAG, "Cursor Insert :" + cursorInsert.moveToLast() + cursorInsert.getCount());
        }


        *//*ContentValues[] values = new ContentValues[]{};
        values[0].put(KContact.CurrencyRate.COLUMN_NAME, "USDLAK");
        values[1].put(KContact.CurrencyRate.COLUMN_RATE, 8000);
        values[2].put(KContact.CurrencyRate.COLUMN_DATE, 20150425);


        Uri uri1Bulk = Uri.parse(String.valueOf(KContact.CurrencyRate.CONTENT_URI));
        int _idInsert = getContentResolver().bulkInsert(uri1Bulk, values);
*//**//*        Uri uriUpdate = Uri.parse(String.valueOf(KContact.CurrencyRate.CONTENT_URI + "/" + KContact.CurrencyRate.COLUMN_NAME + "/USDLAK"));
        int _id = getContentResolver().update(uriUpdate, values, null, null);
        Log.d(TAG, uriUpdate.toString() + " _id :" + _id);*//**//*
        Log.d(TAG, "BulkInsert :" + _idInsert);*//**/
/*
        Uri uriQuery = Uri.parse(String.valueOf(KContact.CurrencyRate.CONTENT_URI + "/USDLAK"));
        Cursor cursor = getContentResolver().query(uriQuery, null, null, null, null);
        Log.d(TAG, "Cursor :" + cursor);
        if (cursor != null) {
            cursor.moveToFirst();
            Log.d(TAG, "get Cursor BUlkInsert :" + cursor.getColumnIndex(KContact.CurrencyRate.COLUMN_NAME) +
                    " : " + KContact.CurrencyRate.COLUMN_RATE +
                    " : " + KContact.CurrencyRate.COLUMN_DATE);
        }*/
    }


    @Override
    public void onResume() {
        super.onResume();

        delay_time = time;
        handler.postDelayed(runnable, delay_time);
        time = System.currentTimeMillis();
    }

    @Override
    public void onPause() {
        super.onPause();

        handler.removeCallbacks(runnable);
        time = delay_time - (System.currentTimeMillis() - time);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        private TextView txtVersion;

        public PlaceholderFragment() {
        }

        public static PlaceholderFragment newInstance() {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle arg = new Bundle();
            fragment.setArguments(arg);
            return fragment;
        }

        String strVersion = "INVALID";

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            getActivity().getWindow().getDecorView().setSystemUiVisibility(View.INVISIBLE);
            View rootView = inflater.inflate(R.layout.fragment_splash_screen, container, false);

            txtVersion = (TextView) rootView.findViewById(R.id.txt_splash_vesion);

            Utilities utilities = new Utilities(getActivity().getApplicationContext());
            strVersion = utilities.getVersion();

            txtVersion.setText(strVersion);

            return rootView;
        }

    }

    private void intent() {
        Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
        utilities.setUsed();
    }

    //the rate will be update every time start activity, cuz the rate would be refresh every time .. :D
    private void aqSyncRate() {
        AQuery aq = new AQuery(SplashScreenActivity.this);
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
                            intent();
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
