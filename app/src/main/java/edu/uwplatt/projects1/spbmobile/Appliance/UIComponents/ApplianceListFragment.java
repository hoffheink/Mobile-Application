package edu.uwplatt.projects1.spbmobile.Appliance.UIComponents;

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

import edu.uwplatt.projects1.spbmobile.Appliance.Appliance;
import edu.uwplatt.projects1.spbmobile.Command.UIComponents.CommandListFragment;
import edu.uwplatt.projects1.spbmobile.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must
 * handle interaction events.
 */
public class ApplianceListFragment extends Fragment {
    /**
     * The required default constructor.
     */
    //TODO: Find out if we need this
    public ApplianceListFragment() {
    }

    /**
     * This method will create fragment for the list of Appliance.
     *
     * @param inflater           the inflater to inflate the view.
     * @param container          the container to throw the fragment in.
     * @param savedInstanceState The saved instance (if available).
     * @return The view after inflation.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (container != null) {
            container.removeAllViews();
        }

        return inflater.inflate(R.layout.fragment_appliance_list, container, false);
    }

    /**
     * This method will load up the Appliances and create the views.
     */
    @Override
    public void onStart() {
        super.onStart();
        ListAdapter applianceAdapter = new ApplianceListAdapter(getContext(), R.id.appliance_list);
        View view = getView();
        if (view != null) {
            final ListView listView = view.findViewById(R.id.appliance_list);
            if (listView != null) {
                listView.setAdapter(applianceAdapter);
                setOnClickListener(listView);
            }
        }
    }

    /**
     * This method is used to respond to an item being clicked.
     *
     * @param listView the ListView to attach the onClickListener to.
     */
    private void setOnClickListener(final ListView listView) {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.wtf("onItemClick", "Item clicked");
                Appliance appliance = (Appliance) listView.getItemAtPosition(i);
                CommandListFragment commandListFragment = new CommandListFragment();

                Appliance.currentAppliance = appliance;

                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.content_main, commandListFragment,
                        "newFragTag");
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
    }
}
