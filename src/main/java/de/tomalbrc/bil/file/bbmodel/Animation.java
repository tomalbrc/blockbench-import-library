package de.tomalbrc.bil.file.bbmodel;

import com.google.gson.annotations.SerializedName;

import java.util.Map;
import java.util.UUID;

public class Animation {
    public UUID uuid;
    public String name;
    public String loop;
    public boolean override;
    public int length;
    public int snapping;
    public boolean selected;
    @SerializedName("anim_time_update")
    public String animTimeUpdate;
    @SerializedName("blend_weight")
    public String blendWeight;
    @SerializedName("start_delay")
    public String startDelay;
    @SerializedName("loop_delay")
    public String loopDelay;
    public Map<UUID, Animator> animators;
}