package de.tomalbrc.bil.file.bbmodel;

import com.google.gson.annotations.SerializedName;

import java.util.Map;
import java.util.UUID;

public class BbAnimation {
    public UUID uuid;
    public String name;
    public LoopMode loop;
    public boolean override;
    public float length;
    public float snapping;
    @SerializedName("start_delay")
    public String startDelay;
    @SerializedName("loop_delay")
    public String loopDelay;

    public Map<UUID, BbAnimator> animators;

    public enum LoopMode {
        @SerializedName("once")
        ONCE,
        @SerializedName("hold")
        HOLD,
        @SerializedName("loop")
        LOOP
    }
}