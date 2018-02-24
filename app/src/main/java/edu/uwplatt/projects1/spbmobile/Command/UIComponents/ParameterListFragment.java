package edu.uwplatt.projects1.spbmobile.Command.UIComponents;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;


import edu.uwplatt.projects1.spbmobile.R;


public class ParameterListFragment extends Fragment {
    public ParameterListFragment() {
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

        return inflater.inflate(R.layout.fragment_parameter_list, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        ListAdapter parameterAdapter = new ParameterListAdapter(getContext(), R.id.parameter_listView);
        View view = getView();
        if (view != null) {
            final ListView listView = view.findViewById(R.id.parameter_listView);
            if (listView != null) {
                listView.setAdapter(parameterAdapter);
            }
        }
    }

    // TODO: Create method to respond to buttons, update argument and hook method into UI event
}