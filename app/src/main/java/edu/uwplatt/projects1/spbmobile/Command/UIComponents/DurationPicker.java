package edu.uwplatt.projects1.spbmobile.Command.UIComponents;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;

import edu.uwplatt.projects1.spbmobile.Command.Command;
import edu.uwplatt.projects1.spbmobile.R;

/**
 * This class represents a duration picker.
 */
class DurationPicker {
    private final int SecondsPerMinute = 60;
    private final int MinutesPerHour = 60;
    private final int SecondsPerHour = 60 * SecondsPerMinute;
    private final int maxTime;

    /**
     * The constructor.
     *
     * @param max the maximum time (in seconds) the duration can have.
     */
    DurationPicker(int max) {
        maxTime = max;
    }

    private View view = null;
    private NumberPicker durationHours = null;
    private NumberPicker durationMinutes = null;
    private NumberPicker durationSeconds = null;
    private RelativeLayout hoursRelLayout = null;
    private RelativeLayout minutesRelLayout = null;
    private RelativeLayout secondsRelLayout = null;

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
            int timeAvailable = maxTime - durationMinutes.getValue() * SecondsPerMinute
                    - durationSeconds.getValue();
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
            int timeAvailable = maxTime - durationHours.getValue() * SecondsPerHour
                    - durationSeconds.getValue();
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
            int timeAvailable = maxTime - durationHours.getValue() * SecondsPerHour
                    - durationMinutes.getValue() * SecondsPerMinute;
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
     * This method will return the View associated with this DurationPicker.
     *
     * @param container the ViewGroup that this view will be placed in.
     * @param context  the Application Context.
     */
    View getView(@NonNull ViewGroup container, Context context) {
        LayoutInflater layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
     * This listener will call the updateDisplays method when fired.
     */
    private NumberPicker.OnValueChangeListener valueChangeListener =
            new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                    updateDisplays();
                    int timeInSeconds = durationHours.getValue() * SecondsPerHour +
                            durationMinutes.getValue() * SecondsPerMinute +
                            durationSeconds.getValue();
                    Command.setParameterOnCurrentCommand("duration", timeInSeconds);
                }
            };
}