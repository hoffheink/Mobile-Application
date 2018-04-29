package edu.uwplatt.projects1.spbmobile.Command;

/**
 * This class represents a Range element
 */
public class Range {
    public int min;
    public int max;
    public int step;

    /**
     * This method returns an array of Strings to be displayed in a spinner.
     *
     * @return an array of Strings to be displayed in a spinner.
     */
    public String[] getDisplayableValues() {
        String[] valueSet = new String[((max - min) / step) + 1];
        for (int i = min; i <= max; i += step)
            valueSet[(i - min) / step] = String.valueOf(i);
        return valueSet;
    }
}
