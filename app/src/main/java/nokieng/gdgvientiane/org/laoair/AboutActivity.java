package nokieng.gdgvientiane.org.laoair;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import nokieng.gdgvientiane.org.laoair.Helper.Utilities;


public class AboutActivity extends ActionBarActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);


        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.about_container, AboutFragment.newInstance())
                    .commit();
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */


    public static class AboutFragment extends Fragment {

        public AboutFragment() {
        }

        public static AboutFragment newInstance() {
            AboutFragment fragment = new AboutFragment();
            Bundle args = new Bundle();
            fragment.setArguments(args);
            return fragment;
        }

        Toolbar toolbar;

        private TextView txtVersion;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_about, container, false);
            txtVersion = (TextView) rootView.findViewById(R.id.txt_about_version);
            Utilities utilities = new Utilities(getActivity().getApplicationContext());

            txtVersion.setText(utilities.getVersion());

            toolbar = (Toolbar) rootView.findViewById(R.id.toolbar_about);

            if (toolbar != null) {
                ActionBarActivity activity = (ActionBarActivity) getActivity();
                activity.setSupportActionBar(toolbar);
                activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                activity.getSupportActionBar().setHomeButtonEnabled(true);
                toolbar.setTitle(getResources().getString(R.string.action_about));
            }
            return rootView;
        }

    }
}
