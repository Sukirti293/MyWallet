package com.sukirti.mywallettracker.Spendings;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class UserSpendings {

    //private String email;
    private int clothes = 0;
    private int food = 0;
    private int entertainment = 0;
    private int education = 0;
    private int necessities = 0;
    private int extras = 0;
    //private Date date = Calendar.getInstance().getTime();
   // private String userID;


    public void setClothes(int clothes) {
        this.clothes = clothes;
    }

    public void setFood(int food) {
        this.food = food;
    }

    public void setEntertainment(int entertainment) {
        this.entertainment = entertainment;
    }

    public void setEducation(int education) {
        this.education = education;
    }

    public void setNecessities(int necessities) {
        this.necessities = necessities;
    }


    public void setExtras(int extras) {
        this.extras = extras;
    }



    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("clothes", clothes);
        result.put("food", food);
        result.put("entertainment", entertainment);
        result.put("education", education);
        result.put("necessities", necessities);
        result.put("extras", extras);
        return result;
    }


    public int getClothes() {
        return clothes;
    }

    public int getFood() {
        return food;
    }

    public int getEntertainment() {
        return entertainment;
    }

    public int getEducation() {
        return education;
    }

    public int getNecessities() {
        return necessities;
    }

    public int getExtras() {
        return extras;
    }

    public int totalSpendings(){
        return clothes+food+entertainment+education+necessities+extras;
    }

    @Override
    public String toString() {
        return "UserSpendings{" +
                "clothes=" + clothes +
                ", food=" + food +
                ", entertainment=" + entertainment +
                ", education=" + education +
                ", necessities=" + necessities +
                ", extras=" + extras +
                '}';
    }
}
