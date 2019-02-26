package com.mayanksharma.homework.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Text implements Parcelable{
    private String title;
    private String description;
    private String classes;
    private String section;
    private String date_created;
    private String user_id;


    public Text() {
    }


    public Text(String title, String description, String classes, String section, String date_created, String user_id) {
        this.title = title;
        this.description = description;
        this.classes = classes;
        this.section = section;
        this.date_created = date_created;
        this.user_id = user_id;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getDate_created() {
        return date_created;
    }

    public void setDate_created(String date_created) {
        this.date_created = date_created;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    @Override
    public String toString() {
        return "Text{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", classes='" + classes + '\'' +
                ", section='" + section + '\'' +
                ", date_created='" + date_created + '\'' +
                ", user_id='" + user_id + '\'' +
                '}';
    } @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(description);
        dest.writeString(classes);
        dest.writeString(section);
        dest.writeString(date_created);
        dest.writeString(title);
        dest.writeString(user_id);
    }

    protected Text(Parcel in) {
        description = in.readString();
        classes = in.readString();
        section = in.readString();
        date_created = in.readString();
        title = in.readString();
        user_id = in.readString();
    }

    public static final Creator<Text> CREATOR = new Creator<Text>() {
        @Override
        public Text createFromParcel(Parcel in) {
            return new Text(in);
        }

        @Override
        public Text[] newArray(int size) {
            return new Text[size];
        }
    };

    public static Creator<Text> getCREATOR() {
        return CREATOR;
    }

}