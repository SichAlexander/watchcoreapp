package com.boost.watchcore.model;

/**
 * Created by Alex_Jobs on 29.04.2015.
 */
public class Property {
    private String propertyKey;
    private boolean show;
    private String valueString;
    private int valueInt;
    private String title;
    private int logoOnId;
    private int logoOffId;

    public int getValueInt() {
        return valueInt;
    }

    public void setValueInt(int valueInt) {
        this.valueInt = valueInt;
    }

    public int getLogoOnId() {
        return logoOnId;
    }

    public void setLogoOnId(int logoOnId) {
        this.logoOnId = logoOnId;
    }

    public int getLogoOffId() {
        return logoOffId;
    }

    public void setLogoOffId(int logoOffId) {
        this.logoOffId = logoOffId;
    }

    public String getPropertyKey() {
        return propertyKey;
    }

    public void setPropertyKey(String propertyKey) {
        this.propertyKey = propertyKey;
    }

    public boolean isShow() {
        return show;
    }

    public void setShow(boolean show) {
        this.show = show;
    }

    public String getValueString() {
        return valueString;
    }

    public void setValueString(String value) {
        this.valueString = value;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
