package com.moallem.stu.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Subject implements Parcelable{

    private String name;
    private String image;
    private String key;
    private String arabicName;

    public Subject(){

    }

    protected Subject(Parcel in) {
        name = in.readString();
        image = in.readString();
        key = in.readString();
        arabicName = in.readString();
    }

    public static final Creator<Subject> CREATOR = new Creator<Subject>() {
        @Override
        public Subject createFromParcel(Parcel in) {
            return new Subject(in);
        }

        @Override
        public Subject[] newArray(int size) {
            return new Subject[size];
        }
    };

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {

        return key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setArabicName(String arabicName) {
        this.arabicName = arabicName;
    }

    public String getArabicName() {

        return arabicName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(image);
        dest.writeString(key);
        dest.writeString(arabicName);
    }
}