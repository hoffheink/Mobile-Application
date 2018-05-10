package edu.uwplatt.projects1.spbmobile.Appliance.UIComponents;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import edu.uwplatt.projects1.spbmobile.Appliance.Appliance;
import edu.uwplatt.projects1.spbmobile.CloudDatasource;
import edu.uwplatt.projects1.spbmobile.R;

/**
 * This class is used to convert a list of Appliances to an ArrayAdapter of Appliances.
 */
public class ApplianceListAdapter extends ArrayAdapter<Appliance> {

    /**
     * This is the constructor for the ApplianceListAdapter.
     *
     * @param context  the Application Context.
     * @param resource the resource used for drawing.
     */
    ApplianceListAdapter(@NonNull Context context, int resource) {
        super(context, resource);
        addAll(CloudDatasource.applianceList);
    }

    /**
     * This method gets the view for the Appliance view.
     *
     * @param position    the index of the Appliance.
     * @param convertView the View to put the info.
     * @param container   the ViewGroup the convertView will go into.
     * @return the View of the Appliance.
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull final ViewGroup container) {
        final Appliance appliance = getItem(position);

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater)
                    getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (layoutInflater == null) {
                throw new NullPointerException("Layout Inflater was null");
            }
            convertView = layoutInflater.inflate(R.layout.appliance_list_item, container,
                    false);
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
            ((TextView) convertView.findViewById(R.id.appliance_status))
                    .setText(appliance.getStatus());

            ((TextView) convertView.findViewById(R.id.appliance_name))
                    .setText(appliance.getName());
        }

        ImageView connectionIndicator = (convertView.findViewById(R.id.connection_indicator));

        if (position % 2 == 0) {
            Drawable connectionConnected = ContextCompat.getDrawable(getContext(),
                    R.drawable.connection_connected);
            connectionIndicator.setImageDrawable(connectionConnected);
        } else {
            Drawable connectionUnconnected = ContextCompat.getDrawable(getContext(),
                    R.drawable.connection);
            connectionIndicator.setImageDrawable(connectionUnconnected);
        }
        convertView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                MenuInflater menuInflater = new MenuInflater(getContext());
                menuInflater.inflate(R.menu.appliance_context_menu, contextMenu);
            }
        });

        convertView.setOnContextClickListener(new View.OnContextClickListener() {
            @Override
            public boolean onContextClick(View view) {
                Log.d("", "trying to get this to appear");
                return false;
            }
        });
        return convertView;
    }
}