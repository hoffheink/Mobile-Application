package edu.uwplatt.projects1.spbmobile.Command.UIComponents;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import edu.uwplatt.projects1.spbmobile.Appliance;
import edu.uwplatt.projects1.spbmobile.Command.Command;
import edu.uwplatt.projects1.spbmobile.R;

public class CommandListAdapter extends ArrayAdapter<Command> {

    CommandListAdapter(@NonNull Context context, int resource) {
        super(context, resource);
        addAll(Appliance.currentAppliance.commands);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup container) {
        Command command = getItem(position);

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (layoutInflater == null) {
                throw new NullPointerException("Layout Inflater was null");
            }
            convertView = layoutInflater.inflate(R.layout.command_list_item, container, false);
        }

        Drawable drawable;
        if (command != null) {
            ((TextView) convertView.findViewById(R.id.command_name)).setText(command.humanName);
            //((TextView) convertView.findViewById(R.id.appliance_status)).setText(appliance.getStatus());

            //((TextView) convertView.findViewById(R.id.appliance_name)).setText(appliance.getName());
        }
        return convertView;
    }
}
