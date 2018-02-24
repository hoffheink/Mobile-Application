package edu.uwplatt.projects1.spbmobile;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import edu.uwplatt.projects1.spbmobile.Command.UIComponents.CommandListFragment;


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
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Log.wtf("onItemClick", "Item clicked");
                        Appliance appliance = (Appliance) adapterView.getSelectedItem();
                        CommandListFragment commandListFragment = new CommandListFragment();
                        commandListFragment.appliance = appliance;

                        FragmentManager fragmentManager = getFragmentManager();
                        fragmentManager.popBackStack();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                        fragmentTransaction.add(commandListFragment, "tag");
                        fragmentTransaction.commit();
                    }
                });
            }
        }
    }

    // TODO: Create method to respond to buttons, update argument and hook method into UI event
}
