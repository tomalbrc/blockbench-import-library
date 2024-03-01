package de.tomalbrc.bil.file.bbmodel;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Keyframe {
    public Channel channel;
    @SerializedName("data_points")
    public List<Map<String, Float>> dataPoints;

    public UUID uuid;
    public int time;
    public int color;
    public String interpolation;
    @SerializedName("bezier_linked")
    public boolean bezierLinked;
    @SerializedName("bezier_left_time")
    public List<Double> bezierLeftTime;
    @SerializedName("bezier_left_value")
    public List<Double> bezierLeftValue;
    @SerializedName("bezier_right_time")
    public List<Double> bezierRightTime;
    @SerializedName("bezier_right_value")
    public List<Double> bezierRightValue;

    public enum Channel {
        position,
        rotation
    }
}