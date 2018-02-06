package edu.uwplatt.projects1.spbmobile;

import android.support.annotation.NonNull;

import java.util.List;

/**
 * This class represents an Appliance.
 */
public class Appliance {
    private String name;
    private String id;
    @NonNull
    private String status;
    private List<ICommand> commands;
    @NonNull
    private ApplianceType applianceType = ApplianceType.Unknown;


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
