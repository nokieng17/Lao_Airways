package nokieng.gdgvientiane.org.laoair;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import nokieng.gdgvientiane.org.laoair.Helper.AdapterAllFlight;

/**
 * A placeholder fragment containing a simple view.
 */
public class FragmentAllFlight extends Fragment {

    private final String TAG = FragmentAllFlight.class.getSimpleName();

    private AdapterAllFlight adapter;
    //HasMap get search from intent
    private HashMap<String, String> mMapSearch = new HashMap<>();
    public static final String KEY_HASHMAP_SEARCH = "HashMapSearch";
    public static final String KEY_TWO_PANE = "mTwoPane";
    private TextView txtHeader;
    private ListView lvFlight;

    private String strLeaveFrom = "";
    private String strGoTo = "";
    private String strClassType = "";
    private String strRoundType = "";
    private String strLeaveDate = "";
    private String strReturnDate = "";

    private String strSuccess = "";

    //expire cache date, 3 day is quite fair. cus flight data will not frequency change
    private int expireDate = 3 * 24 * 60 * 60 * 1000;

    private Toolbar toolbar;
    private ProgressBar pb;
    private LinearLayout layout;

    private SharedPreferences sharedPreferences;

    private ArrayList<HashMap<String, String>> mListMapAll = new ArrayList<>();
    private int listViewPosition = ListView.INVALID_POSITION;
    private final static String KEY_LIST_VIEW_POSITION = "KEY_LIST_VIEW_POSITION";
    //    private Vector<ContentValues> vValues;
    private static final int ALL_FLIGHT_LOADER = 12;
    private boolean mTwoPane = false;

    public interface Callback {
        public void onItemSelected(HashMap<String, String> items, boolean isClick, boolean isRotate);
    }

    public static FragmentAllFlight newInstance() {
        FragmentAllFlight fragment = new FragmentAllFlight();
        Bundle args = new Bundle();
        args.putBoolean("KEY", false);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mTwoPane = getResources().getBoolean(R.bool.mTwoPane);
        if (!mTwoPane) {
            setHasOptionsMenu(true);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_all_flight, container, false);

        Intent getIntent = getActivity().getIntent();
        mMapSearch = (HashMap<String, String>) getIntent.getSerializableExtra(KEY_HASHMAP_SEARCH);
        Log.d(TAG, "mMapSearch getIntent : " + mMapSearch);

        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar_all_flight);
        pb = (ProgressBar) rootView.findViewById(R.id.pb_all_flight);
        layout = (LinearLayout) rootView.findViewById(R.id.layout_all_flight);

        //work with View
        layout.setVisibility(View.GONE);
        pb.setVisibility(View.VISIBLE);

        txtHeader = (TextView) rootView.findViewById(R.id.txt_all_flight_header);
        lvFlight = (ListView) rootView.findViewById(R.id.lv_all_flight);
        lvFlight.setChoiceMode(ListView.CHOICE_MODE_SINGLE);


        sharedPreferences = getActivity().getSharedPreferences("AllFLight", 0x0000);        //0x0000 is MODE_PRIVATE or 0


        if (mMapSearch != null) {
            strLeaveFrom = mMapSearch.get(FragmentInternational.KEY_LEAVE_FROM);
            strGoTo = mMapSearch.get(FragmentInternational.KEY_GO_TO);
            strClassType = mMapSearch.get(FragmentInternational.KEY_CLASS_TYPE);
            strRoundType = mMapSearch.get(FragmentInternational.KEY_ROUND_TYPE);
            strLeaveDate = mMapSearch.get(FragmentInternational.KEY_LEAVE_DATE);
            strReturnDate = mMapSearch.get(FragmentInternational.KEY_RETURN_DATE);

            //save pre

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(FragmentInternational.KEY_LEAVE_FROM, strLeaveFrom);
            editor.putString(FragmentInternational.KEY_GO_TO, strGoTo);
            editor.putString(FragmentInternational.KEY_CLASS_TYPE, strClassType);
            editor.putString(FragmentInternational.KEY_ROUND_TYPE, strRoundType);
            editor.putString(FragmentInternational.KEY_LEAVE_DATE, strLeaveDate);
            editor.putString(FragmentInternational.KEY_RETURN_DATE, strReturnDate);

            editor.commit();
        } else {
            strLeaveFrom = sharedPreferences.getString(FragmentInternational.KEY_LEAVE_FROM, "");
            strGoTo = sharedPreferences.getString(FragmentInternational.KEY_GO_TO, "");
            strClassType = sharedPreferences.getString(FragmentInternational.KEY_CLASS_TYPE, "");
            strRoundType = sharedPreferences.getString(FragmentInternational.KEY_ROUND_TYPE, "");
            strLeaveDate = sharedPreferences.getString(FragmentInternational.KEY_LEAVE_DATE, "");
            strReturnDate = sharedPreferences.getString(FragmentInternational.KEY_RETURN_DATE, "");
        }

        if (strLeaveFrom.equals("") || strGoTo.equals("") || strClassType.equals("") || strRoundType.equals("") || strLeaveDate.equals("")) {
            Toast.makeText(getActivity().getApplicationContext(), "Invalid data ...!", Toast.LENGTH_SHORT).show();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    getActivity().finish();
                }
            }, 1000);
        }


        if (adapter == null)
            aqGetDestination(strLeaveFrom, strGoTo, strClassType, strRoundType);
        else {
            if (adapter.getCount() == -1)
                aqGetDestination(strLeaveFrom, strGoTo, strClassType, strRoundType);
        }

        lvFlight.setSelected(true);

        lvFlight.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String, String> item = (HashMap<String, String>) lvFlight.getItemAtPosition(position);
                item.put(FragmentInternational.KEY_LEAVE_FROM, strLeaveFrom);
                item.put(FragmentInternational.KEY_GO_TO, strGoTo);
                if (mTwoPane) {
                    ((Callback) getActivity()).onItemSelected(
                            item, true, false
                    );
                } else {
                    Intent intentDetail = new Intent(getActivity(), FLightDetailActivity.class);
                    intentDetail.putExtra(AdapterAllFlight.KEY_FLIGHT_NO, item.get(AdapterAllFlight.KEY_FLIGHT_NO));
                    intentDetail.putExtra(AdapterAllFlight.KEY_CLASS, item.get(AdapterAllFlight.KEY_CLASS));
                    intentDetail.putExtra(AdapterAllFlight.KEY_PRICE, item.get(AdapterAllFlight.KEY_PRICE));
                    intentDetail.putExtra(AdapterAllFlight.KEY_DEPART, item.get(AdapterAllFlight.KEY_DEPART));
                    intentDetail.putExtra(AdapterAllFlight.KEY_ARRIVE, item.get(AdapterAllFlight.KEY_ARRIVE));
                    intentDetail.putExtra(AdapterAllFlight.KEY_LEAVE_RETURN, item.get(AdapterAllFlight.KEY_LEAVE_RETURN));
                    intentDetail.putExtra(AdapterAllFlight.KEY_DETAIL, item.get(AdapterAllFlight.KEY_DETAIL));

                    intentDetail.putExtra(FragmentInternational.KEY_LEAVE_FROM, strLeaveFrom);
                    intentDetail.putExtra(FragmentInternational.KEY_GO_TO, strGoTo);
                    Log.d(TAG, "listView onCLick strLeaveFrom : " + strLeaveFrom + strGoTo);
                    startActivity(intentDetail);
                }
                listViewPosition = position;
            }
        });

        if (savedInstanceState != null && savedInstanceState.containsKey(KEY_LIST_VIEW_POSITION)) {
            listViewPosition = savedInstanceState.getInt(KEY_LIST_VIEW_POSITION);
            Log.d(TAG, "GET LIST POSITION FROM SAVE BUNDLE : " + listViewPosition);
        }

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (lvFlight != null && listViewPosition != ListView.INVALID_POSITION) {
            Log.d(TAG, "SELECT SMOOTH SCROLL AT : " + listViewPosition);
            lvFlight.post(new Runnable() {
                @Override
                public void run() {
                    lvFlight.setItemChecked(listViewPosition, true);
                    HashMap<String, String> item = (HashMap<String, String>) lvFlight.getItemAtPosition(listViewPosition);
                    if (mTwoPane) {
                        ((Callback) getActivity()).onItemSelected(
                                item, true, true
                        );
                    }//item HasMap, isClick, isRotate (if otate do not have to save any data
                }
            });
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (listViewPosition != ListView.INVALID_POSITION) {
            outState.putInt(KEY_LIST_VIEW_POSITION, listViewPosition);
        }
        super.onSaveInstanceState(outState);
    }

    private void aqGetDestination(final String strLeaveFrom, final String strGoTo, String strClassType, String strRoundType) {
        String url = "http://tk.aseanlinc.com/airline/tkairservices.php?Task=GetDetail&LeaveFrom=" + strLeaveFrom + "&GoTo=" + strGoTo + "&ClassType=" + strClassType + "&RoundType=" + strRoundType;
        AQuery aq = new AQuery(getActivity());
        AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {
                Log.d(TAG, "get detail aquery ");
                if (json != null) {
                    try {
                        if (json.get(AdapterAllFlight.KEY_SUCCESS).toString().trim().equals("1")) {
                            for (int i = 0; i < 5; i++) {
                                try {
                                    JSONObject objItem = json.getJSONObject(String.valueOf(i));
                                    HashMap<String, String> item = new HashMap<>();
//                                    ContentValues item = new ContentValues();
                                    String strPrice = objItem.getString(AdapterAllFlight.KEY_PRICE);
                                    item.put(AdapterAllFlight.KEY_FLIGHT_NO, objItem.getString(AdapterAllFlight.KEY_FLIGHT_NO));
                                    item.put(AdapterAllFlight.KEY_CLASS, objItem.getString(AdapterAllFlight.KEY_CLASS));
                                    item.put(AdapterAllFlight.KEY_PRICE, strPrice);
                                    item.put(AdapterAllFlight.KEY_DEPART, objItem.getString(AdapterAllFlight.KEY_DEPART));
                                    item.put(AdapterAllFlight.KEY_ARRIVE, objItem.getString(AdapterAllFlight.KEY_ARRIVE));
                                    item.put(AdapterAllFlight.KEY_LEAVE_RETURN, objItem.getString(AdapterAllFlight.KEY_LEAVE_RETURN));
                                    String strDetail = objItem.getString(AdapterAllFlight.KEY_DETAIL).replace("<.", "</");
                                    item.put(AdapterAllFlight.KEY_DETAIL, strDetail);

                                    mListMapAll.add(item);

                                    //set toolbar
                                    setToolBar();

                                } catch (Exception ignored) {
                                }
                            }
//                            Log.d(TAG, "mListMapAll :" + mListMapAll.toString());

                            adapter = new AdapterAllFlight(getActivity(), mListMapAll, strLeaveFrom, strGoTo);
                            lvFlight.setAdapter(adapter);
                            adapter.notifyDataSetChanged();

                            lvFlight.requestFocus();
                            lvFlight.smoothScrollToPosition(listViewPosition);
                            //set first Item
                            if (mTwoPane)
                                if (lvFlight.getSelectedItemPosition() == -1) {
                                    HashMap<String, String> item = (HashMap<String, String>) lvFlight.getItemAtPosition(0);
                                    item.put(FragmentInternational.KEY_LEAVE_FROM, strLeaveFrom);
                                    item.put(FragmentInternational.KEY_GO_TO, strGoTo);
                                    ((Callback) getActivity()).onItemSelected(
                                            item, false, false
                                    );
                                }
                            layout.setVisibility(View.VISIBLE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else

                {
                    status.invalidate();
                }
            }
        };

        cb.fileCache(true);
        cb.expire(expireDate);

        ProgressDialog dialog = new ProgressDialog(getActivity().getApplicationContext());

        dialog.setIndeterminate(true);
        dialog.setCancelable(true);
        dialog.setInverseBackgroundForced(false);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setTitle(getResources().getString(R.string.searching));

        aq.progress(pb).ajax(url, JSONObject.class, cb);
    }

    private void setToolBar() {
        if (toolbar != null) {
            ActionBarActivity activity = (ActionBarActivity) getActivity();
            activity.setSupportActionBar(toolbar);
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            activity.getSupportActionBar().setHomeButtonEnabled(true);
            String title = "";
            if (!mTwoPane) {
                title = mMapSearch.get(FragmentInternational.KEY_FULL_NAME_LEAVE)
                        + ", "
                        + mMapSearch.get(FragmentInternational.KEY_FULL_NAME_GO_TO)
                        + ". Leave: " + strLeaveDate;
            } else {
                title = strLeaveFrom + ", " + strGoTo + ". Leave: " + strLeaveDate;
            }
            if (strRoundType.trim().equals("0")) {
                title = title + ", Return: " + strReturnDate;
            }
            toolbar.setTitle(title);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (menu != null)
            menu.clear();
        inflater.inflate(R.menu.all_flight, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_setting:
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.action_about:
                Intent intentAbout = new Intent(getActivity(), AboutActivity.class);
                startActivity(intentAbout);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}