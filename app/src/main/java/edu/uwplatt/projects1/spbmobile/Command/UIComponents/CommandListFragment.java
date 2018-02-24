package edu.uwplatt.projects1.spbmobile.Command.UIComponents;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import edu.uwplatt.projects1.spbmobile.Appliance;
import edu.uwplatt.projects1.spbmobile.R;
import android.support.v4.app.Fragment;


public class CommandListFragment extends Fragment {
    public Appliance appliance;

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

        return inflater.inflate(R.layout.fragment_command_list, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        ListAdapter commandListAdapter = new CommandListAdapter(getContext(), R.id.command_list);
        View view = getView();
        if (view != null) {
            ListView listView = view.findViewById(R.id.command_list);
            if (listView != null) {
                listView.setAdapter(commandListAdapter);
            }
        }
    }
    // TODO: Create method to respond to buttons, update argument and hook method into UI event
}
