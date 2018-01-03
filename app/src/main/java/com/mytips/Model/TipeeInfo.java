package com.mytips.Model;

/**
 * Created by Beesolver on 06-10-2017.
 */

public class TipeeInfo {
    String name;
    String percentage;
    String id;
    boolean is_deleted;
    boolean is_checkedInAddDay;
    public boolean isIs_checkedInAddDay() {
        return is_checkedInAddDay;
    }

    public void setIs_checkedInAddDay(boolean is_checkedInAddDay) {
        this.is_checkedInAddDay = is_checkedInAddDay;
    }



    public boolean isIs_deleted() {
        return is_deleted;
    }

    public void setIs_deleted(boolean is_deleted) {
        this.is_deleted = is_deleted;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPercentage() {
        return percentage;
    }

    public void setPercentage(String percentage) {
        this.percentage = percentage;
    }


}
