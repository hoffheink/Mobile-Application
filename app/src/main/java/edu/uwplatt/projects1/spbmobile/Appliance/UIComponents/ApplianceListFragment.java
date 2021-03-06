package edu.uwplatt.projects1.spbmobile.Appliance.UIComponents;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import edu.uwplatt.projects1.spbmobile.Appliance.Appliance;
import edu.uwplatt.projects1.spbmobile.CloudDatasource;
import edu.uwplatt.projects1.spbmobile.Command.UIComponents.CommandListFragment;
import edu.uwplatt.projects1.spbmobile.GoogleProvider;
import edu.uwplatt.projects1.spbmobile.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must
 * handle interaction events.
 */
public class ApplianceListFragment extends Fragment {

    /**
     * This method will create fragment for the list of Appliance.
     *
     * @param inflater           the LayoutInflater used to inflate the view.
     * @param container          the ViewGroup to throw the fragment in.
     * @param savedInstanceState the Bundle (if available).
     * @return the View after inflation.
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
     * This method will load up the Appliances and create the Views.
     */
    @Override
    public void onStart() {
        super.onStart();
        ListAdapter applianceAdapter = new ApplianceListAdapter(getContext(), R.id.appliance_list);
        View view = getView();
        if (view != null) {
            final ListView listView = view.findViewById(R.id.appliance_list);
            if (listView != null) {
                final SwipeRefreshLayout refreshLayout = view.findViewById(R.id.swipeRefreshApplianceList);
                refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        CloudDatasource.getInstance(getContext(), GoogleProvider.getInstance(getContext(), getActivity()).getAccount()).loadAppliances(true);
                        listView.setAdapter(new ApplianceListAdapter(getContext(), R.id.appliance_list));
                        Log.d("onRefresh", "Refreshing");
                        refreshLayout.setRefreshing(false);
                    }
                });
                listView.setAdapter(applianceAdapter);
                setOnClickListener(listView);
            }
        }
    }

    /**
     * This method is used to respond to an Appliance being clicked.
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
