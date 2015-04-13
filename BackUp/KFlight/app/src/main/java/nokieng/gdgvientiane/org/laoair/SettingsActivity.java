package nokieng.gdgvientiane.org.laoair;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Vector;

import nokieng.gdgvientiane.org.laoair.data.KContact;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends PreferenceActivity {
    /**
     * Determines whether to always show the simplified settings UI, where
     * settings are presented in a single list. When false, settings are shown
     * as a master/detail two-pane view on tablets. When true, a single pane is
     * shown on tablets.
     */
    private static final String TAG = SettingsActivity.class.getSimpleName();

    private Toolbar toolbar;
//    private ArrayList<HashMap<String, String>> mListRate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        aqSyncRate();
    }

    private void aqSyncRate() {
        AQuery aq = new AQuery(SettingsActivity.this);
        String strUrl = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.xchange%20where%20pair%20in%20(%22USDEUR%22%2C%20%22USDCNY%22%2C%20%22USDTHB%22%2C%20%22USDLAK%22)&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys";
        AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {
//                Log.d(TAG, "Currency : " + json);
                if (json != null) {
//                    mListRate = new ArrayList<>();
//                    mListRate.clear();
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
                            String strName = objRate.getString("Name");
                            String strRate = objRate.getString("Rate");
                            String[] paramsDate = objRate.getString("Date").split("/");
                            String strDate = paramsDate[2] + "-" + paramsDate[1] + "-" + paramsDate[0];
//                            item.put("id", strID);
                            item.put(KContact.CurrencyRate.COLUMN_NAME, strName);
                            item.put(KContact.CurrencyRate.COLUMN_RATE, strRate);
                            item.put(KContact.CurrencyRate.COLUMN_DATE, strDate);
//                            mListRate.add(item);
                            cVVector.add(item);
                        }
                        if (cVVector.size() > 0) {
                            ContentValues[] cvArray = new ContentValues[cVVector.size()];
                            cVVector.toArray(cvArray);
                            getContentResolver().bulkInsert(KContact.CurrencyRate.CONTENT_URI, cvArray);

                            //see what i store in bulkInsert
                            Uri currencyUri = KContact.CurrencyRate.buildCurrencyRateUri(1);
                            Cursor cursor = getContentResolver().query(currencyUri,
                                    null, null, null, null);
                            cVVector = new Vector<>(cursor.getColumnCount());
                            if (cursor.moveToFirst()) {
                                do {
                                    ContentValues cv = new ContentValues();
                                    DatabaseUtils.cursorRowToContentValues(cursor, cv);
                                    cVVector.add(cv);
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

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

            LinearLayout root = (LinearLayout) findViewById(android.R.id.list).getParent().getParent().getParent();
            toolbar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.toolbar_setting, root, false);
            root.addView(toolbar, 0);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent upIntent = NavUtils.getParentActivityIntent(SettingsActivity.this);
                    if (NavUtils.shouldUpRecreateTask(SettingsActivity.this, upIntent)) {
                        TaskStackBuilder.create(SettingsActivity.this)
                                .addNextIntentWithParentStack(upIntent)
                                .startActivities();
                    } else
                        NavUtils.navigateUpFromSameTask(SettingsActivity.this);
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Intent upIntent = NavUtils.getParentActivityIntent(SettingsActivity.this);
            if (NavUtils.shouldUpRecreateTask(SettingsActivity.this, upIntent)) {
                TaskStackBuilder.create(SettingsActivity.this)
                        .addNextIntentWithParentStack(upIntent)
                        .startActivities();
            } else
                NavUtils.navigateUpFromSameTask(SettingsActivity.this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        setupSimplePreferencesScreen();
    }

    /**
     * Shows the simplified settings UI if the device configuration if the
     * device configuration dictates that a simplified, single-pane UI should be
     * shown.
     */
    private void setupSimplePreferencesScreen() {
        if (!isSimplePreferences(this)) {
            return;
        }

        // In the simplified UI, fragments are not used at all and we instead
        // use the older PreferenceActivity APIs.

        // Add 'general' preferences.
//        addPreferencesFromResource(R.xml.pref_general);

        // Add 'notifications' preferences, and a corresponding header.
        PreferenceCategory fakeHeader = new PreferenceCategory(this);
        // Add 'data and sync' preferences, and a corresponding header.
        fakeHeader = new PreferenceCategory(this);
        fakeHeader.setTitle(R.string.pref_header_currency);
        addPreferencesFromResource(R.xml.pref_data_sync);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this) && !isSimplePreferences(this);
    }

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * Determines whether the simplified settings UI should be shown. This is
     * true if this is forced via {@link #}, or the device
     * doesn't have newer APIs like {@link PreferenceFragment}, or the device
     * doesn't have an extra-large screen. In these cases, a single-pane
     * "simplified" settings UI should be shown.
     */
    private static boolean isSimplePreferences(Context context) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB
                || !isXLargeTablet(context);
    }

    /**
     * {@inheritDoc}
     */
/*    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        if (!isSimplePreferences(this)) {
            loadHeadersFromResource(R.xml.pref_headers, target);
        }
    }*/

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }


    /**
     * This fragment shows data and sync preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class DataSyncPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_data_sync);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("sync_frequency"));
        }
    }
}
