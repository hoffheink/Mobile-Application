package edu.uwplatt.projects1.spbmobile.Command.UIComponents;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.amazonaws.auth.AWSSessionCredentials;

import edu.uwplatt.projects1.spbmobile.CloudDatasource;
import edu.uwplatt.projects1.spbmobile.Command.Command;
import edu.uwplatt.projects1.spbmobile.GoogleProvider;
import edu.uwplatt.projects1.spbmobile.MainActivity;
import edu.uwplatt.projects1.spbmobile.R;
import edu.uwplatt.projects1.spbmobile.Shadow.AwsIotShadowClient;

/**
 * This class is used to display the list of Parameters to the user.
 */
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

    private void setExecuteButtonOnClickListener(ImageButton executeButton) {
        executeButton.setOnClickListener(executeButtonExecute);
    }

    private View.OnClickListener executeButtonExecute = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            try {
                GoogleProvider googleProvider = GoogleProvider.getInstance(getContext(),
                        getActivity());
                CloudDatasource datasource = CloudDatasource.getInstance(getContext(),
                        googleProvider.getAccount(), MainActivity.region);
                AWSSessionCredentials credentials = datasource.getCredentials();
                AwsIotShadowClient client = AwsIotShadowClient.getInstance(credentials);
                Command.executeCurrentCommand(client,
                        (String) getContext().getText(R.string.appVersion));
                getFragmentManager().popBackStack();
                getFragmentManager().popBackStack();
                Toast.makeText(getContext(),
                        Command.currentCommand.humanName + " Sent Successfully",
                        Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Log.e("executeButtonExecute", e.getMessage(), e);
                Toast.makeText(getContext(),
                        "Failed to execute " + Command.currentCommand.humanName,
                        Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        ListAdapter parameterAdapter = new ParameterListAdapter(getContext(),
                R.id.parameter_listView);
        View view = getView();

        if (view != null) {
            final ListView listView = view.findViewById(R.id.parameter_listView);
            if (listView != null) {
                listView.setAdapter(parameterAdapter);
                final ImageButton executeButton =
                        view.findViewById(R.id.parameter_execute_imageButton);
                if (executeButton != null) {
                    setExecuteButtonOnClickListener(executeButton);
                }
            }
        }
    }
}