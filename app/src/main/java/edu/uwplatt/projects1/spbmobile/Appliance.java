package edu.uwplatt.projects1.spbmobile;

import android.support.annotation.NonNull;

import java.util.List;

/**
 * Created by dowster on 12/10/2017.
 */

/**
 * This class represents an Appliance.
 */
public class Appliance {
    private String name;
    private String id;
    private String status;
    private List<ICommand> commands;
    @NonNull
    private ApplianceType applianceType = ApplianceType.Unknown;

    public enum ApplianceType
    {
        CoffeeMaker,
        Unknown
    }

    /**
     * Constructor.
     * @param inName the Appliances name.
     * @param inId the Appliances id.
     */
    public Appliance(String inName, String inId) {
        name = inName;
        id = inId;
        status = "OK";
    }

    /**
     * Gets the name.
     * @return name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name.
     * @param inName the new name.
     */
    public Appliance setName(String inName) {
        name = inName;
        return this;
    }

    /**
     * Gets the id.
     * @return id.
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the id.
     * @param inId the new id.
     */
    public Appliance setId(String inId) {
        id = inId;
        return this;
    }

    /**
     * Gets the status.
     * @return status.
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the status.
     * @param inStatus the new status.
     */
    public Appliance setStatus(String inStatus) {
        status = inStatus;
        return this;
    }

    /**
     * Gets the appliance type.
     * @return appliance type.
     */
    public ApplianceType getApplianceType() {
        return applianceType;
    }

    /**
     * Sets the appliance type.
     * @param inApplianceType the new appliance type.
     */
    public Appliance setApplianceType(ApplianceType inApplianceType) {
        applianceType = inApplianceType;
        return this;
    }
}
