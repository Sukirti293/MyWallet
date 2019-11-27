package com.sukirti.mywallettracker.RegistrationInfo;

import java.util.HashMap;
import java.util.Map;

public class User {

    private String userID;
    private String email;
    private int monthlyBudget;
    private Map<String, Object> userSpendings;


    public User(){}


    public User(String userID, String email, int monthlyBudget) {
        this.userID = userID;
        this.email = email;
        this.monthlyBudget = monthlyBudget;
        this.userSpendings =  new HashMap<>();
    }

    public User(String userID, String email, int monthlyBudget, Map<String,Object> userSpendings) {
        this.userID = userID;
        this.email = email;
        this.monthlyBudget = monthlyBudget;
        this.userSpendings = userSpendings;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public int getMonthlyBudget() {
        return monthlyBudget;
    }

    public void setMonthlyBudget(int monthlyBudget) {
        this.monthlyBudget = monthlyBudget;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserID() {
        return userID;
    }

    public Map<String, Object> getUserSpendings() {
        return userSpendings;
    }

    public void setUserSpendings(Map<String, Object> userSpendings) {
        this.userSpendings = userSpendings;
    }

    @Override
    public String toString() {
        return "User{" +
                "userID='" + userID + '\'' +
                ", email='" + email + '\'' +
                ", monthlyBudget=" + monthlyBudget +
                ", userSpendings=" + userSpendings +
                '}';
    }
}
