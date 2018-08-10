package me.fmtech.apollo.model.bean;

import com.slamtec.slamware.robot.Location;

public class LocationBean extends Location {
    private String name;

    public LocationBean(Location location) {
        super(location);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubName() {
        return getX() + ", " + getY() + ", " + getZ();
    }
}
