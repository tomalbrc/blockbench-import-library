package de.tomalbrc.bil.file.bbmodel;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BbAnimator {
    public String name;
    public Type type;
    public List<BbKeyframe> keyframes = List.of();

    public enum Type {
        @SerializedName("bone")
        BONE,
        @SerializedName("effect")
        EFFECT
    }
}
