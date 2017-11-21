package com.mytips.Model;

import java.io.Serializable;

/**
 * Created by Beesolver on 09-10-2017.
 */

public class Profiles implements Serializable {

    int profile_color;

    public int getProfile_color() {
        return profile_color;
    }

    public void setProfile_color(int profile_color) {
        this.profile_color = profile_color;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    int id;
    String profile_id;
    String profile_name;
    int is_supervisor;
    int get_tournamenttip;
    int get_tips;
    String startday;
    String holiday_pay;
    String tipees_name;
    int is_active;
    String pay_period;
    String hourly_pay;

    public String getProfile_pic() {
        return profile_pic;
    }

    public void setProfile_pic(String profile_pic) {
        this.profile_pic = profile_pic;
    }

    String profile_pic;

    public String getTipees_name() {
        return tipees_name;
    }

    public void setTipees_name(String tipees_name) {
        this.tipees_name = tipees_name;
    }


    public int getIs_supervisor() {
        return is_supervisor;
    }

    public void setIs_supervisor(int is_supervisor) {
        this.is_supervisor = is_supervisor;
    }

    public int getGet_tournamenttip() {
        return get_tournamenttip;
    }

    public void setGet_tournamenttip(int get_tournamenttip) {
        this.get_tournamenttip = get_tournamenttip;
    }

    public int getGet_tips() {
        return get_tips;
    }

    public void setGet_tips(int get_tips) {
        this.get_tips = get_tips;
    }

    public String getStartday() {
        return startday;
    }

    public void setStartday(String startday) {
        this.startday = startday;
    }

    public String getHoliday_pay() {
        return holiday_pay;
    }

    public void setHoliday_pay(String holiday_pay) {
        this.holiday_pay = holiday_pay;
    }

    public int getIs_active() {
        return is_active;
    }

    public void setIs_active(int is_active) {
        this.is_active = is_active;
    }


    public String getProfile_id() {
        return profile_id;
    }

    public void setProfile_id(String profile_id) {
        this.profile_id = profile_id;
    }

    public String getProfile_name() {
        return profile_name;
    }

    public void setProfile_name(String profile_name) {
        this.profile_name = profile_name;
    }

    public String getPay_period() {
        return pay_period;
    }

    public void setPay_period(String pay_period) {
        this.pay_period = pay_period;
    }

    public String getHourly_pay() {
        return hourly_pay;
    }

    public void setHourly_pay(String hourly_pay) {
        this.hourly_pay = hourly_pay;
    }


}
