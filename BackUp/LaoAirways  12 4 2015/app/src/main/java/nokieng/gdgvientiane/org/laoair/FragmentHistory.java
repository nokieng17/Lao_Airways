package nokieng.gdgvientiane.org.laoair;


import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.concurrent.TimeUnit;

import nokieng.gdgvientiane.org.laoair.Helper.AdapterAllFlight;
import nokieng.gdgvientiane.org.laoair.Helper.CursorAdapterAllFlight;
import nokieng.gdgvientiane.org.laoair.Helper.Utilities;
import nokieng.gdgvientiane.org.laoair.data.KContact;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentHistory extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = FragmentHistory.class.getSimpleName();

    public FragmentHistory() {
        // Required empty public constructor
    }

    public static FragmentHistory newInstance() {
        FragmentHistory fragmentHistory = new FragmentHistory();
        Bundle args = new Bundle();
        fragmentHistory.setArguments(args);
        return fragmentHistory;
    }

    public static final String IS_FROM_I_HISTORY = "FromMy";

    private CursorAdapterAllFlight mAdapterAllFlight;
    private Utilities utilities;

    private static final int HISTORY_LOADER = 11;

    private LinearLayout layout;
    private ListView lvHistory;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(HISTORY_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        utilities = new Utilities(getActivity());
        mAdapterAllFlight = new CursorAdapterAllFlight(getActivity(), null, HISTORY_LOADER);
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_history, container, false);
        lvHistory = (ListView) rootView.findViewById(R.id.lv_history);

        lvHistory.setAdapter(mAdapterAllFlight);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        getLoaderManager().restartLoader(HISTORY_LOADER, null, this);

        lvHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object o = lvHistory.getItemAtPosition(position);
                Log.d(TAG, "Position :" + position);
                Cursor cursor = (Cursor) o;
                cursor.moveToFirst();
                Intent intentDetail = new Intent(getActivity(), FLightDetailActivity.class);
                intentDetail.putExtra(FragmentInternational.KEY_LEAVE_FROM, cursor.getString(KContact.History.COL_LEAVE_FROM));
                intentDetail.putExtra(FragmentInternational.KEY_GO_TO, cursor.getString(KContact.History.COL_GO_TO));
                intentDetail.putExtra(AdapterAllFlight.KEY_FLIGHT_NO, cursor.getString(KContact.History.COL_FLiGHT_NO));
                intentDetail.putExtra(AdapterAllFlight.KEY_CLASS, cursor.getString(KContact.History.COL_CLASS));
                intentDetail.putExtra(AdapterAllFlight.KEY_PRICE, cursor.getString(KContact.History.COL_PRICE));
                intentDetail.putExtra(AdapterAllFlight.KEY_DEPART, cursor.getString(KContact.History.COL_DEPART));
                intentDetail.putExtra(AdapterAllFlight.KEY_ARRIVE, cursor.getString(KContact.History.COL_ARRIVE));
                intentDetail.putExtra(AdapterAllFlight.KEY_LEAVE_RETURN, cursor.getString(KContact.History.COL_LEAVE_RETURN));
                intentDetail.putExtra(AdapterAllFlight.KEY_DETAIL, cursor.getString(KContact.History.COL_DETAIL));
                intentDetail.putExtra(IS_FROM_I_HISTORY, true);

                startActivity(intentDetail);
            }
        });

        ///delete old record
        deleteOldHistory();

    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "onCreateLoader WITH ID :" + id);
        String order = KContact.History.COLUMN_DATE_INSERT + " DESC";
        Uri historyUri = KContact.History.CONTENT_URI;
        CursorLoader cursor = new CursorLoader(getActivity(),
                historyUri,
                KContact.History.columnHacks,
                null,
                null,
                order);
        Log.d(TAG, "CursorLoader : " + cursor);
/*        //test
        Cursor c = getActivity().getContentResolver().query(KContact.History.CONTENT_URI,
                null,
                null,
                null,
                null,
                null);
        if (null != c) {
            c.moveToFirst();
            Log.d(TAG, "c get LeaveFrom :" + c.getString(c.getColumnIndex(KContact.History.COLUMN_LEAVE_FROM)));
        }*/
        return cursor;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(TAG, " onLoaderFinish Loader :" + loader);
        if (data != null)
//            Log.d(TAG, "Cursor : " + data.getString(data.getColumnIndex(KContact.History.COLUMN_LEAVE_FROM)));
            mAdapterAllFlight.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(TAG, "onLoaderReset");
        mAdapterAllFlight.swapCursor(null);
    }

    private void deleteOldHistory() {
        int numDays = 15;
        numDays = utilities.getNumDayKeepHistory();
        Uri uri = KContact.History.CONTENT_URI;
        Cursor cursor = getActivity().getContentResolver().query(
                uri,
                KContact.History.columnHacks,
                null,
                null,
                null
        );
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                long _id = Long.decode(cursor.getString(KContact.History.COL_ID));
                long dateInsert = Long.valueOf(cursor.getString(KContact.History.COL_DATE_INSERT));
                Long now = utilities.getCurrentDateInMillis();
                long drift = now - dateInsert;
//                Log.d(TAG, "MILI TO MINUTE :" + TimeUnit.MILLISECONDS.toMinutes(dateInsert) + " WITH DATE INSERT :" + utilities.convertLongToDateTime(dateInsert) + " ORIGINAL : " + dateInsert);
                if (TimeUnit.MILLISECONDS.toDays(drift) > numDays) {
                    long _idDel = getActivity().getContentResolver().delete(
                            KContact.History.buildHistoryUri(_id),
                            null,
                            null
                    );
                    Log.d(TAG, "Remove History ID :" + _idDel);
                }
                if (cursor.isLast())
                    break;
            } while (cursor.moveToNext());
        }
    }

}
