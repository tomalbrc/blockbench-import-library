package de.tomalbrc.bil.file.bbmodel;

import com.google.gson.annotations.SerializedName;
import net.minecraft.world.phys.Vec3;

import java.util.Map;
import java.util.UUID;

public class Element {
    private String name;
    @SerializedName("box_uv")
    private boolean boxUv;
    private boolean rescale;
    private boolean locked;
    @SerializedName("render_order")
    private String renderOrder;
    @SerializedName("allow_mirror_modeling")
    private boolean allowMirrorModeling;
    private Vec3 from;
    private Vec3 to;
    private int autouv;
    private int color;
    private Vec3 origin;
    private Map<String, Face> faces;
    private String type;
    private UUID uuid;
}
