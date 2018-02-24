package edu.uwplatt.projects1.spbmobile;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.gson.Gson;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import edu.uwplatt.projects1.spbmobile.Command.Command;

/**
 * This class represents an Appliance.
 */
public class Appliance {
    private String name;
    private String id;
    @NonNull
    private String status;
    Command[] commands;
    @NonNull
    private ApplianceType applianceType = ApplianceType.Unknown;

    private static String VersionNumber;

    static void setVersionNumber(String versionNumber)
    {
        VersionNumber = versionNumber;
    }

    /**
     * Appliance types
     */
    public enum ApplianceType {
        CoffeeMaker,
        Unknown
    }

    /**
     * Constructor.
     *
     * @param inName the Appliances name.
     * @param inId   the Appliances id.
     */
    Appliance(String inName, String inId) {
        name = inName;
        id = inId;
        status = "OK";
        loadCommands();
    }

    private void loadCommands()
    {
        String inputSample = "[ { \"cmdName\": \"EchoCommand\", \"humanName\": \"Echo Command\", \"priority\": false, \"parameters\": [ { \"machineName\": \"echoText\", \"humanName\": \"Echo Text\", \"description\": \"Text that the device sends back to cloud.\", \"type\": \"string\", \"enumerations\": [] } ], \"states\": [ { \"value\": 4, \"text\": \"Echoing\" } ] }, { \"cmdName\": \"bakeCommand\", \"humanName\": \"Bake Command\", \"parameters\": [ { \"machineName\": \"temperature\", \"humanName\": \"Temperature\", \"description\": \"Temperature to bake at.\", \"type\": \"int\", \"enumerations\": [], \"range\": { \"min\": 100, \"max\": 600, \"step\": 5 }, \"units\": \"deg F\" }, { \"machineName\": \"mode\", \"humanName\": \"Oven Mode\", \"description\": \"\", \"type\": \"enum\", \"enumerations\": [ { \"value\": 1, \"name\": \"Bake\" }, { \"value\": 2, \"name\": \"Broil\" }, { \"value\": 3, \"name\": \"Bake (Convection)\" } ], \"range\": {}, \"units\": \"\" }, { \"machineName\": \"duration\", \"humanName\": \"Duration\", \"description\": \"Time to cook for.\", \"type\": \"duration\", \"enumerations\": [], \"range\": { \"min\": 0, \"max\": 86400, \"step\": 1 }, \"units\": \"\" } ], \"states\": [ { \"value\": 4, \"text\": \"Pre-Heating\" }, { \"value\": 5, \"text\": \"Bake\" }, { \"value\": 6, \"text\": \"Cooling\" } ] }]";
        Gson gson = new Gson();
        commands = gson.fromJson(inputSample, Command[].class);
        //commands.addAll(Arrays.asList(command));
        String json = gson.toJson(commands);
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
    String getStatus() {
        return status;
    }

    /**
     * Sets the status.
     *
     * @param inStatus the new status.
     */
    public Appliance setStatus(@NonNull String inStatus) {
        status = inStatus;
        return this;
    }

    /**
     * Gets the appliance type.
     *
     * @return appliance type.
     */
    @NonNull
    ApplianceType getApplianceType() {
        return applianceType;
    }

    /**
     * Sets the appliance type.
     *
     * @param inApplianceType the new appliance type.
     */
    void setApplianceType(@NonNull ApplianceType inApplianceType) {
        applianceType = inApplianceType;
    }
}
