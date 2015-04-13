package nokieng.gdgvientiane.org.laoair.Helper;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import nokieng.gdgvientiane.org.laoair.R;
import nokieng.gdgvientiane.org.laoair.data.KContact;

/**
 * Created by kieng on 4/3/2015.
 */

public class CursorAdapterAllFlight extends CursorAdapter {

    private static final String TAG = CursorAdapterAllFlight.class.getSimpleName();

    private Utilities utilities;

    public static final String KEY_SUCCESS = "Success";
    public static final String KEY_FLIGHT_NO = "FlightNO";
    public static final String KEY_CLASS = "ClassType";
    public static final String KEY_PRICE = "Price";
    public static final String KEY_DEPART = "DepartureTime";
    public static final String KEY_ARRIVE = "ArriveTime";
    public static final String KEY_LEAVE_RETURN = "LeaveOrReturn";
    public static final String KEY_DETAIL = "Detail";

    private final Context getActivity;
    private String strLeaveFrom = "";
    private String strGoTo = "";
    private Cursor mCursor;


    public CursorAdapterAllFlight(Context context, Cursor c, int flags) {
        super(context, c, flags);
        this.getActivity = context;
        this.mCursor = c;
        utilities = new Utilities(getActivity);
        Log.d(TAG, "Constructor of CursorAdapter ");
    }

    public static class ViewHolder {
        private TextView txtFlightNo, txtClass, txtPrice, txtDepart, txtLeaveReturn, txtDateHistory;

        public ViewHolder(View rootView) {
            txtFlightNo = (TextView) rootView.findViewById(R.id.txt_all_flight_flight_no);
            txtClass = (TextView) rootView.findViewById(R.id.txt_all_flight_class);
            txtPrice = (TextView) rootView.findViewById(R.id.txt_all_flight_price);
            txtDepart = (TextView) rootView.findViewById(R.id.txt_all_flight_depart_arrive);
            txtLeaveReturn = (TextView) rootView.findViewById(R.id.txt_all_flight_leave_return);
            txtDateHistory = (TextView) rootView.findViewById(R.id.txt_history_date_history);
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        Log.d(TAG, "New View Cursor :" + cursor.getCount());
        View rootView = LayoutInflater.from(mContext).inflate(R.layout.item_history, parent, false);
        ViewHolder viewHolder = new ViewHolder(rootView);
        rootView.setTag(viewHolder);
        return rootView;
    }

    @Override
    public void bindView(View rootView, Context context, Cursor cursor) {
        Log.d(TAG, "on bindView");
        String dateInsert = utilities.calculateDate(Long.parseLong(cursor.getString(KContact.History.COL_DATE_INSERT)));

        String header = "Lao Airline | FLight Number " +
                cursor.getString(KContact.History.COL_FLiGHT_NO) + " . From: " +
                utilities.getCodeFullName(cursor.getString(KContact.History.COL_LEAVE_FROM).toUpperCase()) + " To: " +
                utilities.getCodeFullName(cursor.getString(KContact.History.COL_GO_TO).toUpperCase());


        Log.d(TAG, "on newView");
        ViewHolder viewHolder = (ViewHolder) rootView.getTag();

        //Our view is pretty simple here -- just a text view
        //we'll keep the UI functional with a simple (and slow) binding.
        //set text here
        String price = utilities.calPrice(cursor.getString(KContact.History.COL_PRICE));
        viewHolder.txtFlightNo.setText(header);
        viewHolder.txtClass.setText("Class: " + cursor.getString(KContact.History.COL_CLASS));
        viewHolder.txtPrice.setText("Price: " + price);
        viewHolder.txtDepart.setText("Depart: " + cursor.getString(KContact.History.COL_DEPART).replace("-", ":") +
                " (" +
                cursor.getString(KContact.History.COL_LEAVE_FROM) + ") " +
                "Arrive: " + cursor.getString(KContact.History.COL_ARRIVE).replace("-", ":") +
                " (" + cursor.getString(KContact.History.COL_GO_TO) + ")");
        viewHolder.txtLeaveReturn.setText(cursor.getString(KContact.History.COL_LEAVE_RETURN) + " Flight");
        viewHolder.txtDateHistory.setText(dateInsert);

    }
}
