package com.stratafy.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by cn on 10/11/2017.
 */

public class AroundPlaces implements Parcelable {
    String id, category, offer, terms_condition, placename, address, latitude, longitude, distance, phone,
            image, time, website, openinghour;

    public AroundPlaces(){}

    protected AroundPlaces(Parcel in) {
        id = in.readString();
        category = in.readString();
        offer = in.readString();
        terms_condition = in.readString();
        placename = in.readString();
        address = in.readString();
        latitude = in.readString();
        longitude = in.readString();
        distance = in.readString();
        phone = in.readString();
        image = in.readString();
        time = in.readString();
        website = in.readString();
        openinghour = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(category);
        dest.writeString(offer);
        dest.writeString(terms_condition);
        dest.writeString(placename);
        dest.writeString(address);
        dest.writeString(latitude);
        dest.writeString(longitude);
        dest.writeString(distance);
        dest.writeString(phone);
        dest.writeString(image);
        dest.writeString(time);
        dest.writeString(website);
        dest.writeString(openinghour);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<AroundPlaces> CREATOR = new Creator<AroundPlaces>() {
        @Override
        public AroundPlaces createFromParcel(Parcel in) {
            return new AroundPlaces(in);
        }

        @Override
        public AroundPlaces[] newArray(int size) {
            return new AroundPlaces[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getOffer() {
        return offer;
    }

    public void setOffer(String offer) {
        this.offer = offer;
    }

    public String getTerms_condition() {
        return terms_condition;
    }

    public void setTerms_condition(String terms_condition) {
        this.terms_condition = terms_condition;
    }

    public String getPlacename() {
        return placename;
    }

    public void setPlacename(String placename) {
        this.placename = placename;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getOpeninghour() {
        return openinghour;
    }

    public void setOpeninghour(String openinghour) {
        this.openinghour = openinghour;
    }
}
