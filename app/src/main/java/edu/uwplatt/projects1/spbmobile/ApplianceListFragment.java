package edu.uwplatt.projects1.spbmobile;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must
 * handle interaction events.
 */
public class ApplianceListFragment extends Fragment {

    public ApplianceListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (container != null) {
            container.removeAllViews();
        }

        return inflater.inflate(R.layout.fragment_appliance_list, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        ListAdapter applianceAdapter = new ApplianceListAdapter(getContext(), R.id.appliance_list);
        View view = getView();
        if (view != null) {
            ListView listView = view.findViewById(R.id.appliance_list);
            if (listView != null) {
                listView.setAdapter(applianceAdapter);
            }
        }
    }
    // TODO: Create method to respond to buttons, update argument and hook method into UI event
}
