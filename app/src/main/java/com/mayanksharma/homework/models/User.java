package com.mayanksharma.homework.models;

public class User {
    private String username;
    private long phone_number;
    private String user_id;
    private String email;
    private String profile;
    private String classes;
    private String section;

    public User(String username, long phone_number, String user_id, String email, String profile, String classes, String section) {
        this.username = username;
        this.phone_number = phone_number;
        this.user_id = user_id;
        this.email = email;
        this.profile = profile;
        this.classes = classes;
        this.section = section;
    }

    public User() {
    }

    public String getClasses() {
        return classes;
    }

    public void setClasses(String classes) {
        this.classes = classes;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(long phone_number) {
        this.phone_number = phone_number;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", phone_number=" + phone_number +
                ", user_id='" + user_id + '\'' +
                ", email='" + email + '\'' +
                ", profile='" + profile + '\'' +
                ", classes=" + classes +
                ", section=" + section +
                '}';
    }
}
