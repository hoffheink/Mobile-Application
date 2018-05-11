package edu.uwplatt.projects1.spbmobile.Appliance;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import edu.uwplatt.projects1.spbmobile.CloudDatasource;
import edu.uwplatt.projects1.spbmobile.Command.Command;
import edu.uwplatt.projects1.spbmobile.Command.Range;
import edu.uwplatt.projects1.spbmobile.JsonHelpers.RangeTypeAdapter;
import edu.uwplatt.projects1.spbmobile.Lambda.LambdaFunctionNames;
import edu.uwplatt.projects1.spbmobile.MainActivity;

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
    private ApplianceTypes applianceType = ApplianceTypes.Unknown;

    private static Gson applianceGson = constructApplianceGson();

    /**
     * This method creates our Gson object to handle proper json parsing from an improper json file.
     *
     * @return the Gson object.
     */
    @NonNull
    private static Gson constructApplianceGson() {
        return new GsonBuilder().registerTypeAdapter(Range.class, new RangeTypeAdapter()).create();
    }

    public enum ApplianceTypes {
        CoffeeMaker,
        Test,
        Unknown
    }

    /**
     * Constructor.
     *
     * @param name the Appliances name.
     * @param id   the Appliances id.
     */
    public Appliance(@NonNull String name, @NonNull String id) {
        this.name = name;
        this.id = id;
        status = "OK";
        loadCommands();
    }

    /**
     * This method loads the commands into an Appliance.
     */
    private void loadCommands() {
        String inputText = "[ { \"cmdName\": \"EchoCommand\", \"humanName\": \"Echo Command\", \"priority\": false, \"parameters\": [ { \"machineName\": \"echoText\", \"humanName\": \"Echo Text\", \"description\": \"Text that the device sends back to cloud.\", \"type\": \"string\", \"enumerations\": [], \"range\": {}, \"units\": \"\" } ], \"status\": { \"states\": [ { \"value\": 4, \"text\": \"Echoing\" } ] } }, { \"cmdName\": \"BakeCommand\", \"humanName\": \"Bake Command\", \"priority\": false, \"parameters\": [ { \"machineName\": \"temperature\", \"humanName\": \"Temperature\", \"description\": \"Temperature to bake at.\", \"type\": \"int\", \"enumerations\": [], \"range\": { \"min\": 100, \"max\": 600, \"step\": 5 }, \"units\": \"deg F\" }, { \"machineName\": \"mode\", \"humanName\": \"Oven Mode\", \"description\": \"\", \"priority\": false, \"type\": \"enum\", \"enumerations\": [ { \"value\": 1, \"name\": \"Bake\" }, { \"value\": 2, \"name\": \"Broil\" }, { \"value\": 3, \"name\": \"Bake (Convection)\" } ], \"range\": {}, \"units\": \"\" }, { \"machineName\": \"duration\", \"humanName\": \"Duration\", \"description\": \"Time to cook for.\", \"type\": \"duration\", \"enumerations\": [], \"range\": { \"min\": 0, \"max\": 86400, \"step\": 1 }, \"units\": \"\" } ], \"status\": { \"state\": [ { \"value\": 4, \"text\": \"Pre-Heating\" }, { \"value\": 5, \"text\": \"Bake\" }, { \"value\": 6, \"text\": \"Cooling\" } ], \"startTime\": 123 } }]";
        commands = applianceGson.fromJson(inputText, Command[].class);
    }

    /**
     * Gets the name.
     *
     * @return the name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name.
     *
     * @param name the new name.
     */
    public Appliance setName(@NonNull String name) {
        this.name = name;
        return this;
    }

    /**
     * Gets the id.
     *
     * @return the id.
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the id.
     *
     * @param id the new id.
     */
    public Appliance setId(@NonNull String id) {
        this.id = id;
        return this;
    }

    /**
     * Gets the status.
     *
     * @return the status.
     */
    @NonNull
    public String getStatus() {
        return status;
    }

    /**
     * Sets the status.
     *
     * @param status the new status.
     */
    public Appliance setStatus(@NonNull String status) {
        this.status = status;
        return this;
    }

    /**
     * Gets the ApplianceTypes.
     *
     * @return the ApplianceTypes.
     */
    @NonNull
    public ApplianceTypes getApplianceType() {
        return applianceType;
    }

    /**
     * Sets the appliance type.
     *
     * @param applianceType the new appliance type.
     */
    public void setApplianceType(@NonNull ApplianceTypes applianceType) {
        this.applianceType = applianceType;
    }

    public static void RemoveAppliance(GoogleSignInAccount inAccount, Context inContext,
                                       String thingId) {
        RemoveDeviceFormat removeDeviceFormat = new RemoveDeviceFormat(thingId,
                FirebaseInstanceId.getInstance().getToken());
        CloudDatasource.getInstance(inContext, inAccount, MainActivity.region).invokeLambda(
                LambdaFunctionNames.REMOVE_DEVICE, new Gson().toJson(removeDeviceFormat));
    }
}
