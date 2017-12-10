package edu.uwplatt.projects1.spbmobile;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by dowster on 12/9/2017.
 */

public class ApplianceListAdapter extends ArrayAdapter {
    /**
     * Testing things
     * @param context
     * @param resource
     */
    public ApplianceListAdapter(@NonNull Context context, int resource) {
        super(context, resource);

        this.addAll(CloudDatasource.getInstance().getDevices());
    }

    public ApplianceListAdapter(@NonNull Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
    }

    public ApplianceListAdapter(@NonNull Context context, int resource, @NonNull Object[] objects) {
        super(context, resource, objects);
    }

    public ApplianceListAdapter(@NonNull Context context, int resource, int textViewResourceId, @NonNull Object[] objects) {
        super(context, resource, textViewResourceId, objects);
    }

    public ApplianceListAdapter(@NonNull Context context, int resource, @NonNull List objects) {
        super(context, resource, objects);
    }

    public ApplianceListAdapter(@NonNull Context context, int resource, int textViewResourceId, @NonNull List objects) {
        super(context, resource, textViewResourceId, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup container) {
        if (convertView == null) {
            convertView = ((LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.appliance_list_item, container, false);
        }


        ImageView connectionIndicator = ((ImageView)convertView.findViewById(R.id.connection_indicator));

        if(position % 2 == 0)
        {
            Drawable connectionConnected = ContextCompat.getDrawable(getContext(), R.drawable.connection_connected);
            connectionIndicator.setImageDrawable(connectionConnected);
            ((TextView) convertView.findViewById(R.id.appliance_status))
                    .setText("Connected");
        } else {
            Drawable connectionUnconnected = ContextCompat.getDrawable(getContext(), R.drawable.connection);
            connectionIndicator.setImageDrawable(connectionUnconnected);
            ((TextView) convertView.findViewById(R.id.appliance_status))
                    .setText("Unconnected");
        }

        ((TextView) convertView.findViewById(R.id.appliance_name))
                .setText(getItem(position).toString());
        return convertView;
    }
}
