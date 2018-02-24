package edu.uwplatt.projects1.spbmobile.Command.UIComponents;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;

import edu.uwplatt.projects1.spbmobile.Command.Command;
import edu.uwplatt.projects1.spbmobile.Command.Enumeration;
import edu.uwplatt.projects1.spbmobile.Command.Parameter;
import edu.uwplatt.projects1.spbmobile.R;

public class ParameterListAdapter extends ArrayAdapter<Parameter> {
    ParameterListAdapter(@NonNull Context context, int resource) {
        super(context, resource);
        addAll(Command.currentCommand.parameters);
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
                        Log.d("getView", "Adding NumberPicker");
                        NumberPicker intTypeNumberPicker = new NumberPicker(getContext());
                        intTypeNumberPicker.setMinValue(0);
                        intTypeNumberPicker.setMaxValue(parameter.range.getDisplayableValues().length - 1);
                        intTypeNumberPicker.setDisplayedValues(parameter.range.getDisplayableValues());
                        intTypeNumberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                            @Override
                            public void onValueChange(NumberPicker numberPicker, int oldValue, int newValue) {
                                int value = newValue * parameter.range.step + parameter.range.min;
                                Command.setParameterOnCurrentCommand(parameter.machineName, value);
                            }
                        });
                        frameLayout.addView(intTypeNumberPicker);
                        Command.setParameterOnCurrentCommand(parameter.machineName, parameter.range.min);
                        break;
                    case StringType:
                        Log.d("getView", "Adding EditText");
                        EditText stringTypeEditText = new EditText(getContext());
                        stringTypeEditText.addTextChangedListener(new TextWatcher() {
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
                        frameLayout.addView(stringTypeEditText);
                        Command.setParameterOnCurrentCommand(parameter.machineName, "");
                        break;
                    case EnumType:
                        Log.d("getView", "EnumType");
                        final Spinner spinner = new Spinner(getContext());
                        spinner.setAdapter(parameter.getSpinnerOptions(getContext()));
                        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                for (Enumeration enumeration : parameter.enumerations)
                                    Command.setParameterOnCurrentCommand(parameter.humanName, enumeration.value);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {
                            }
                        });
                        spinner.setSelection(0);
                        frameLayout.addView(spinner);
                        Command.setParameterOnCurrentCommand(parameter.machineName, parameter.enumerations[0].value);
                        break;
                    case DurationType:
                        Log.d("getView", "DurationType");
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
}
