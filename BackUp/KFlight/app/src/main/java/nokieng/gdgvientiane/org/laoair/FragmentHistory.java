package nokieng.gdgvientiane.org.laoair;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentHistory extends Fragment {


    public FragmentHistory() {
        // Required empty public constructor
    }

    public static FragmentHistory newInstance() {
        Bundle bundle = new Bundle();
        return new FragmentHistory();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_history, container, false);
        return rootView;
    }


}
