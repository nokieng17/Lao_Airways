package nokieng.gdgvientiane.org.laoair;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;


public class FLightDetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flight_detail);

        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(FragmentDetail.ARG_ITEM_ID,
                    getIntent().getStringExtra(FragmentDetail.ARG_ITEM_ID));
            Fragment fragment = new Fragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.all_flight_container, FragmentDetail.newInstance())
                    .commit();
        }
    }
}
