package nokieng.gdgvientiane.org.laoair;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Window;

import java.util.HashMap;


public class AllFlightActivity extends ActionBarActivity {


    HashMap<String, String> mMapSearch = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_all_flight);

        Intent getIntent = getIntent();
        mMapSearch = (HashMap<String, String>) getIntent.getSerializableExtra(FragmentAllFlight.KEY_HASHMAP_SEARCH);
        assert mMapSearch != null;
//        Log.d("AllFlightActivity", "mMapSearch : " + mMapSearch.toString());

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.all_flight_container, FragmentAllFlight.newInstance(mMapSearch))
                    .commit();
        }
    }
}
