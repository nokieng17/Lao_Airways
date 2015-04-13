package nokieng.gdgvientiane.org.laoair;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class SplashScreen extends ActionBarActivity {


    Runnable runnable;
    Handler handler;

    Long delay_time;
    Long time = 1500l;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.all_flight_container, new PlaceholderFragment())
                    .commit();
        }


        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        };

        //test database
/*        LaoAirDBHelper dbHelper = new LaoAirDBHelper(SplashScreen.this);
        SQLiteDatabase db;

        //here i test insert and select from database
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KContact.CurrencyRate.COLUMN_NAME, "USD-LAK");
        values.put(KContact.CurrencyRate.COLUMN_RATE, 8000);
        values.put(KContact.CurrencyRate.COLUMN_DATE, 20150505);

        db.insert(KContact.CurrencyRate.TABLE_NAME, null, values);
        db.close();
        if (!db.isOpen()) {
            db.isOpen();
        }
        db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(KContact.CurrencyRate.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null);
        cursor.moveToFirst();
        Log.d("SplashScreen", "Cursor.getColumnName" + cursor.getString(1));*/

    }


    @Override
    protected void onResume() {
        super.onResume();

        delay_time = time;
        handler.postDelayed(runnable, delay_time);
        time = System.currentTimeMillis();

    }

    @Override
    protected void onPause() {
        super.onPause();

        handler.removeCallbacks(runnable);
        time = delay_time - (System.currentTimeMillis() - time);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {


        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_splash_screen, container, false);


            return rootView;
        }

    }
}
