package de.tomalbrc.bil.file.bbmodel;

import com.google.gson.annotations.SerializedName;
import org.joml.Vector2i;
import org.joml.Vector3f;

import java.util.Map;
import java.util.UUID;

public class BbElement {
    public String name;
    @SerializedName("box_uv")
    public boolean boxUv;
    @SerializedName("uv_offset")
    public Vector2i uvOffset;

    public boolean rescale;
    public boolean locked;


    public Vector3f from;
    public Vector3f to;

    public Vector3f rotation;
    public int autouv;
    public int color;
    public float inflate;
    public Vector3f origin;
    public Map<String, BbFace> faces;
    public String type;
    public UUID uuid;
}
