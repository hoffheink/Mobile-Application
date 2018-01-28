package edu.uwplatt.projects1.spbmobile;

import java.util.List;

/**
 * Created by dowster on 12/10/2017.
 */

public class Appliance {
    private String name;
    private String id;
    private String status;
    private List<ICommand> commands;

    public Appliance(String inName, String inId) {
        name = inName;
        id = inId;
        status = "OK";
    }

    public String getName() {
        return name;
    }

    public void setName(String inName) {
        name = inName;
    }

    public String getId() {
        return id;
    }

    public void setId(String inId) {
        id = inId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String inStatus) {
        status = inStatus;
    }
}
