package nokieng.gdgvientiane.org.laoair;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;


public class FLightDetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flight_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, FragmentDetail.newInstance())
                    .commit();
        }
    }
}
