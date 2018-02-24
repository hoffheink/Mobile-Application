package edu.uwplatt.projects1.spbmobile.Command;

public class Range {
    public int min;
    public int max;
    public int step;

    public String[] getDisplayableValues() {
        String[] valueSet = new String[((max - min) / step) + 1];
        for (int i = min; i <= max; i += step)
            valueSet[(i - min) / step] = String.valueOf(i);
        return valueSet;
    }
}
