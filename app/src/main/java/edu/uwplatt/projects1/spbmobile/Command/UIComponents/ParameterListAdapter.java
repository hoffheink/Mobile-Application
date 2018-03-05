package edu.uwplatt.projects1.spbmobile.Command.UIComponents;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import edu.uwplatt.projects1.spbmobile.Command.Command;
import edu.uwplatt.projects1.spbmobile.Command.Enumeration;
import edu.uwplatt.projects1.spbmobile.Command.Parameter;
import edu.uwplatt.projects1.spbmobile.Command.Range;
import edu.uwplatt.projects1.spbmobile.R;

/**
 * This class is used to load the Parameter from the current Command.
 */
public class ParameterListAdapter extends ArrayAdapter<Parameter> {
    ParameterListAdapter(@NonNull Context context, int resource) {
        super(context, resource);
        addAll(Command.currentCommand.parameters);
    }

    private NumberPicker getNumberPicker(final Parameter parameter) {
        final Range range = parameter.range;
        NumberPicker result = new NumberPicker(getContext());
        result.setMinValue(0);
        result.setMaxValue(range.getDisplayableValues().length - 1);
        result.setDisplayedValues(range.getDisplayableValues());
        result.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldValue, int newValue) {
                int value = newValue * range.step + range.min;
                Command.setParameterOnCurrentCommand(parameter.machineName, value);
            }
        });
        return result;
    }

    private EditText getEditText(final Parameter parameter) {
        EditText result = new EditText(getContext());
        result.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Command.setParameterOnCurrentCommand(parameter.machineName, charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        return result;
    }

    private Spinner getSpinner(final Parameter parameter) {
        Spinner result = new Spinner(getContext());
        result.setAdapter(parameter.getSpinnerOptions(getContext()));
        result.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                for (Enumeration enumeration : parameter.enumerations)
                    Command.setParameterOnCurrentCommand(parameter.humanName, enumeration.value);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        result.setSelection(0);
        return result;
    }

    private DurationPicker getDurationPicker(Parameter parameter) {
        DurationPicker result = new DurationPicker(getContext());
        return result;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup container) {
        final Parameter parameter = getItem(position);
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
                        frameLayout.addView(getNumberPicker(parameter));
                        Command.setParameterOnCurrentCommand(parameter.machineName, parameter.range.min);
                        break;
                    case StringType:
                        Log.d("getView", "Adding EditText");
                        frameLayout.addView(getEditText(parameter));
                        Command.setParameterOnCurrentCommand(parameter.machineName, "");
                        break;
                    case EnumType:
                        Log.d("getView", "EnumType");
                        frameLayout.addView(getSpinner(parameter));
                        Command.setParameterOnCurrentCommand(parameter.machineName, parameter.enumerations[0].value);
                        break;
                    case DurationType:
                        Log.d("getView", "DurationType");
                        frameLayout.addView(getDurationPicker(parameter));
                        TextView durationTypeTextView = new TextView(getContext());
                        durationTypeTextView.setText(R.string.not_yet_implemented);
                        frameLayout.addView(durationTypeTextView);
                        break;
                }
            }
            ((TextView) convertView.findViewById(R.id.parameter_name_textView)).setText(parameter.humanName);
        }
        return convertView;
    }



    private class DurationPicker extends RelativeLayout {
        private NumberPicker hours;
        private NumberPicker minutes;
        private NumberPicker seconds;

        /**
         * The maximum time in seconds
         */
        private int maxTime = 3600;

        public DurationPicker(Context context) {
            super(context);
            hours = new NumberPicker(context);
            minutes = new NumberPicker(context);
            seconds = new NumberPicker(context);
            LayoutParams layoutParams = new LayoutParams()
            hours.setLayoutParams();
            this.addView(hours);
            this.addView(minutes);
            this.addView(seconds);
        }

    }
}
