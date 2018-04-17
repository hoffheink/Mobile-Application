package edu.uwplatt.projects1.spbmobile.Appliance;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import edu.uwplatt.projects1.spbmobile.Command.Command;
import edu.uwplatt.projects1.spbmobile.Command.Range;
import edu.uwplatt.projects1.spbmobile.JsonHelpers.RangeTypeAdapter;

/**
 * This class represents an Appliance.
 */
public class Appliance {
    public static Appliance currentAppliance;
    private String name;
    private String id;
    @NonNull
    private String status;
    public Command[] commands;
    @NonNull
    private ApplianceType applianceType = ApplianceType.Unknown;

    private static Gson applianceGson = constructApplianceGson();

    @NonNull
    private static Gson constructApplianceGson() {
        return new GsonBuilder().registerTypeAdapter(Range.class, new RangeTypeAdapter()).create();
    }

    /**
     * Appliance types
     */
    public enum ApplianceType {
        CoffeeMaker,
        Test,
        Unknown
    }

    /**
     * Constructor.
     *
     * @param inName the Appliances name.
     * @param inId   the Appliances id.
     */
    public Appliance(String inName, String inId) {
        name = inName;
        id = inId;
        status = "OK";
        loadCommands();
    }

    private void loadCommands() {
        String inputText = "[ { \"cmdName\": \"EchoCommand\", \"humanName\": \"Echo Command\", \"priority\": false, \"parameters\": [ { \"machineName\": \"echoText\", \"humanName\": \"Echo Text\", \"description\": \"Text that the device sends back to cloud.\", \"type\": \"string\", \"enumerations\": [], \"range\": {}, \"units\": \"\" } ], \"status\": { \"states\": [ { \"value\": 4, \"text\": \"Echoing\" } ] } }, { \"cmdName\": \"bakeCommand\", \"humanName\": \"Bake Command\", \"priority\": false, \"parameters\": [ { \"machineName\": \"temperature\", \"humanName\": \"Temperature\", \"description\": \"Temperature to bake at.\", \"type\": \"int\", \"enumerations\": [], \"range\": { \"min\": 100, \"max\": 600, \"step\": 5 }, \"units\": \"deg F\" }, { \"machineName\": \"mode\", \"humanName\": \"Oven Mode\", \"description\": \"\", \"priority\": false, \"type\": \"enum\", \"enumerations\": [ { \"value\": 1, \"name\": \"Bake\" }, { \"value\": 2, \"name\": \"Broil\" }, { \"value\": 3, \"name\": \"Bake (Convection)\" } ], \"range\": {}, \"units\": \"\" }, { \"machineName\": \"duration\", \"humanName\": \"Duration\", \"description\": \"Time to cook for.\", \"type\": \"duration\", \"enumerations\": [], \"range\": { \"min\": 0, \"max\": 86400, \"step\": 1 }, \"units\": \"\" } ], \"status\": { \"state\": [ { \"value\": 4, \"text\": \"Pre-Heating\" }, { \"value\": 5, \"text\": \"Bake\" }, { \"value\": 6, \"text\": \"Cooling\" } ], \"startTime\": 123 } }]";
        commands = applianceGson.fromJson(inputText, Command[].class);
    }

    /**
     * Gets the name.
     *
     * @return name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name.
     *
     * @param inName the new name.
     */
    public Appliance setName(String inName) {
        name = inName;
        return this;
    }

    /**
     * Gets the id.
     *
     * @return id.
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the id.
     *
     * @param inId the new id.
     */
    public Appliance setId(String inId) {
        id = inId;
        return this;
    }

    /**
     * Gets the status.
     *
     * @return status.
     */
    @NonNull
    public String getStatus() {
        return status;
    }

    /**
     * Gets the appliance type.
     *
     * @return appliance type.
     */
    @NonNull
    public ApplianceType getApplianceType() {
        return applianceType;
    }

    /**
     * Sets the appliance type.
     *
     * @param inApplianceType the new appliance type.
     */
    public void setApplianceType(@NonNull ApplianceType inApplianceType) {
        applianceType = inApplianceType;
    }
}
