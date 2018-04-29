package edu.uwplatt.projects1.spbmobile.Command.UIComponents;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import edu.uwplatt.projects1.spbmobile.Command.Command;
import edu.uwplatt.projects1.spbmobile.R;

import android.support.v4.app.Fragment;

/**
 * This class is used to display the list of Commands to the user.
 */
public class CommandListFragment extends Fragment {

    /**
     * This method will create fragment for the list of Command.
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
        return inflater.inflate(R.layout.fragment_command_list, container, false);
    }

    /**
     * This method will load up the Commands and create the Views.
     */
    @Override
    public void onStart() {
        super.onStart();
        ListAdapter commandListAdapter = new CommandListAdapter(getContext(), R.id.command_list);
        View view = getView();
        if (view != null) {
            ListView listView = view.findViewById(R.id.command_list);
            if (listView != null) {
                listView.setAdapter(commandListAdapter);
                setOnClickListener(listView);
            }
        }
    }

    /**
     * This method is used to respond to a Command being clicked.
     *
     * @param listView the ListView to attach the onClickListener to.
     */
    private void setOnClickListener(final ListView listView) {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("onItemClick", "Item clicked in CommandListFragment");
                Command.currentCommand = (Command) listView.getItemAtPosition(i);
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.content_main, new ParameterListFragment(),
                        "parameterListFragment");
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
    }
}
