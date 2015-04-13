package nokieng.gdgvientiane.org.laoair;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Window;

import java.util.HashMap;


public class AllFlightActivity extends ActionBarActivity implements FragmentAllFlight.Callbacks {

    private static final String TAG = AllFlightActivity.class.getSimpleName();

    private HashMap<String, String> mMapSearch = new HashMap<>();
    private static final String DETAILFRAGMENT_TAG = "DFTAG";
    private boolean mTwoPane = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_all_flight);

        Intent getIntent = getIntent();
        mMapSearch = (HashMap<String, String>) getIntent.getSerializableExtra(FragmentAllFlight.KEY_HASHMAP_SEARCH);
        assert mMapSearch != null;
//        Log.d("AllFlightActivity", "mMapSearch : " + mMapSearch.toString());

/*        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.all_flight_container, FragmentAllFlight.newInstance(mMapSearch))
                    .commit();
        }*/
        Log.d(TAG, " onCreate mTwoPane :" + mTwoPane);
        if (findViewById(R.id.all_flight_container) != null) {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.all_flight_container, FragmentDetail.newInstance(), DETAILFRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
            getSupportActionBar().setElevation(0f);
        }

        FragmentAllFlight fragmentAllFlight = ((FragmentAllFlight) getSupportFragmentManager()
                .findFragmentById(R.id.all_flight_container));
        fragmentAllFlight.setActivateOnItemClick(!mTwoPane);
    }

    @Override
    public void onItemSelected(String id) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(FragmentDetail.ARG_ITEM_ID, id);
            FragmentDetail fragment = new FragmentDetail();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.flight_detail_container, fragment)
                    .commit();

        } else {
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.
            Intent detailIntent = new Intent(this, FLightDetailActivity.class);
            detailIntent.putExtra(FragmentDetail.ARG_ITEM_ID, id);
            startActivity(detailIntent);
        }
    }
}
