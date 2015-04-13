package nokieng.gdgvientiane.org.laoair;


import android.app.Dialog;
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
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

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

    private static final String[] listDelete = new String[]{
            "See full detail",
            "Delete history",
            "Clear all history"
    };
    private Dialog dialog;
    private long lnCurrentItem = -1;


    public static final String IS_FROM_I_HISTORY = "FromMy";

    private CursorAdapterAllFlight mAdapterAllFlight;
    private Utilities utilities;

    private static final int HISTORY_LOADER = 11;

    private LinearLayout layout_history;
    private LinearLayout layout_history_no_data;
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

        layout_history = (LinearLayout) rootView.findViewById(R.id.layout_history);
        layout_history_no_data = (LinearLayout) rootView.findViewById(R.id.layout_history_no_data);

        lvHistory = (ListView) rootView.findViewById(R.id.lv_history);

        dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        lvHistory.setAdapter(mAdapterAllFlight);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (layout_history.getVisibility() == View.VISIBLE)
            layout_history_no_data.setVisibility(View.GONE);
        else layout_history_no_data.setVisibility(View.VISIBLE);

        if (!dialog.isShowing()) {
            lnCurrentItem = -1;
        }

        getLoaderManager().restartLoader(HISTORY_LOADER, null, this);

        lvHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intentDetail = getDetailForIntent(position);
                startActivity(intentDetail);
            }
        });

        lvHistory.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final Cursor cursor = (Cursor) lvHistory.getItemAtPosition(position);
                lnCurrentItem = Long.decode(cursor.getString(KContact.History.COL_ID));
                dialog.setContentView(R.layout.dialog_listview);
//                dialog.setCancelable();
                final ListView listView = (ListView) dialog.findViewById(R.id.dialog_lv);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity().getApplicationContext(),
                        R.layout.text_view1, listDelete);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        switch (position) {
                            case 0:
                                Intent intent = getDetailForIntent(position);
                                startActivity(intent);
                                break;
                            case 1:
                                long _id = deleteOldHistoryItem(lnCurrentItem);
                                if (_id != -1) {
                                    Toast.makeText(getActivity(), "Deleted", Toast.LENGTH_SHORT).show();
                                    onResume();
                                }
                                break;
                            case 2:
                                long _idAll = deleteOldHistoryAll();
                                if (_idAll != -1)
                                    Toast.makeText(getActivity(), "Clear all", Toast.LENGTH_SHORT).show();
                                break;
                            default:
                                break;
                        }
                        dialog.dismiss();
                    }
                });
                dialog.show();

                return true;
            }
        });

        ///delete old record
        deleteOldHistory();

    }

    private Intent getDetailForIntent(int position) {
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
        return intentDetail;
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
        Log.d(TAG, " onLoaderFinish Loader :" + loader + " Cursor : " + data.getCount());
        if (data.getCount() > 0) {
            mAdapterAllFlight.swapCursor(data);
        } else {
            layout_history_no_data.setVisibility(View.VISIBLE);
            layout_history.setVisibility(View.GONE);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(TAG, "onLoaderReset");
        mAdapterAllFlight.swapCursor(null);
    }


    private long deleteOldHistoryAll() {
        return (long) getActivity().getContentResolver().delete(
                KContact.History.CONTENT_URI,
                null,
                null
        );
    }

    private long deleteOldHistoryItem(long _id) {
        return (long) getActivity().getContentResolver().delete(
                KContact.History.buildHistoryUri(_id),
                null,
                null
        );
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
