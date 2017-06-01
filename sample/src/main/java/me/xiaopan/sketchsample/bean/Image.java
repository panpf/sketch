package me.xiaopan.sketchsample.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class Image implements Parcelable{
    public String regularUrl;
    public String highDefinitionUrl;

    public Image(String regularUrl, String highDefinitionUrl) {
        this.regularUrl = regularUrl;
        this.highDefinitionUrl = highDefinitionUrl;
    }

    protected Image(Parcel in) {
        regularUrl = in.readString();
        highDefinitionUrl = in.readString();
    }

    public static final Creator<Image> CREATOR = new Creator<Image>() {
        @Override
        public Image createFromParcel(Parcel in) {
            return new Image(in);
        }

        @Override
        public Image[] newArray(int size) {
            return new Image[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(regularUrl);
        dest.writeString(highDefinitionUrl);
    }
}
