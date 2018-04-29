package edu.uwplatt.projects1.spbmobile.Command;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.google.gson.annotations.SerializedName;

import java.util.LinkedList;
import java.util.List;

/**
 * This class represents a parameter element.
 */
public class Parameter {
    public String machineName;
    public String humanName;
    public String description;
    public Types type;
    public Enumeration[] enumerations;
    public Range range;
    public String units;

    public Object value;

    /**
     * This method gets an ArrayAdapter of Strings containing the proper human names of the values
     * that could be sent.
     *
     * @param context the Application Context.
     * @return the ArrayAdapter of Strings.
     */
    public ArrayAdapter<String> getSpinnerOptions(Context context) {
        List<String> optionList = new LinkedList<>();
        for (Enumeration enumeration : enumerations)
            optionList.add(enumeration.name);
        String[] strings = optionList.toArray(new String[optionList.size()]);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context,
                android.R.layout.simple_spinner_item, strings);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return arrayAdapter;
    }

    public enum Types {
        @SerializedName("int")
        IntType,
        @SerializedName("string")
        StringType,
        @SerializedName("enum")
        EnumType,
        @SerializedName("duration")
        DurationType
    }
}
