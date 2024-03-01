package de.tomalbrc.bil.file.bbmodel;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;
import java.util.UUID;

class Keyframe {
    private String channel;
    @SerializedName("data_points")
    private List<Map<String, String>> dataPoints;
    private UUID uuid;
    private int time;
    private int color;
    private String interpolation;
    @SerializedName("bezier_linked")
    private boolean bezierLinked;
    @SerializedName("bezier_left_time")
    private List<Double> bezierLeftTime;
    @SerializedName("bezier_left_value")
    private List<Double> bezierLeftValue;
    @SerializedName("bezier_right_time")
    private List<Double> bezierRightTime;
    @SerializedName("bezier_right_value")
    private List<Double> bezierRightValue;
}