package nokieng.gdgvientiane.org.laoair.Helper;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import nokieng.gdgvientiane.org.laoair.R;

/**
 * Created by kieng on 4/3/2015.
 */

public class AdapterAllFlight extends ArrayAdapter<HashMap<String, String>> {

    private static final String TAG = AdapterAllFlight.class.getSimpleName();

    public static final String KEY_SUCCESS = "Success";
    public static final String KEY_FLIGHT_NO = "FlightNO";
    public static final String KEY_CLASS = "ClassType";
    public static final String KEY_PRICE = "Price";
    public static final String KEY_DEPART = "DepartureTime";
    public static final String KEY_ARRIVE = "ArriveTime";
    public static final String KEY_LEAVE_RETURN = "LeaveOrReturn";
    public static final String KEY_DETAIL = "Detail";

    private final Context getActivity;
    private ArrayList<HashMap<String, String>> listAllItem;
    private String strLeaveFrom = "";
    private String strGoTo = "";


    public AdapterAllFlight(Context getActivity, ArrayList<HashMap<String, String>> listAllItem, String strLeaveFrom, String strGoTo) {
        super(getActivity, R.layout.fragment_all_flight, listAllItem);
        this.getActivity = getActivity;
        this.listAllItem = listAllItem;
        this.strLeaveFrom = strLeaveFrom;
        this.strGoTo = strGoTo;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rootView = convertView;
        ViewHolder viewHolder = null;

        //technique recycle view
        if (rootView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = ((Activity) getActivity).getLayoutInflater();
            rootView = inflater.inflate(R.layout.item_all_flight, null, true);

            viewHolder.txtFlightNo = (TextView) rootView.findViewById(R.id.txt_all_flight_flight_no);
            viewHolder.txtClass = (TextView) rootView.findViewById(R.id.txt_all_flight_class);
            viewHolder.txtPrice = (TextView) rootView.findViewById(R.id.txt_all_flight_price);
            viewHolder.txtDepart = (TextView) rootView.findViewById(R.id.txt_all_flight_depart_arrive);
            viewHolder.txtLeaveReturn = (TextView) rootView.findViewById(R.id.txt_all_flight_leave_return);


            rootView.setTag(viewHolder);
        }
        viewHolder = (ViewHolder) rootView.getTag();
        if (listAllItem != null) {
            viewHolder.txtFlightNo.setText("Lao Airline | Flight Number " + listAllItem.get(position).get(KEY_FLIGHT_NO));
            viewHolder.txtClass.setText("Class " + listAllItem.get(position).get(KEY_CLASS));
            viewHolder.txtPrice.setText("Prices " + listAllItem.get(position).get(KEY_PRICE));
            viewHolder.txtDepart.setText("Depart: " + listAllItem.get(position).get(KEY_DEPART) + " (" + strLeaveFrom + ")      " +
                    "Arrive: " + listAllItem.get(position).get(KEY_ARRIVE) + " (" + strGoTo + ")");
            viewHolder.txtLeaveReturn.setText(listAllItem.get(position).get(KEY_LEAVE_RETURN) +" Flight");
        }

        return rootView;
    }

    private class ViewHolder {
        private TextView txtFlightNo, txtClass, txtPrice, txtDepart, txtLeaveReturn;
    }

    @Override
    public int getCount() {
        if (listAllItem.isEmpty()) {
            return -1;
        }
        return listAllItem.size();
    }

    @Override
    public HashMap<String, String> getItem(int position) {
        return listAllItem.get(position);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }
}