package com.mayanksharma.homework.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Photo implements Parcelable{
    private String description;
    private String classes;
    private String section;
    private String date_created;
    private String image_path;
    private String photo_id;
    private String user_id;

    public Photo() {

    }

    public Photo(String description, String classes, String section,
                 String date_created, String image_path, String photo_id, String user_id) {
        this.description = description;
        this.classes = classes;
        this.section = section;
        this.date_created = date_created;
        this.image_path = image_path;
        this.photo_id = photo_id;
        this.user_id = user_id;
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

    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

    public String getPhoto_id() {
        return photo_id;
    }

    public void setPhoto_id(String photo_id) {
        this.photo_id = photo_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    @Override
    public String toString() {
        return "Photo{" +
                "description='" + description + '\'' +
                ", classes='" + classes + '\'' +
                ", section='" + section + '\'' +
                ", date_created='" + date_created + '\'' +
                ", image_path='" + image_path + '\'' +
                ", photo_id='" + photo_id + '\'' +
                ", user_id='" + user_id + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(description);
        dest.writeString(classes);
        dest.writeString(section);
        dest.writeString(date_created);
        dest.writeString(image_path);
        dest.writeString(photo_id);
        dest.writeString(user_id);
    }

    protected Photo(Parcel in) {
        description = in.readString();
        classes = in.readString();
        section = in.readString();
        date_created = in.readString();
        image_path = in.readString();
        photo_id = in.readString();
        user_id = in.readString();
    }

    public static final Creator<Photo> CREATOR = new Creator<Photo>() {
        @Override
        public Photo createFromParcel(Parcel in) {
            return new Photo(in);
        }

        @Override
        public Photo[] newArray(int size) {
            return new Photo[size];
        }
    };

    public static Creator<Photo> getCREATOR() {
        return CREATOR;
    }

}
