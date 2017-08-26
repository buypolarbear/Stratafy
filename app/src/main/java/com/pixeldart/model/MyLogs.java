package com.pixeldart.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by cn on 8/25/2017.
 */

public class MyLogs implements Parcelable {
    private String id, userId, buildingId, logCategoryId, title, description, status, privates,
                   archived_date, logCategoryName, attachments, created;

    public MyLogs(){}


    protected MyLogs(Parcel in) {
        id = in.readString();
        userId = in.readString();
        buildingId = in.readString();
        logCategoryId = in.readString();
        title = in.readString();
        description = in.readString();
        status = in.readString();
        privates = in.readString();
        archived_date = in.readString();
        logCategoryName = in.readString();
        attachments = in.readString();
        created = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(userId);
        dest.writeString(buildingId);
        dest.writeString(logCategoryId);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(status);
        dest.writeString(privates);
        dest.writeString(archived_date);
        dest.writeString(logCategoryName);
        dest.writeString(attachments);
        dest.writeString(created);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MyLogs> CREATOR = new Creator<MyLogs>() {
        @Override
        public MyLogs createFromParcel(Parcel in) {
            return new MyLogs(in);
        }

        @Override
        public MyLogs[] newArray(int size) {
            return new MyLogs[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getBuildingId() {
        return buildingId;
    }

    public void setBuildingId(String buildingId) {
        this.buildingId = buildingId;
    }

    public String getLogCategoryId() {
        return logCategoryId;
    }

    public void setLogCategoryId(String logCategoryId) {
        this.logCategoryId = logCategoryId;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPrivates() {
        return privates;
    }

    public void setPrivates(String privates) {
        this.privates = privates;
    }

    public String getArchived_date() {
        return archived_date;
    }

    public void setArchived_date(String archived_date) {
        this.archived_date = archived_date;
    }

    public String getLogCategoryName() {
        return logCategoryName;
    }

    public void setLogCategoryName(String logCategoryName) {
        this.logCategoryName = logCategoryName;
    }

    public String getAttachments() {
        return attachments;
    }

    public void setAttachments(String attachments) {
        this.attachments = attachments;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }
}
