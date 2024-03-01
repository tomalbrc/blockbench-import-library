package de.tomalbrc.bil.file.bbmodel;

import com.google.gson.annotations.SerializedName;
import org.joml.Vector3f;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Element {
    public String name;
    @SerializedName("box_uv")
    public boolean boxUv;
    public boolean rescale;
    public boolean locked;
    @SerializedName("render_order")
    public String renderOrder;
    @SerializedName("allow_mirror_modeling")
    public boolean allowMirrorModeling;
    public List<Double> from;
    public List<Double> to;
    public int autouv;
    public int color;
    public Vector3f origin;
    public Map<String, Face> faces;
    public String type;
    public UUID uuid;
}
