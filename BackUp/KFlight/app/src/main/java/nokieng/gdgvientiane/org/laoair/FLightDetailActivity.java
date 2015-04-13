package nokieng.gdgvientiane.org.laoair;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ZoomButtonsController;

import java.lang.reflect.InvocationTargetException;

import nokieng.gdgvientiane.org.laoair.Helper.AdapterAllFlight;


public class FLightDetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flight_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, DetailFragment.newInstance())
                    .commit();
        }
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class DetailFragment extends Fragment {

        public DetailFragment() {
        }

        public static DetailFragment newInstance() {
            Bundle bundle = new Bundle();
            bundle.putString("key", "val");
            return new DetailFragment();
        }

        private WebView webView;
        private Toolbar toolbar;

        private String strDetail = "";
        private String strFlightNo = "";
        private String strPrice = "";
        private String strClassType = "";
        private String strLeave = "";
        private String strArrive = "";
        private String strLeaveReturn = "";
        private String strLeaveFrom = "";
        private String strGoTo = "";

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            setHasOptionsMenu(true);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_flight_detail, container, false);
            webView = (WebView) rootView.findViewById(R.id.wv_detail);
            toolbar = (Toolbar) rootView.findViewById(R.id.toolbar_detail);

            ActionBarActivity activity = (ActionBarActivity) getActivity();
            activity.setSupportActionBar(toolbar);
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            activity.getSupportActionBar().setHomeButtonEnabled(true);

            return rootView;
        }

        @Override
        public void onResume() {
            super.onResume();
            Intent getIntent = getActivity().getIntent();
            strDetail = getIntent.getStringExtra(AdapterAllFlight.KEY_DETAIL);
            strFlightNo = getIntent.getStringExtra(AdapterAllFlight.KEY_FLIGHT_NO);
            strClassType = getIntent.getStringExtra(AdapterAllFlight.KEY_CLASS);
            strPrice = getIntent.getStringExtra(AdapterAllFlight.KEY_PRICE);
            strLeave = getIntent.getStringExtra(AdapterAllFlight.KEY_DEPART);
            strArrive = getIntent.getStringExtra(AdapterAllFlight.KEY_ARRIVE);
            strLeaveReturn = getIntent.getStringExtra(AdapterAllFlight.KEY_LEAVE_RETURN);

            strLeaveFrom = getIntent.getStringExtra(FragmentInternational.KEY_LEAVE_FROM);
            strGoTo = getIntent.getStringExtra(FragmentInternational.KEY_GO_TO);


            if (toolbar != null) {
                toolbar.setTitle("Flight Detail, " + strFlightNo);
            }

            String first = strDetail.replace("&nbsp;", " ");

            String newHtml = "<Html> <Body>" + first + "<br>" +
                    "<h3>Lao Airlines Fare Condition</h3>" + strCondition + "</Body></Html>";
            webView.loadDataWithBaseURL(null, newHtml, "text/html", "utf-8", null);
            webView.getSettings().supportZoom();
            webView.getSettings().setBuiltInZoomControls(true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                new Runnable() {
                    @Override
                    public void run() {
                        webView.getSettings().setDisplayZoomControls(false);
                    }
                }.run();
            } else {
                try {
                    final ZoomButtonsController zoomControl
                            = (ZoomButtonsController) webView.getClass().getMethod("getZoomBuildController").invoke(webView, null);
                    zoomControl.getContainer().setVisibility(View.GONE);
                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }
        }

        private String strCondition = "<div style=\"height:20px\"><strong lang=\"en\">BOOKING CLASS</strong></div><div style=\"padding-left:10px; margin-bottom:7px;\">V</div><div style=\"height:20px\"><strong lang=\"en\">TICKET VALIDITY</strong></div><div style=\"padding-left:10px; margin-bottom:7px;\">3 Months.</div><div style=\"height:20px\"><strong lang=\"en\">REROUTE</strong></div><div style=\"padding-left:10px; margin-bottom:7px;\">Reroute flight or change destination is not permitted.</div><div style=\"height:20px\"><strong lang=\"en\">UPGRADE</strong></div><div style=\"padding-left:10px; margin-bottom:7px;\">Not permitted.</div><div style=\"height:20px\"><strong lang=\"en\">CHANGES AND CANCELLATION</strong></div><div style=\"padding-left:10px; margin-bottom:7px;\">Change date before departure date for the 1st time is permitted/.\n" +
                "Change second or third time charge USD 30/person/.\n" +
                "Cancellation any time charge USD 50/person for refund/.\n" +
                "No show charge USD 30/person/.</div><div style=\"height:20px\"><strong lang=\"en\">BAGGAGE ALLOWANCE</strong></div><div style=\"padding-left:10px; margin-bottom:7px;\">20 KGs for this booking class.</div><div id=\"MoreCon\" class=\"hide\" style=\"width:650px; padding:10px; \"><div style=\"height:20px\"><strong lang=\"en\">RULEID</strong></div><div style=\"padding-left:10px; margin-bottom:7px;\">94INTQT14V</div><div style=\"height:20px\"><strong lang=\"en\">BOOKING CLASS</strong></div><div style=\"padding-left:10px; margin-bottom:7px;\">V</div><div style=\"height:20px\"><strong lang=\"en\">TICKET VALIDITY</strong></div><div style=\"padding-left:10px; margin-bottom:7px;\">3 Months.</div><div style=\"height:20px\"><strong lang=\"en\">REROUTE</strong></div><div style=\"padding-left:10px; margin-bottom:7px;\">Reroute flight or change destination is not permitted.</div><div style=\"height:20px\"><strong lang=\"en\">UPGRADE</strong></div><div style=\"padding-left:10px; margin-bottom:7px;\">Not permitted.</div><div style=\"height:20px\"><strong lang=\"en\">CHANGES AND CANCELLATION</strong></div><div style=\"padding-left:10px; margin-bottom:7px;\">Change date before departure date for the 1st time is permitted/.\n" +
                "Change second or third time charge USD 30/person/.\n" +
                "Cancellation any time charge USD 50/person for refund/.\n" +
                "No show charge USD 30/person/.</div><div style=\"height:20px\"><strong lang=\"en\">BAGGAGE ALLOWANCE</strong></div><div style=\"padding-left:10px; margin-bottom:7px;\">20 KGs for this booking class.</div><div style=\"height:20px\"><strong lang=\"en\">CABIN CLASS</strong></div><div style=\"padding-left:10px; margin-bottom:7px;\">Economy</div><div style=\"height:20px\"><strong lang=\"en\">ELIGIBILITY</strong></div><div style=\"padding-left:10px; margin-bottom:7px;\">For adult. </div><div style=\"height:20px\"><strong lang=\"en\">SALES TICKETING</strong></div><div style=\"padding-left:10px; margin-bottom:7px;\">Journey must be completed within ticket validity.</div><div style=\"height:20px\"><strong lang=\"en\">CHILD INF WITH SEAT</strong></div><div style=\"padding-left:10px; margin-bottom:7px;\">Fare 25% discount. Unaccompanied minors are not permitted at these fares.</div><div style=\"height:20px\"><strong lang=\"en\">INFANT WITHOUT SEAT</strong></div><div style=\"padding-left:10px; margin-bottom:7px;\">Pay 10% of published fare.</div><div style=\"height:20px\"><strong lang=\"en\">COMBINATION</strong></div><div style=\"padding-left:10px; margin-bottom:7px;\">Not permitted.</div><div style=\"height:20px\"><strong lang=\"en\">EXTEND</strong></div><div style=\"padding-left:10px; margin-bottom:7px;\">Not permitted.</div><div style=\"height:20px\"><strong lang=\"en\">NAME CHANGE</strong></div><div style=\"padding-left:10px; margin-bottom:7px;\">Not permitted.</div><div style=\"height:20px\"><strong lang=\"en\">DATE FLIGHT CHANGE</strong></div><div style=\"padding-left:10px; margin-bottom:7px;\">Change date any time before departure flight is permitted without fee only one time per ticket. (Maximum: 3 times per ticket).</div><div style=\"height:20px\"><strong lang=\"en\">STOPOVERS</strong></div><div style=\"padding-left:10px; margin-bottom:7px;\">1 Stopover permitted in each direction.</div><div style=\"height:20px\"><strong lang=\"en\">TRANSFER</strong></div><div style=\"padding-left:10px; margin-bottom:7px;\">1 Transfer permitted in each direction.  </div><div style=\"height:20px\"><strong lang=\"en\">ENDORSEMENT</strong></div><div style=\"padding-left:10px; margin-bottom:7px;\">Valid on QV operates flight only/non endorsement/penalty applies.</div><div style=\"height:20px\"><strong lang=\"en\">YQ AND TAXES</strong></div><div style=\"padding-left:10px; margin-bottom:7px;\">YQ surcharge and taxes are not included. Taxes imposed by departure Airport is collected at time of ticketing.</div><div style=\"height:20px\"><strong lang=\"en\">CAPACITY LIMITATIONS</strong></div><div style=\"padding-left:10px; margin-bottom:7px;\">The carrier shall limit the number of passenger carried on any one flight at fares governed by this rule and search fares will not necessary by available on all flights. The number of seats which the carrier shall make available on a given flight may vary and will be determined by the carrier’s best judgment.</div><div style=\"height:20px\"><strong lang=\"en\">OTHERS</strong></div><div style=\"padding-left:10px; margin-bottom:7px;\">Carrier reserves the right to alter/withdraw this fare without prior notice.</div></div>";

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            super.onCreateOptionsMenu(menu, inflater);
            inflater.inflate(R.menu.all_flight, menu);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_setting:
                    Intent intent = new Intent(getActivity(), SettingsActivity.class);
                    startActivity(intent);
            }
            return super.onOptionsItemSelected(item);
        }

    }
}
