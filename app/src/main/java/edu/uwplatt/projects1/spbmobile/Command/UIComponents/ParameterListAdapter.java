package edu.uwplatt.projects1.spbmobile.Command.UIComponents;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.LinkedList;
import java.util.List;

import edu.uwplatt.projects1.spbmobile.Command.Parameter;
import edu.uwplatt.projects1.spbmobile.R;

public class ParameterListAdapter extends ArrayAdapter<Parameter> {
    ParameterListAdapter(@NonNull Context context, int resource) {
        super(context, resource);
        addAll(Parameter.parametersForList);
    }

    static int count = 0;

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup container) {
        Parameter parameter = getItem(position);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (layoutInflater == null) {
                throw new NullPointerException("Layout Inflater was null");
            }
            convertView = layoutInflater.inflate(R.layout.parameter_list_item, container, false);
        }
        if (parameter != null) {
            FrameLayout frameLayout = convertView.findViewById(R.id.parameter_content_frameLayout);
            if (frameLayout.getChildCount() == 0) {
                switch (parameter.type) {
                    case IntType:
                        Log.d("getView", "Adding NumberPicker");
                        NumberPicker intTypeNumberPicker = new NumberPicker(getContext());
                        intTypeNumberPicker.setMinValue(0);
                        intTypeNumberPicker.setMaxValue(parameter.range.getDisplayableValues().length - 1);
                        intTypeNumberPicker.setDisplayedValues(parameter.range.getDisplayableValues());
                        frameLayout.addView(intTypeNumberPicker);
                        break;
                    case StringType:
                        Log.d("getView", "Adding EditText");
                        EditText editText = new EditText(getContext());
                        frameLayout.addView(editText);
                        break;
                    case EnumType:
                        Log.d("getView", "EnumType");
                        Spinner spinner = new Spinner(getContext());
                        spinner.setAdapter(parameter.getSpinnerOptions(getContext()));
                        spinner.setSelection(0);
                        frameLayout.addView(spinner);
                        break;
                    case DurationType:
                        Log.d("getView", "DurationType");
                        TextView durationTypeTextView = new TextView(getContext());
                        durationTypeTextView.setText(R.string.not_yet_implemented);
                        frameLayout.addView(durationTypeTextView);
                        break;
                    default:
                }
            }
            ((TextView) convertView.findViewById(R.id.parameter_name_textView)).setText(parameter.humanName + count++);
        }
        return convertView;
    }
}
