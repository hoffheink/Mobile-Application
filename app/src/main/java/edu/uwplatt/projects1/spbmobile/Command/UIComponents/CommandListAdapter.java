package edu.uwplatt.projects1.spbmobile.Command.UIComponents;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import edu.uwplatt.projects1.spbmobile.Appliance.Appliance;
import edu.uwplatt.projects1.spbmobile.Command.Command;
import edu.uwplatt.projects1.spbmobile.R;

/**
 * This class is used to load the Commands from the current Appliance.
 */
public class CommandListAdapter extends ArrayAdapter<Command> {

    /**
     * This is the constructor for the CommandListAdapter.
     *
     * @param context  The context for the adapter.
     * @param resource the resource used for drawing.
     */
    CommandListAdapter(@NonNull Context context, int resource) {
        super(context, resource);
        addAll(Appliance.currentAppliance.commands);
    }

    /**
     * This method gets the view for the Command view.
     *
     * @param position    Its index.
     * @param convertView The area to put the info.
     * @param container   The container the convertView will go into.
     * @return The View of the Command.
     */
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup container) {
        Command command = getItem(position);

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (layoutInflater == null) {
                throw new NullPointerException("Layout Inflater was null");
            }
            convertView = layoutInflater.inflate(R.layout.command_list_item, container,
                    false);
        }

        if (command != null)
            ((TextView) convertView.findViewById(R.id.command_name)).setText(command.humanName);
        return convertView;
    }
}
