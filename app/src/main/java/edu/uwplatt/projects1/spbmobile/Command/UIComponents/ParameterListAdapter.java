package edu.uwplatt.projects1.spbmobile.Command.UIComponents;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
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

    /**
     * This method gets a NumberPicker from a Parameter.
     *
     * @param parameter the Parameter to create the NumberPicker from.
     * @return the new NumberPicker.
     */
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

    /**
     * This method gets a EditText from a Parameter.
     *
     * @param parameter the Parameter to create the EditText from.
     * @return the new EditText.
     */
    private EditText getEditText(final Parameter parameter) {
        EditText result = new EditText(getContext());
        result.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Command.setParameterOnCurrentCommand(parameter.machineName,
                        charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        return result;
    }

    /**
     * This method gets a Spinner from a Parameter.
     *
     * @param parameter the Parameter to create the Spinner from.
     * @return the new Spinner.
     */
    private Spinner getSpinner(final Parameter parameter) {
        Spinner result = new Spinner(getContext());
        result.setAdapter(parameter.getSpinnerOptions(getContext()));
        result.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Spinner spinner = (Spinner) adapterView;
                if (spinner != null) {
                    String selectedItem = spinner.getSelectedItem().toString();
                    for (Enumeration enumeration : parameter.enumerations)
                        if (selectedItem.equals(enumeration.name)) {
                            Command.setParameterOnCurrentCommand(parameter.machineName,
                                    enumeration.value);
                            break;
                        }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        result.setSelection(0);
        return result;
    }

    /**
     * This method will get a View that is a duration picker.
     *
     * @param max       The maximum number of seconds for the duration.
     * @param container The container the View will be put into.
     * @return The View that represents a duration picker.
     */
    private View getDurationPicker(final int max, @NonNull ViewGroup container) {
        return (new DurationPicker(max)).getView(container, getContext());
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
        final Parameter parameter = getItem(position);
        LayoutInflater layoutInflater;
        if (convertView == null) {
            layoutInflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (layoutInflater == null) {
                throw new NullPointerException("Layout Inflater was null");
            }
            convertView = layoutInflater.inflate(R.layout.parameter_list_item, container,
                    false);
        }
        if (parameter != null) {
            FrameLayout frameLayout = convertView.findViewById(R.id.parameter_content_frameLayout);
            if (frameLayout.getChildCount() == 0) {
                switch (parameter.type) {
                    case IntType:
                        frameLayout.addView(getNumberPicker(parameter));
                        Command.setParameterOnCurrentCommand(parameter.machineName,
                                parameter.range.min);
                        break;
                    case StringType:
                        frameLayout.addView(getEditText(parameter));
                        Command.setParameterOnCurrentCommand(parameter.machineName, "");
                        break;
                    case EnumType:
                        frameLayout.addView(getSpinner(parameter));
                        Command.setParameterOnCurrentCommand(parameter.machineName,
                                parameter.enumerations[0].value);
                        break;
                    case DurationType:
                        frameLayout.addView(getDurationPicker(parameter.range.max, container));
                        Command.setParameterOnCurrentCommand(parameter.machineName,
                                parameter.value = 0);
                        break;
                }
            }
            ((TextView) convertView.findViewById(R.id.parameter_name_textView))
                    .setText(parameter.humanName);
        }
        return convertView;
    }
}