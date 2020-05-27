package com.team19.rentmicroservice.security;

public class CustomPrincipal {

    private String permissions;
    private String userID;
    private String token;

    public CustomPrincipal(String permissions, String userID, String token){
        this.permissions = permissions;
        this.userID = userID;
        this.token = token;
    }

    public String getPermissions() {
        return permissions;
    }

    public void setPermissions(String permissions) {
        this.permissions = permissions;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
