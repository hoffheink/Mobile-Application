package edu.uwplatt.projects1.spbmobile.JsonHelpers;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

import edu.uwplatt.projects1.spbmobile.Command.Range;

/**
 * This class assists our Gson parser to handle proper json parsing from an improper json file.
 */
public class RangeTypeAdapter implements JsonSerializer<Range> {
    /**
     * This method serializes a Range object for us.
     */
    @Override
    public JsonElement serialize(Range src, Type typeOfSrc, JsonSerializationContext context) {
        if (src.min == 0 && src.max == 0 && src.step == 0)
            return new JsonArray();
        else
            return (new Gson()).toJsonTree(src);
    }
}