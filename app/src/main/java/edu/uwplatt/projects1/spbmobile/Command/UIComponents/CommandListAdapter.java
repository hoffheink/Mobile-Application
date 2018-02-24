package edu.uwplatt.projects1.spbmobile.Command.UIComponents;

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

import edu.uwplatt.projects1.spbmobile.Command.Command;
import edu.uwplatt.projects1.spbmobile.R;

public class CommandListAdapter extends ArrayAdapter<Command> {

    CommandListAdapter(@NonNull Context context, int resource) {
        super(context, resource);
        addAll();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup container) {
        /*Appliance appliance = getItem(position);

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (layoutInflater == null) {
                throw new NullPointerException("Layout Inflater was null");
            }
            convertView = layoutInflater.inflate(R.layout.appliance_list_item, container, false);
        }

        ImageView applianceType = (convertView.findViewById(R.id.appliance_type));
        Drawable drawable;
        if (appliance != null) {
            switch (appliance.getApplianceType()) {
                case CoffeeMaker:
                    drawable = ContextCompat.getDrawable(getContext(), R.drawable.mug);
                    break;
                default:
                    drawable = ContextCompat.getDrawable(getContext(), R.drawable.question);
                    break;
            }
            applianceType.setImageDrawable(drawable);
            ((TextView) convertView.findViewById(R.id.appliance_status)).setText(appliance.getStatus());

            ((TextView) convertView.findViewById(R.id.appliance_name))
                    .setText(appliance.getName());
        }

        ImageView connectionIndicator = (convertView.findViewById(R.id.connection_indicator));

        if (position % 2 == 0) {
            Drawable connectionConnected = ContextCompat.getDrawable(getContext(), R.drawable.connection_connected);
            connectionIndicator.setImageDrawable(connectionConnected);
        } else {
            Drawable connectionUnconnected = ContextCompat.getDrawable(getContext(), R.drawable.connection);
            connectionIndicator.setImageDrawable(connectionUnconnected);
        }*/
        return convertView;
    }
}
