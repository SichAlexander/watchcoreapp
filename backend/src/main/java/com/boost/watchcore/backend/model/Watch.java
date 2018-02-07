package com.boost.watchcore.backend.model;

/**
 * Created by BruSD on 16.04.2015.
 */
public class Watch {

    private long id;
    private String name;
    private String iconeURL;
    private String imageFolderURL;
    private String description;
    private long timeStamp;
    private String packageName;
    private String actionName;
    private Boolean isFree;

    public Watch(){

    }

    public void setId(long id) {
        this.id = id;
    }
    public long getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }

    public void setIconeURL(String iconeURL) {
        this.iconeURL = iconeURL;
    }
    public String getIconeURL() {
        return iconeURL;
    }

    public void setImageFolderURL(String imageFolderURL) {
        this.imageFolderURL = imageFolderURL;
    }
    public String getImageFolderURL() {
        return imageFolderURL;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public String getDescription() {
        return description;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
    public long getTimeStamp() {
        return timeStamp;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
    public String getPackageName() {
        return packageName;
    }

    public void setIsFree(Boolean isFree) {
        this.isFree = isFree;
    }
    public Boolean getIsFree() {
        return isFree;
    }

    public String getActionName() {
        return actionName;
    }
    public void setActionName(String actionName) {
        this.actionName = actionName;
    }
}
