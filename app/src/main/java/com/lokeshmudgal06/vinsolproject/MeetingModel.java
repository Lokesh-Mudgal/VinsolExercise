package com.lokeshmudgal06.vinsolproject;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MeetingModel implements Parcelable {
    @SerializedName("start_time")
    @Expose
    private String startTime;
    @SerializedName("end_time")
    @Expose
    private String endTime;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("participants")
    @Expose
    private List<String> participants = null;

    protected MeetingModel(Parcel in) {
        startTime = in.readString();
        endTime = in.readString();
        description = in.readString();
        participants = in.createStringArrayList();
    }

    public static final Creator<MeetingModel> CREATOR = new Creator<MeetingModel>() {
        @Override
        public MeetingModel createFromParcel(Parcel in) {
            return new MeetingModel(in);
        }

        @Override
        public MeetingModel[] newArray(int size) {
            return new MeetingModel[size];
        }
    };

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getParticipants() {
        return participants;
    }

    public void setParticipants(List<String> participants) {
        this.participants = participants;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(startTime);
        dest.writeString(endTime);
        dest.writeString(description);
        dest.writeStringList(participants);
    }
}
