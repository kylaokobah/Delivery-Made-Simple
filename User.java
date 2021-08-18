package com.example.dmsimpledriver;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class User {
    private String fullname;
    private String email;
    private String phone;
    private String password;
    private String address;
    private String ID;
    String Prefs = "myPrefs";
    public User(){
        this.fullname = null;
        this.email = null;
        this.phone= "000-000-0000";
        this.password = null;
        this.address = null;
        this.ID = "";
    }
    public User( String fname, String email, String phone_number, String pass, String addrss){
        this.fullname = fname;
        this.email = email;
        this.phone= phone_number;
        this.password = pass;
        this.address = addrss;
    }

    public String getFullName(){
        return this.fullname;
    }
    public String getEmail(){
        return this.email;
    }
    public String getPassword(){
        return this.password;
    }
    public String getAddress(){
        return this.address;
    }
    public String getPhone(){
        return this.phone;
    }

    //Setters


    public void setFirstname(String fname){
        this.fullname = fname;
    }
    public void setEmail(String eml){
        this.email = eml;
    }
    public void setPassword(String pass){
        this.password = pass;
    }
    public void setAddress(String addr){
        this.address = addr;
    }
    public void setPhone(String phone_num){
        this.phone = phone_num;
    }
    public void setId(String new_id){
        this.ID = new_id;
    }

    public User getUser(Context context){
        SharedPreferences sharedPref = context.getSharedPreferences(
                Prefs, Context.MODE_PRIVATE);
        String user_string =  sharedPref.getString("user", null);
        if (user_string != null){
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.setDateFormat("M/d/yy hh:mm a");
            Gson gson = gsonBuilder.create();
            User user = gson.fromJson(user_string, User.class);
            Log.d("User", "saved user is: "+user.email);
            return user;
        }
//        User user = new User(this.fullname, this.email, this.phone, this.password, this.address);
        return null;
    }

    public void saveUser(Context context, User user){
        SharedPreferences sharedPref = context.getSharedPreferences(
                Prefs, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPref.edit();
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat("M/d/yy hh:mm a");
        Gson gson = gsonBuilder.create();
        String json = gson.toJson(user);
        editor.putString("user", json);
        editor.apply();

    }


    public String getNotificationToken(Context context){
        SharedPreferences sharedPref = context.getSharedPreferences(
                Prefs, Context.MODE_PRIVATE);
        String token =  sharedPref.getString("notification_token", null);
        if (token != null){
            return token;
        }
        return null;
    }
    public void saveNotificationToken(Context context, String token){
        SharedPreferences sharedPref = context.getSharedPreferences(
                Prefs, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("notification_token", token);
        editor.apply();

    }

}
