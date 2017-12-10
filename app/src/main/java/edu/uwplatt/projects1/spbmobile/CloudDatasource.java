package edu.uwplatt.projects1.spbmobile;

/**
 * Created by dowster on 12/9/2017.
 */

class CloudDatasource {
    private static final CloudDatasource ourInstance = new CloudDatasource();

    private String[] appliances = {"One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten", "Eleven", "Twelve", "Thirteen"};

    static CloudDatasource getInstance() {
        return ourInstance;
    }

    private CloudDatasource() {
    }

    public String[] getDevices() {
        return appliances.clone();
    }
}
