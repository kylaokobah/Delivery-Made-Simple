package com.example.dmsimpledriver;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Ride {
    private String current_driver_id;

    public String getUser_phone() {
        return this.user_phone;
    }

    public void setUser_phone(String user_phone) {
        this.user_phone = user_phone;
    }

    private String user_phone = null;

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    private String user_id;

    private Map <String, String> drop_off_location;

    public String getCurrent_driver_id() {
        return current_driver_id;
    }

    public void setCurrent_driver_id(String current_driver_id) {
        this.current_driver_id = current_driver_id;
    }

    public Map<String, String> getDrop_off_location() {
        return drop_off_location;
    }

    public void setDrop_off_location(Map<String, String> drop_off_location) {
        this.drop_off_location = drop_off_location;
    }

    public Map<String, String> getPick_up_location() {
        return pick_up_location;
    }

    public void setPick_up_location(Map<String, String> pick_up_location) {
        this.pick_up_location = pick_up_location;
    }

    public ArrayList<String>  getRejected_drivers_id() {
        return rejected_drivers_id;
    }

    public void setRejected_drivers_id(ArrayList<String> rejected_drivers_id) {
        this.rejected_drivers_id = rejected_drivers_id;
    }

    private  Map <String, String>  pick_up_location;
    private ArrayList<String> rejected_drivers_id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String id;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    private String status;

    String Prefs = "myPrefs";
    public Ride(){
        this.current_driver_id = "";
        this.drop_off_location = new HashMap<>();
        this.pick_up_location = new HashMap<>();
        this.rejected_drivers_id = new ArrayList<String> ();
        this.user_id = "";
        this.id = "";
    }
    public Ride(String id,String user_id,   Map<String, String>  pick_up_location_number, Map<String, String>  drop_off_location,ArrayList<String>  rejected_drivers_id,
                String current_driver_id, String status, String User_phone){
       this.id = id;
        this.current_driver_id = current_driver_id;
        this.drop_off_location = drop_off_location;
        this.pick_up_location= pick_up_location_number;
        this.rejected_drivers_id = rejected_drivers_id;
        this.user_id = user_id;
        this.status = status;
        this.user_phone = User_phone;
    }

    public Ride getRide(Context context){
        SharedPreferences sharedPref = context.getSharedPreferences(
                Prefs, Context.MODE_PRIVATE);
        String user_string =  sharedPref.getString("ride", null);
        if (user_string != null){
            Log.d("Ride", "saved user is: "+user_string);

            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.setDateFormat("M/d/yy hh:mm a");
            Gson gson = gsonBuilder.create();
            Ride ride = gson.fromJson(user_string, Ride.class);
            Log.d("Ride", "saved user is: "+ride);
            return ride;
        }
//        Ride user = new Ride(this.current_driver_id, this.drop_off_location, this.pick_up_location, this.rejected_drivers_id, this.address);
        return null;
    }

    public void saveRide(Context context, Ride user){

        SharedPreferences sharedPref = context.getSharedPreferences(
                Prefs, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPref.edit();
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat("M/d/yy hh:mm a");
        Gson gson = gsonBuilder.create();
        String json = gson.toJson(user);
        editor.putString("ride", json);
        editor.apply();

    }

}
