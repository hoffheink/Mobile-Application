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

    /**
     * This method will get a View that is a duration picker.
     *
     * @param max       The maximum number of seconds for the duration.
     * @param container The container the View will be put into.
     * @return The View that represents a duration picker.
     */
    private View getDurationPicker(final int max, @NonNull ViewGroup container) {
        return (new DurationPicker(max)).getView(container);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup container) {
        final Parameter parameter = getItem(position);
        LayoutInflater layoutInflater;
        if (convertView == null) {
            layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
                        frameLayout.addView(getDurationPicker(parameter.range.max, container));
                        Command.setParameterOnCurrentCommand(parameter.machineName, parameter.value = 0);
                        break;
                }
            }
            ((TextView) convertView.findViewById(R.id.parameter_name_textView)).setText(parameter.humanName);
        }
        return convertView;
    }

    /**
     * This class represents a duration picker.
     */
    private class DurationPicker {
        private final int SecondsPerMinute = 60;
        private final int MinutesPerHour = 60;
        private final int SecondsPerHour = 60 * SecondsPerMinute;
        final int maxTime;
        final int minTime;

        /**
         * The constructor.
         *
         * @param max The maximum time (in seconds) the duration can have.
         */
        DurationPicker(int max) {
            maxTime = max;
            minTime = 0;
        }

        View view = null;
        NumberPicker durationHours = null;
        NumberPicker durationMinutes = null;
        NumberPicker durationSeconds = null;
        RelativeLayout hoursRelLayout = null;
        RelativeLayout minutesRelLayout = null;
        RelativeLayout secondsRelLayout = null;

        /**
         * This method will update the displays to appropriately display the number of hours,
         * minutes, and seconds available to choose from.
         */
        private void updateDisplays() {
            if (view != null) {
                setHours();
                setMinutes();
                setSeconds();
            }
        }

        /**
         * This method will set the hour picker to a valid range.
         */
        private void setHours() {
            if (durationHours != null) {
                int timeAvailable = maxTime - durationMinutes.getValue() * SecondsPerMinute - durationSeconds.getValue();
                int newMax = timeAvailable / SecondsPerHour;
                durationHours.setMaxValue(newMax);
                if (newMax == 0)
                    hoursRelLayout.setVisibility(View.GONE);
                else
                    hoursRelLayout.setVisibility(View.VISIBLE);
            }
        }

        /**
         * This method will set the minute picker to a valid range.
         */
        private void setMinutes() {
            if (durationMinutes != null) {
                int timeAvailable = maxTime - durationHours.getValue() * SecondsPerHour - durationSeconds.getValue();
                int newMax;
                if (timeAvailable > 0) {
                    if ((timeAvailable / SecondsPerMinute) >= MinutesPerHour)
                        newMax = MinutesPerHour;
                    else
                        newMax = (timeAvailable / SecondsPerMinute) % MinutesPerHour;
                } else
                    newMax = 0;
                durationMinutes.setMaxValue(newMax);
                if (newMax == 0)
                    minutesRelLayout.setVisibility(View.GONE);
                else
                    minutesRelLayout.setVisibility(View.VISIBLE);
            }
        }

        /**
         * This method will set the second picker to a valid range.
         */
        private void setSeconds() {
            if (durationSeconds != null) {
                int timeAvailable = maxTime - durationHours.getValue() * SecondsPerHour - durationMinutes.getValue() * SecondsPerMinute;
                int newMax;
                if (timeAvailable > 0) {
                    if (timeAvailable >= SecondsPerMinute)
                        newMax = SecondsPerMinute;
                    else
                        newMax = timeAvailable % SecondsPerMinute;
                } else
                    newMax = 0;
                durationSeconds.setMaxValue(newMax);
                if (newMax == 0)
                    secondsRelLayout.setVisibility(View.GONE);
                else
                    secondsRelLayout.setVisibility(View.VISIBLE);
            }
        }

        /**
         * This method will return the View associated with this duration picker.
         *
         * @param container The ViewGroup that this view will be placed in.
         */
        View getView(@NonNull ViewGroup container) {
            LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (layoutInflater == null)
                throw new NullPointerException("Layout Inflater was null");
            view = layoutInflater.inflate(R.layout.duration_picker, container, false);
            setUpPickers();
            return view;
        }

        /**
         * This method will set up the NumberPickers.
         */
        private void setUpPickers() {
            durationHours = view.findViewById(R.id.durationHours);
            durationMinutes = view.findViewById(R.id.durationMinutes);
            durationSeconds = view.findViewById(R.id.durationSeconds);
            hoursRelLayout = view.findViewById(R.id.hoursRelLayout);
            minutesRelLayout = view.findViewById(R.id.minutesRelLayout);
            secondsRelLayout = view.findViewById(R.id.secondsRelLayout);
            durationHours.setMinValue(0);
            durationMinutes.setMinValue(0);
            durationSeconds.setMinValue(0);
            durationHours.setOnValueChangedListener(valueChangeListener);
            durationMinutes.setOnValueChangedListener(valueChangeListener);
            durationSeconds.setOnValueChangedListener(valueChangeListener);
            updateDisplays();
        }

        /**
         * This Listener will call the updateDisplays method when fired.
         */
        NumberPicker.OnValueChangeListener valueChangeListener = new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                updateDisplays();
            }
        };

    }
}
