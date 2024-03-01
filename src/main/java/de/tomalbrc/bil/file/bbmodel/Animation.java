package de.tomalbrc.bil.file.bbmodel;

import com.google.gson.annotations.SerializedName;

import java.util.Map;
import java.util.UUID;

public class Animation {
    private UUID uuid;
    private String name;
    private String loop;
    private boolean override;
    private int length;
    private int snapping;
    private boolean selected;
    @SerializedName("anim_time_update")
    private String animTimeUpdate;
    @SerializedName("blend_weight")
    private String blendWeight;
    @SerializedName("start_delay")
    private String startDelay;
    @SerializedName("loop_delay")
    private String loopDelay;
    private Map<UUID, Animator> animators;
}