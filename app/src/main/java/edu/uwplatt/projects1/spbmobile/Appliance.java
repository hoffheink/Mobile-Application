package edu.uwplatt.projects1.spbmobile;

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
    public void setName(String inName) {
        name = inName;
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
    public void setId(String inId) {
        id = inId;
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
    public void setStatus(String inStatus) {
        status = inStatus;
    }
}
