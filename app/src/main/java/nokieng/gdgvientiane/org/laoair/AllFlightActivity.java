package nokieng.gdgvientiane.org.laoair;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Window;

import java.util.HashMap;

import nokieng.gdgvientiane.org.laoair.Helper.AdapterAllFlight;


public class AllFlightActivity extends ActionBarActivity implements FragmentAllFlight.Callback {

    private static final String TAG = AllFlightActivity.class.getSimpleName();
    private static final String DETAILFRAGMENT_TAG = "DFTAG";

    private HashMap<String, String> mMapSearch = new HashMap<>();
    private boolean mTwoPane = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_all_flight);


        assert mMapSearch != null;
//        Log.d("AllFlightActivity", "mMapSearch : " + mMapSearch.toString());

        //when not design for tablet
        if (findViewById(R.id.fragment_flight_detail) != null) {
            //this container view will be present only in the large screen layout smallest width 600dp
            //if this is present the layout should be two pane layout
            mTwoPane = true;
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_flight_detail, FragmentDetail.newInstance(mMapSearch), DETAILFRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_all_flight, FragmentAllFlight.newInstance())
                        .commit();
            }
        }

/*        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_all_flight, FragmentAllFlight.newInstance())
                    .commit();
        }*/
        Log.d(TAG, "mTwoPane : " + mTwoPane);
    }

    @Override
    public void onItemSelected(HashMap<String, String> items, boolean isClick) {
        if (mTwoPane) {
            Log.d(TAG, "all fligh str leave from : " + items.get(FragmentInternational.KEY_LEAVE_FROM));
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_flight_detail, FragmentDetail.newInstance(items))
                    .commit();
        } else {
            if (isClick) {
                Intent intent = new Intent(AllFlightActivity.this, FLightDetailActivity.class);
                intent.putExtra(AdapterAllFlight.KEY_FLIGHT_NO, items.get(AdapterAllFlight.KEY_FLIGHT_NO));
                intent.putExtra(AdapterAllFlight.KEY_CLASS, items.get(AdapterAllFlight.KEY_CLASS));
                intent.putExtra(AdapterAllFlight.KEY_PRICE, items.get(AdapterAllFlight.KEY_PRICE));
                intent.putExtra(AdapterAllFlight.KEY_DEPART, items.get(AdapterAllFlight.KEY_DEPART));
                intent.putExtra(AdapterAllFlight.KEY_ARRIVE, items.get(AdapterAllFlight.KEY_ARRIVE));
                intent.putExtra(AdapterAllFlight.KEY_LEAVE_RETURN, items.get(AdapterAllFlight.KEY_LEAVE_RETURN));
                intent.putExtra(AdapterAllFlight.KEY_DETAIL, items.get(AdapterAllFlight.KEY_DETAIL));

                intent.putExtra(FragmentInternational.KEY_LEAVE_FROM, items.get(FragmentInternational.KEY_LEAVE_FROM));
                intent.putExtra(FragmentInternational.KEY_GO_TO, items.get(FragmentInternational.KEY_GO_TO));
                //TODO set intent data here, to sent array item to detail activity
                startActivity(intent);
            }
        }
    }

}
