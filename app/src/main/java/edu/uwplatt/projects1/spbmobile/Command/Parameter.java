package edu.uwplatt.projects1.spbmobile.Command;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.google.gson.annotations.SerializedName;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Parameter {
    public static Parameter[] parametersForList;
    public String machinename;
    public String humanName;
    public String description;
    public Types type;
    public Enumeration[] enumerations;
    public Range range;
    public String units;

    public ArrayAdapter<String> getSpinnerOptions(Context context)
    {
        List<String> optionList = new LinkedList<>();
        for (Enumeration enumeration : enumerations)
            optionList.add(enumeration.name);
        String[] strings = optionList.toArray(new String[optionList.size()]);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, strings);
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
