package com.mayanksharma.homework.models;

public class UserAccountSettings {
    private String description;
    private String display_name;
    private String profile_photo;
    private String status;
    private String username;
    private String profile;
    private String user_id;

    public UserAccountSettings(String description, String display_name,
                               String profile_photo, String status, String username, String profile, String user_id) {
        this.description = description;
        this.display_name = display_name;
        this.profile_photo = profile_photo;
        this.status = status;
        this.username = username;
        this.profile = profile;
        this.user_id = user_id;
    }

    public UserAccountSettings() {

    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public String getProfile_photo() {
        return profile_photo;
    }

    public void setProfile_photo(String profile_photo) {
        this.profile_photo = profile_photo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    @Override
    public String toString() {
        return "UserAccountSettings{" +
                "description='" + description + '\'' +
                ", display_name='" + display_name + '\'' +
                ", profile_photo='" + profile_photo + '\'' +
                ", status='" + status + '\'' +
                ", username='" + username + '\'' +
                ", profile='" + profile + '\'' +
                '}';
    }
}
