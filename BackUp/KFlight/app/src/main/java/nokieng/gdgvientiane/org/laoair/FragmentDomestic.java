package nokieng.gdgvientiane.org.laoair;


import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentDomestic extends Fragment {

    public static FragmentDomestic newInstance() {
        Bundle bundle = new Bundle();
        bundle.putString("key", "value");
        return new FragmentDomestic();
    }


    private RadioGroup rgWay, rgTriType;
    private RadioButton rbRoundTrip, rbOneWay, rbTypeAll, rbTypeEconomy, rbTypeBusiness;
    private Spinner spnLeaveFrom, spnGoTo, spnAdults, spnChildren, spnInfant;
    private TextView txtDateFrom, txtDateTo;
    private Button btnSearch;


    private int intYears;
    private int intMonth;
    private int intDay;

    private ArrayAdapter<String> adapterLeaveFrom;
    private ArrayAdapter<String> adapterGoTo;
    private ArrayAdapter<String> adapterAdults;
    private ArrayAdapter<String> adapterChildren;
    private ArrayAdapter<String> adapterInfant;

    public FragmentDomestic() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_domestic, container, false);

        rgWay = (RadioGroup) rootView.findViewById(R.id.rg_domestic_way);
        rgTriType = (RadioGroup) rootView.findViewById(R.id.rg_domestic_trip_type);

        rbRoundTrip = (RadioButton) rootView.findViewById(R.id.rb_domestic_round_trip);
        rbOneWay = (RadioButton) rootView.findViewById(R.id.rb_domestic_one_way);
        rbTypeAll = (RadioButton) rootView.findViewById(R.id.rb_domestic_type_all);
        rbTypeEconomy = (RadioButton) rootView.findViewById(R.id.rb_domestic_type_economy);
        rbTypeBusiness = (RadioButton) rootView.findViewById(R.id.rb_domestic_type_business);

        spnLeaveFrom = (Spinner) rootView.findViewById(R.id.spn_domestic_leave_from);
        spnGoTo = (Spinner) rootView.findViewById(R.id.spn_domestic_go_to);
        spnAdults = (Spinner) rootView.findViewById(R.id.spn_domestic_adult);
        spnChildren = (Spinner) rootView.findViewById(R.id.spn_domestic_children);
        spnInfant = (Spinner) rootView.findViewById(R.id.spn_domestic_infants);

        txtDateFrom = (TextView) rootView.findViewById(R.id.txt_domestic_date_from);
        txtDateTo = (TextView) rootView.findViewById(R.id.txt_domestic_date_to);

        btnSearch = (Button) rootView.findViewById(R.id.btn_domestic_search);

        return rootView;
    }


    @Override
    public void onStart() {
        super.onStart();
        setDefaultValues();


    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void setDefaultValues() {

        adapterLeaveFrom = new ArrayAdapter<String>(getActivity().getApplicationContext(),
                R.layout.text_view1, FragmentInternational.LEAVE_FROM);
        spnLeaveFrom.setAdapter(adapterLeaveFrom);

        adapterGoTo = new ArrayAdapter<String>(getActivity().getApplicationContext()
                , R.layout.text_view1, FragmentInternational.GOING_TO);
        spnGoTo.setAdapter(adapterGoTo);

        adapterAdults = new ArrayAdapter<String>(getActivity().getApplicationContext(),
                R.layout.text_view1, FragmentInternational.ADULTS);
        spnAdults.setAdapter(adapterAdults);

        adapterChildren = new ArrayAdapter<String>(getActivity().getApplicationContext(),
                R.layout.text_view1, FragmentInternational.CHILDREN);
        spnChildren.setAdapter(adapterChildren);

        adapterInfant = new ArrayAdapter<String>(getActivity().getApplicationContext(),
                R.layout.text_view1, FragmentInternational.INFANT);
        spnInfant.setAdapter(adapterInfant);

        rbTypeAll.setChecked(true);
        rbRoundTrip.setChecked(true);

        Calendar mCalendar = Calendar.getInstance();
        txtDateFrom.setText(String.valueOf(mCalendar.get(Calendar.YEAR) + "-" + (mCalendar.get(Calendar.MONTH) + 1) + "-" + mCalendar.get(Calendar.DAY_OF_MONTH)));
        txtDateTo.setText(String.valueOf(mCalendar.get(Calendar.YEAR) + "-" + (mCalendar.get(Calendar.MONTH) + 1) + "-" + mCalendar.get(Calendar.DAY_OF_MONTH)));
    }


    private DatePickerDialog showDatePickerDialog(int initialYear, int initialMonth, int initialDay, DatePickerDialog.OnDateSetListener listener) {
        DatePickerDialog dialog = new DatePickerDialog(getActivity().getApplicationContext(), listener, initialYear, initialMonth, initialDay);
        dialog.show();
        return dialog;
    }

    // CallBacks for date pickers
    private DatePickerDialog.OnDateSetListener mSelectDateFrom = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            intDay = dayOfMonth;
            intMonth = monthOfYear;
            intYears = year;

            txtDateFrom.setText(intYears + "-" + intMonth + "-" + intDay);
        }
    };
    // CallBacks for date pickers
    private DatePickerDialog.OnDateSetListener mSelectDateTo = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            intDay = dayOfMonth;
            intMonth = monthOfYear;
            intYears = year;

            txtDateTo.setText(intYears + "-" + intMonth + "-" + intDay);
        }
    };


}
