package com.mytips.Model;

import java.io.Serializable;

/**
 * Created by Beesolver on 12-10-2017.
 */

public class AddDay implements Serializable {

    String profile;
    String start_shift;
    String check_in;
    String end_shift;
    String check_out;
    String calculated_hours;
    int isHolidaypay;
    String total_tips;
    String tip_out_tipees;
    String tip_out_percentage;
    String tip_out;
    String tounament_count;
    String tournament_perday;
    String total_tournament_downs;

    public String getTotal_earnings() {
        return total_earnings;
    }

    public void setTotal_earnings(String total_earnings) {
        this.total_earnings = total_earnings;
    }

    String total_earnings;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    String id;

    public String getWages_hourly() {
        return wages_hourly;
    }

    public void setWages_hourly(String wages_hourly) {
        this.wages_hourly = wages_hourly;
    }

    String wages_hourly;

    public int getDay_off() {
        return day_off;
    }

    public void setDay_off(int day_off) {
        this.day_off = day_off;
    }

    int day_off;

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getStart_shift() {
        return start_shift;
    }

    public void setStart_shift(String start_shift) {
        this.start_shift = start_shift;
    }

    public String getCheck_in() {
        return check_in;
    }

    public void setCheck_in(String check_in) {
        this.check_in = check_in;
    }

    public String getEnd_shift() {
        return end_shift;
    }

    public void setEnd_shift(String end_shift) {
        this.end_shift = end_shift;
    }

    public String getCheck_out() {
        return check_out;
    }

    public void setCheck_out(String check_out) {
        this.check_out = check_out;
    }

    public String getCalculated_hours() {
        return calculated_hours;
    }

    public void setCalculated_hours(String calculated_hours) {
        this.calculated_hours = calculated_hours;
    }

    public int getIsHolidaypay() {
        return isHolidaypay;
    }

    public void setIsHolidaypay(int isHolidaypay) {
        this.isHolidaypay = isHolidaypay;
    }

    public String getTotal_tips() {
        return total_tips;
    }

    public void setTotal_tips(String total_tips) {
        this.total_tips = total_tips;
    }

    public String getTip_out_tipees() {
        return tip_out_tipees;
    }

    public void setTip_out_tipees(String tip_out_tipees) {
        this.tip_out_tipees = tip_out_tipees;
    }

    public String getTip_out_percentage() {
        return tip_out_percentage;
    }

    public void setTip_out_percentage(String tip_out_percentage) {
        this.tip_out_percentage = tip_out_percentage;
    }

    public String getTip_out() {
        return tip_out;
    }

    public void setTip_out(String tip_out) {
        this.tip_out = tip_out;
    }

    public String getTounament_count() {
        return tounament_count;
    }

    public void setTounament_count(String tounament_count) {
        this.tounament_count = tounament_count;
    }

    public String getTournament_perday() {
        return tournament_perday;
    }

    public void setTournament_perday(String tournament_perday) {
        this.tournament_perday = tournament_perday;
    }

    public String getTotal_tournament_downs() {
        return total_tournament_downs;
    }

    public void setTotal_tournament_downs(String total_tournament_downs) {
        this.total_tournament_downs = total_tournament_downs;
    }


}